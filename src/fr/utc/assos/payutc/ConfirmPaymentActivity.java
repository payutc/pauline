package fr.utc.assos.payutc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ConfirmPaymentActivity extends NfcActivity {
	public final static String LOG_TAG = "ConfirmPayment";
	
	public int type;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ConfirmPayment");

        Bundle b = getIntent().getExtras();
        type = b.getInt("type");
        Log.d(LOG_TAG, "type "+type);
    }

    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (type == HomeActivity.VENTE_LIBRE && resultCode == RESULT_OK) {
    		stop();
    	}
    }
    
    private void startResultTransaction() {
    	Log.d(LOG_TAG,"startResultTransaction");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ResultTransactionActivity.class);
    	Bundle b = new Bundle();
    	startActivityForResult(intent, 0);
    }
}
