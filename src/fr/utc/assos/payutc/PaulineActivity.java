package fr.utc.assos.payutc;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import fr.utc.assos.payutc.soap.AdditionalKeyStoresSSLSocketFactory;
import fr.utc.assos.payutc.soap.PBuy;
import fr.utc.assos.payutc.soap.SoapTask;

/**
 * Demande au seller de badger
 * @author thomas
 *
 */
public class PaulineActivity extends BaseActivity {

	//* DEV
	// Urls pour l'api soap
	public static final String API_HOST = "assos.utc.fr";
	public static final String API_PATH = "/payutc_dev/server/POSS2.class.php";
	public static final String API_NAMESPACE = "https://assos.utc.fr:443/payutc_dev/server/POSS2.class.php";
	public static final boolean API_SSL = true;
	// Id du point de vente
	public final static int ID_POI				= 46;
	// */
	/*
	public static final String API_HOST = "assos.utc.fr";
	public static final String API_PATH = "/buckutt/POSS2.class.php";
	public static final String API_NAMESPACE = "https://assos.utc.fr:443/buckutt/POSS2.class.php";
	public static final boolean API_SSL = true;
	public final static int ID_POI				= 48;
	//*/
	
	/** Cas service */
	public static final String CAS_SERVICE		= "https://cas.utc.fr/cas/";
	
	public static final String LOG_TAG			= "PaulineActivity";
	
	public static PBuy PBUY;
	public static final int CASWEBVIEW	= 0;
	public static final int HOMEACTIVITY = 1;
	
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
        PBUY = new PBuy(API_HOST, API_PATH, API_NAMESPACE, API_SSL);
        
        GetCasUrlTask task = new GetCasUrlTask();
        task.execute();
        // decomment pour aller directement au home sans se logger
        //startHomeActivity();
        
    }
    

    public void onClickLogin(View _view) {
    	LogByCas();
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
    
    private class GetCasUrlTask extends SoapTask {
    	private String mUrl;

    	public GetCasUrlTask() {
			super("Synchronization", PaulineActivity.this, 
					"Synchronization avec le serveur en cours...", 1);
		}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		mUrl = PBUY.getCasUrl();
    		return mUrl!=null;
    	}
        

        @Override
        protected void onPostExecute(Integer osef) {
    		super.onPostExecute(osef);
        	if (mUrl==null) {
        		Toast.makeText(PaulineActivity.this, "Echec", Toast.LENGTH_SHORT).show();
        		try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		finish();
        	}
        	_CAS_URL = mUrl;
        }
    }

    private class LoadPosTask extends SoapTask {
    	private String mTicket, mService;
    	private boolean mLoaded;
    	
    	public LoadPosTask(String ticket, String service) {
    		super("Identification", PaulineActivity.this, 
    				"Connection au serveur en cour...", 1);
    		mTicket = ticket;
    		mService = service;
    		mLoaded = false;
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		mLoaded = PBUY.loadPos(mTicket, mService, ID_POI);
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