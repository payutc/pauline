package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class PaulineActivity extends Activity {
	public static final String LOG_TAG		= "PaulineActivity";
	public static final int STATE_BADGE_SELLER 	= 0;
	public static final int STATE_PASS_SELLER 	= 1;
	public static final int STATE_HOME			= 2;
	public static final int STATE_SHOW_ARTICLE	= 3;
	private final String ID_TRECOUVR			= "5B1BF88B";
	private Nfc myNfc = new Nfc();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PaulineActivity");
        myNfc.onCreate(getApplicationContext());
        if(myNfc.NFC_HERE == 0) {
        	startAskSellerPasswordActivity(ID_TRECOUVR);
        }
        setContentView(R.layout.main);
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		startHomeActivity();
    	}
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d(LOG_TAG, "new Intent");
    	String id = myNfc.onNewIntent(intent);
        startAskSellerPasswordActivity(id);
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
    
    private void startAskSellerPasswordActivity(String id) {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.AskSellerPasswordActivity.class);
    	Bundle b = new Bundle();
    	b.putString("id", id); //Your id
    	intent.putExtras(b); //Put your id to your next Intent
    	startActivityForResult(intent, 0);
    }
    
    public void startHomeActivity() {
    	Log.d(LOG_TAG,"startHomeActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
    	startActivity(intent);
    }
}