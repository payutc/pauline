package fr.utc.assos.payutc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * Demande au seller de rentrer son code, demande validation au serveur, puis
 * revnoie sur PaulineActivity en envoyant le résultat
 * 
 * @author thomas
 *
 */
public class AskSellerPasswordActivity extends NfcActivity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	private final int MEAN_OF_LOGIN		= 5; 
	
	

	
	private String mIdSeller;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");

        Bundle b = getIntent().getExtras();
        mIdSeller = b.getString("id");		// récupération du login renvoyé par le nfc
        Log.d(LOG_TAG, "badge #"+mIdSeller);

        setContentView(R.layout.asksellerpassword);
        
        // @todo virer ce hack
        new LoadSellerTask("trecouvr", 1, "");
    }
    
    public void onOk(View view) {
    	String pass = (String) ((TextView) findViewById(R.id.input_login)).getText();
    	new LoadSellerTask(mIdSeller, MEAN_OF_LOGIN, pass).execute();
    }
    
    public void onCancel(View view) {
    	stop(false);
    }

    protected void stop(Boolean success) {
		if (success) {
			setResult(RESULT_OK);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		finish();
    }
    
    
    private class LoadSellerTask extends AsyncTask<Integer, Integer, Integer> {
    	private String mIdSeller, mPass;
    	private int mMeanOfLogin;
    	
    	public LoadSellerTask(String idSeller, int meanOfLogin, String pass) {
    		mIdSeller = idSeller;
    		mMeanOfLogin = meanOfLogin;
    		mPass = pass;
    	}
    	
        protected Integer doInBackground(Integer... args) {
        	int r = PaulineActivity.PBUY.loadSeller(mIdSeller, mMeanOfLogin, mPass, PaulineActivity.ID_POI);
        	// @todo enlever ce vieux hack 
			PaulineActivity.PBUY.loadBuyer("trecouvr", 1, "");
        	return r;
        }

        protected void onPostExecute(Integer r) {
        	stop(r==1);
        }
    }
    
}
