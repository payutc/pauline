package fr.utc.assos.payutc;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
	public static String POSS_API_URL;
	
	public static String KEY_API_URL;
	
	/** Cas service, sera remplie en lisant la config */
	public static String CAS_SERVICE = "http://localhost";
	
	/** POSS Client */
	public static POSS POSS;
	
	/** Image cache */
	public static ImageCache imageCache;
	
	/** ID des Activity */
	public static final int CASWEBVIEW	= 0;
	public static final int HOME_ACTIVITY = 1;
	public static final int SETUP_APP_ACTIVITY = 2;
	
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
	        POSS = new POSS(POSS_API_URL);
	        (new GetCasUrlTask()).execute();
        }
        
        imageCache = new ImageCache(getCacheDir());
        
        // uncomment to reset application key and name
        //resetStore(this);
        
        
        // decomment pour aller directement au home sans se logger
        //startHomeActivity();
    }
    
    protected void loadConfig() throws Exception {
    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xrp = factory.newPullParser();
        
        xrp.setInput(getResources().openRawResource(R.raw.config), "UTF-8");
        
		while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
			 
		    if (xrp.getEventType() == XmlResourceParser.START_TAG) {

		            String s = xrp.getName();
		            Log.i(LOG_TAG, "name "+s);
		            if (s.equals("config")) {
		                POSS_API_URL = xrp.getAttributeValue(null, "poss_api_url");
		                KEY_API_URL = xrp.getAttributeValue(null, "key_api_url");
		            }
		    }
		    xrp.next();
		}
        if (POSS_API_URL==null) { throw new Exception("Config pour poss_api_url introuvable"); }
        if (KEY_API_URL==null) { throw new Exception("Config pour key_api_url introuvable"); }
    }

    public void onClickLogin(View _view) {
    	// COMMENT TO LOGIN DIRECTLY (with faux-cas for example)
    	LogByCas();
    	// UNCOMMENT TO LOGIN DIRECTLY (with faux-cas for example)
    	//new LoginCasTask("trecouvr@POSS3","POSS3").execute();
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode);
		switch (requestCode) {
			case CASWEBVIEW:
				if (resultCode == RESULT_OK) {
					String ticket = data.getStringExtra("ticket");
					Log.i(LOG_TAG, "ticket : "+ticket);
					new LoginCasTask(ticket, CAS_SERVICE).execute();
				}
			break;
			case SETUP_APP_ACTIVITY:
				if (resultCode == RESULT_OK) {
					new LoginAppTask().execute();
				}
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
    }
    
    public void startSetupAppActivity() {
    	Log.d(LOG_TAG,"startHomeActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.SetupAppActivity.class);
    	startActivityForResult(intent,SETUP_APP_ACTIVITY);
    }
    
    public void startFundationsActivity() {
    	Log.d(LOG_TAG,"startFundationsActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.FundationsActivity.class);
    	startActivity(intent);
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
    
    private class GetCasUrlTask extends ApiTask<Integer, Integer, Object> {
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
        protected void onPostExecute(Object osef) {
    		super.onPostExecute(osef);
        	if (mUrl==null) {
        		onGetCasUrlFails(lastException);
        	}
        	_CAS_URL = mUrl;
        }
    }

    private class LoginCasTask extends ApiTask<Integer, Integer, Object> {
    	private String ticket, service;
    	private String seller;
    	
    	public LoginCasTask(String ticket, String service) {
    		super("Identification", PaulineActivity.this, 
    				"Connection au serveur en cour...");
    		this.ticket = ticket;
    		this.service = service;
    		seller = "";
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		seller = POSS.loginCas(ticket, service);
    		return true;
    	}

        @Override
        protected void onPostExecute(Object osef) {
        	super.onPostExecute(osef);
        	if (seller != null && !seller.isEmpty()) {
				mSession.setSellerLogin(seller);
        		if (getKey(PaulineActivity.this) != null) {
        			(new LoginAppTask()).execute();
        		}
        		else {
        			startSetupAppActivity();
        		}
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
    
    
    public void onResultLoginAppTask(String errorMessage) {
    	if (errorMessage != null) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(PaulineActivity.this);
    		builder.setTitle("Echec de l'identification")
    			.setMessage(errorMessage)
    			.setNegativeButton("J'ai compris", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }});
    		builder.create().show();
    	}
    	else {
    		startFundationsActivity();
    	}
    }

    protected class LoginAppTask extends ApiTask<Integer, Integer, Object> {
    	public LoginAppTask() {
    		super("Connexion", PaulineActivity.this, 
    				"Connexion au serveur...");
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		String key = PaulineActivity.getKey(PaulineActivity.this);
    		PaulineActivity.POSS.loginApp(key);
    		return true;
    	}
		
		@Override
		protected void onPostExecute(Object osef) {
			super.onPostExecute(osef);
			String lastExceptionMessage = null;
			if (lastException!=null) {
				lastExceptionMessage = lastException.getMessage();
			}
			onResultLoginAppTask(lastExceptionMessage);
		}
    }
    
    public static void storeRemove(Activity a, String key) {
    	SharedPreferences prefs = a.getSharedPreferences("prefs", MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.remove(key);
    	editor.commit();
    }
    
    public static void storeSet(Activity a, String key, String val) {
    	SharedPreferences prefs = a.getSharedPreferences("prefs", MODE_PRIVATE);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString(key, val);
    	editor.commit();
    }

    public static String storeGet(Activity a, String key) {
    	SharedPreferences prefs = a.getSharedPreferences("prefs", MODE_PRIVATE);
    	return prefs.getString(key, null);
    }
    
    public static String getKey(Activity a) {
    	return storeGet(a, "app_key");
    }
    
    public static void setKey(Activity a, String key) {
    	storeSet(a, "app_key", key);
    }
    
    public static String getAppName(Activity a) {
    	return storeGet(a, "app_name");
    }
    
    public static void setAppName(Activity a, String name) {
    	storeSet(a, "app_name", name);
    }
    
    public static void resetStore(Activity a) {
    	storeRemove(a, "app_key");
    	storeRemove(a, "app_name");
    }

}
