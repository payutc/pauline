package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ShowArticleActivity extends Activity {
	private static final String LOG_TAG = "ShowArticleActivity";
	
	public int type;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        type = b.getInt("type");		// récupération du type de vente
    }
    
    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }
    
    public void onClickCancel(View view) {
    	stop();
    }
    
    public void onClickOk(View view) {
    	startConfirmPaymentActivity(42);
    }
    
    private void startConfirmPaymentActivity(int prix) {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ConfirmPaymentActivity.class);
    	Bundle b = new Bundle();
    	b.putInt("prix", prix);
    	intent.putExtras(b);
    	startActivityForResult(intent, 0);
    }
}
