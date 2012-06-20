package fr.utc.assos.payutc;

import android.app.ProgressDialog;
import android.content.Intent;
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
	
	public static final int CASWEBVIEW	= 0;

	
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
    
    public void onCancel(View view) {
    	stop(false);
    }
    
    public void onLogin(View view) {
    	Log.d(LOG_TAG,"startCasWebView");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.CasWebView.class);
    	startActivityForResult(intent, CASWEBVIEW);
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode);
		switch (requestCode) {
		case CASWEBVIEW:
			if (resultCode == RESULT_OK) {
				String ticket = data.getStringExtra("ticket");
				Log.i(LOG_TAG, "ticket : "+ticket);
			}
		}
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
