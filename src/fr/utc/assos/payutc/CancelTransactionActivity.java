package fr.utc.assos.payutc;

import android.os.Bundle;
import android.util.Log;

public class CancelTransactionActivity extends BaseActivity {
	public final static String LOG_TAG = "CancelTransactionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate CancelTransactionActivity");
    }
}
