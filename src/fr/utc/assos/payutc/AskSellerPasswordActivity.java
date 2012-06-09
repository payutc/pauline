package fr.utc.assos.payutc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Demande au seller de rentrer son code, demande validation au serveur, puis
 * revnoie sur PaulineActivity en envoyant le résultat
 * 
 * @author thomas
 *
 */
public class AskSellerPasswordActivity extends BaseActivity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	
	

	
	private String mIdSeller;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");

        Bundle b = getIntent().getExtras();
        mIdSeller = b.getString("id");		// récupération du login renvoyé par le nfc
        Log.d(LOG_TAG, "badge #"+mIdSeller);

        setContentView(R.layout.asksellerpassword);
    }
    
    public void onOk(View view) {
    	String pass = (String) ((EditText) findViewById(R.id.input_login)).getText().toString();
    	new LoadSellerTask(mIdSeller, PaulineActivity.MEAN_OF_LOGIN, pass).execute();
    }
    
    public void onCancel(View view) {
    	stop(false);
    }
    
    private class LoadSellerTask extends AsyncTask<Integer, Integer, Integer> {
    	private String mIdSeller, mPass;
    	private int mMeanOfLogin;
    	private ProgressDialog mProgressDialog;
    	
    	public LoadSellerTask(String idSeller, int meanOfLogin, String pass) {
    		mIdSeller = idSeller;
    		mMeanOfLogin = meanOfLogin;
    		mPass = pass;
    	}

        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
        	mProgressDialog = ProgressDialog.show(AskSellerPasswordActivity.this, 
        			"Identification", 
        			"Connection au serveur en cour...",
        			true,
        			false
        	);
        }
        
        @Override
        protected Integer doInBackground(Integer... args) {
        	int r = PaulineActivity.PBUY.loadSeller(mIdSeller, mMeanOfLogin, mPass, PaulineActivity.ID_POI);
        	return r;
        }

        @Override
        protected void onPostExecute(Integer r) {
        	mProgressDialog.dismiss();
        	if (r==1) {
            	stop(true);
        	}
        	else {
        		Toast.makeText(AskSellerPasswordActivity.this, "Echec de l'identification", Toast.LENGTH_SHORT).show();
        	}
        }
    }
    
}
