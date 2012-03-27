package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CancelTransactionActivity extends Activity {
	public final static String LOG_TAG = "CancelTransactionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate CancelTransactionActivity");
    }

    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }
}
