package fr.utc.assos.payutc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AskSellerPasswordActivity extends Activity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");
        
        Bundle b = getIntent().getExtras();
        String id = b.getString("id");		// récupération du login renvoyé par le nfc
    }
    
	private void exit(Boolean success) {
		int r_code = RESULT_OK;
		if (!success) {
			r_code = -1;
		}
	    setResult(r_code);
		finish();
	}
}
