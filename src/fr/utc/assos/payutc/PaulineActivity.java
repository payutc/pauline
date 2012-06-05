package fr.utc.assos.payutc;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import fr.utc.assos.payutc.soap.AdditionalKeyStoresSSLSocketFactory;
import fr.utc.assos.payutc.soap.PBuy;

/**
 * Demande au seller de badger
 * @author thomas
 *
 */
public class PaulineActivity extends BaseActivity {
	public static final String LOG_TAG			= "PaulineActivity";
	
	public static final int ASKSELLERPASSWORD	= 0;
	
	public final static int ID_POI				= 2;
	public final static int ID_FUNDATION		= 2;
	public static final int MEAN_OF_LOGIN		= 5; 
	
	public static final String ID_TRECOUVR			= "5B1BF88B";
	
	public static final PBuy PBUY = new PBuy();
	
   
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
        // @todo virer ce vieux hack
    	//startAskSellerPasswordActivity(ID_TRECOUVR); 
        /*SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", createAdditionalCertsSSLSocketFactory(), 443));
        MonThread2 t2 = new MonThread2();
        Thread t = new Thread(t2);
        t.start();*/
    	
        if (!identifierIsAvailable()) {
        	startAskSellerPasswordActivity(ID_TRECOUVR);
        	//startHomeActivity();
        	/*
        	PBUY.loadSeller("trecouvr", 1, "", PaulineActivity.ID_POI);
        	PBUY.loadBuyer("trecouvr", 1, "");
        	Intent intent = new Intent(this, fr.utc.assos.payutc.ShowArticleActivity.class);
        	Bundle b = new Bundle();
        	intent.putExtras(b);
        	startActivity(intent);//*/
        }
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode);
		switch (requestCode) {
		case ASKSELLERPASSWORD:
			if (resultCode == RESULT_OK) {
				startHomeActivity();
			}
		}
    }
    
    @Override
    protected void onIdentification(String id) {
    	Log.d(LOG_TAG, "onIdentification");
        startAskSellerPasswordActivity(id);
    }

    
    private void startAskSellerPasswordActivity(String id) {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.AskSellerPasswordActivity.class);
    	Bundle b = new Bundle();
    	b.putString("id", id); //Your id
    	intent.putExtras(b); //Put your id to your next Intent
    	startActivityForResult(intent, ASKSELLERPASSWORD);
    }
    
    public void startHomeActivity() {
    	Log.d(LOG_TAG,"startHomeActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
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

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }

}