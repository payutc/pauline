package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ResultTransactionActivity extends NfcActivity {
	
	public final static int TRANSACTION_OK		= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
    }
    
    protected void stop(Boolean success) {
		int r_code = RESULT_OK;
		if (!success) {
			r_code = -1;
		}
	    setResult(r_code);
		finish();
    }
    
    
}
