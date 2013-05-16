package fr.utc.assos.payutc;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fr.utc.assos.payutc.api.AdditionalKeyStoresSSLSocketFactory;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.POSS;

/**
 * Demande au seller de badger
 * @author thomas
 *
 */
public class PaulineActivity extends BaseActivity {
	public static final String LOG_TAG			= "PaulineActivity";
	
	/** Sera remplie en lisant la config */
	public static String API_URL;
	/** Id du point de vente */
	public static int POI_ID = -1;
	
	/** Cas service, sera remplie en lisant la config */
	public static String CAS_SERVICE;
	
	/** POSS Client */
	public static POSS POSS;
	
	/** Image cache */
	public static ImageCache imageCache;
	
	/** ID des Activity */
	public static final int CASWEBVIEW	= 0;
	public static final int HOMEACTIVITY = 1;
	
	/* url du CAS, sera remplie en appellanc POSS.getCasUrl */
	private static String _CAS_URL		= null; 
	
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PaulineActivity");
        setContentView(R.layout.main);
        
        HttpsURLConnection.setDefaultSSLSocketFactory(createAdditionalCertsSSLSocketFactory());
        HostnameVerifier v = new HostnameVerifier() {
        	public boolean verify(String hostname, SSLSession session) {
        		return true;
        	}
        };
        HttpsURLConnection.setDefaultHostnameVerifier(v);
        /*SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", createAdditionalCertsSSLSocketFactory(), 443));
        */
        //PBUY = new PBuy(API_HOST, API_PATH, API_NAMESPACE, API_SSL);
        
        boolean configOk = false;
        try {
        	loadConfig();
        	configOk = true;
        }
        catch (Exception e) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("Impossible de charger la config")
        		   .setMessage(e.getMessage())
        	       .setCancelable(false)
        	       .setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
       	                	finish();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }

        if (configOk) {
	        POSS = new POSS(API_URL);
	        (new GetCasUrlTask()).execute();
        }
        
        imageCache = new ImageCache(getCacheDir());
        
        
        // decomment pour aller directement au home sans se logger
        //startHomeActivity();
    }
    
    protected void loadConfig() throws Exception {
    	String poi_id = null;
    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xrp = factory.newPullParser();
        
        xrp.setInput(getResources().openRawResource(R.raw.config), "UTF-8");
        
		while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
			 
		    if (xrp.getEventType() == XmlResourceParser.START_TAG) {

		            String s = xrp.getName();
		            Log.i(LOG_TAG, "name "+s);
		            if (s.equals("config")) {
		                API_URL = xrp.getAttributeValue(null, "api_url");
		                CAS_SERVICE = xrp.getAttributeValue(null, "cas_service");
		                poi_id = xrp.getAttributeValue(null, "poi_id");
		            }
		    }
		    xrp.next();
		}
        if (API_URL==null) { throw new Exception("Config pour api_url introuvable"); }
        if (CAS_SERVICE==null) { throw new Exception("Config pour cas_service introuvable"); }
        if (poi_id==null) { throw new Exception("Config pour poi_id introuvable"); }
        try {
        	POI_ID = Integer.parseInt(poi_id);
        }
        catch (Exception e) {
        	throw new Exception("Format poi_id incorrect : "+e.getMessage());
        }
    }

    public void onClickLogin(View _view) {
    	LogByCas();
    	//new LoadPosTask("42","24").execute();
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode);
		switch (requestCode) {
			case CASWEBVIEW:
				if (resultCode == RESULT_OK) {
					String ticket = data.getStringExtra("ticket");
					Log.i(LOG_TAG, "ticket : "+ticket);
					new LoadPosTask(ticket, CAS_SERVICE).execute();
				}
			break;
			default: break;
		}
    }

    public void LogByCas() {
    	Log.d(LOG_TAG,"startCasWebView");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.CasWebView.class);
    	Bundle b = new Bundle();
    	b.putString("casurl", _CAS_URL);
    	intent.putExtras(b);
    	startActivityForResult(intent, CASWEBVIEW);
    	//new LoadPosTask("42","24").execute(); 
    }
    
    public void startHomeActivity() {
    	Log.d(LOG_TAG,"startHomeActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
    	startActivityForResult(intent,HOMEACTIVITY);
    }
    
    protected SSLSocketFactory createAdditionalCertsSSLSocketFactory() {
        try {
            final KeyStore ks = KeyStore.getInstance("BKS");

            // the bks file we generated above
            final InputStream in = getApplicationContext().getResources().openRawResource( R.raw.mystore);  
            try {
                // don't forget to put the password used above in strings.xml/mystore_password
                ks.load(in, getApplicationContext().getString( R.string.mystore_password ).toCharArray());
            } finally {
                in.close();
            }

            return new AdditionalKeyStoresSSLSocketFactory(ks);
            
            /*TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, tmf.getTrustManagers(), null);
            return context.getSocketFactory();*/

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
        
        
    }
    
    protected void onGetCasUrlFails(Exception e) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Echec de la conexion avec le serveur")
    		   .setMessage(e.getMessage())
    	       .setCancelable(false)
    	       .setPositiveButton("Encore !", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   dialog.cancel();
    	        	   new GetCasUrlTask().execute();
    	           }
    	       })
    	       .setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	                	PaulineActivity.this.finish();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private class GetCasUrlTask extends ApiTask<Integer, Integer, Integer> {
    	private String mUrl;

    	public GetCasUrlTask() {
			super("Synchronization", PaulineActivity.this, 
					"Synchronization avec le serveur en cours...");
		}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		//mUrl = PBUY.getCasUrl();
    		mUrl = POSS.getCasUrl();
    		return mUrl!=null;
    	}
        

        @Override
        protected void onPostExecute(Integer osef) {
    		super.onPostExecute(osef);
        	if (mUrl==null) {
        		onGetCasUrlFails(lastException);
        	}
        	_CAS_URL = mUrl;
        }
    }

    private class LoadPosTask extends ApiTask<Integer, Integer, Integer> {
    	private String mTicket, mService;
    	private boolean mLoaded;
    	
    	public LoadPosTask(String ticket, String service) {
    		super("Identification", PaulineActivity.this, 
    				"Connection au serveur en cour...");
    		mTicket = ticket;
    		mService = service;
    		mLoaded = false;
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		mLoaded = POSS.loadPos(mTicket, mService, POI_ID);
    		return true;
    	}

        @Override
        protected void onPostExecute(Integer osef) {
        	super.onPostExecute(osef);
        	if (mLoaded) {
        		startHomeActivity();
        	}
        	else {
        		AlertDialog.Builder builder = new AlertDialog.Builder(PaulineActivity.this);
        		builder.setTitle("Echec de l'identification")
        			.setMessage(lastException.getMessage())
        			.setNegativeButton("J'ai compris", new DialogInterface.OnClickListener() {
        		           public void onClick(DialogInterface dialog, int id) {
        		                dialog.cancel();
        		           }});
        		builder.create().show();
        	}
        }
    }

}