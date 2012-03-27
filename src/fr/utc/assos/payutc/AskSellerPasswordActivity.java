package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AskSellerPasswordActivity extends Activity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	private Nfc myNfc = new Nfc();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");
        myNfc.onCreate(getApplicationContext());

        Bundle b = getIntent().getExtras();
        String id = b.getString("id");		// récupération du login renvoyé par le nfc
        Log.d(LOG_TAG, "badge #"+id);
    }

    protected void stop(Boolean success) {
		int r_code = RESULT_OK;
		if (!success) {
			r_code = -1;
		}
	    setResult(r_code);
		finish();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
    	myNfc.onResume(getBaseContext(), this, getClass());
	}
    
	@Override
	protected void onPause() {
		super.onPause();
    	myNfc.onPause(this);
	}
}
