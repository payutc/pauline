package fr.utc.assos.payutc;

import android.app.Activity;
import android.os.Bundle;

public class AskSellerPasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
