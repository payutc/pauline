package fr.utc.assos.payutc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;

public class PaulineActivity extends Activity {
	public static final String LOG_TAG		= "PaulineActivity";
	public static final int STATE_BADGE_SELLER 	= 0;
	public static final int STATE_PASS_SELLER 	= 1;
	public static final int STATE_HOME			= 2;
	public static final int STATE_SHOW_ARTICLE	= 3;
	private int NFC_DEBUG           = 1; // IF 0 alors pas de NFC
	private NfcAdapter	mNfcAdapter;
	private final String ID_TRECOUVR			= "5B1BF88B";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PaulineActivity");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if(mNfcAdapter == null) {
			// TODO Make a popup information . NFC is not available
			//finish();
			//return;
			NFC_DEBUG = 0;
		}
		else if(!mNfcAdapter.isEnabled()) {
			// TODO Make a popup information . NFC is disabled
			//finish();
			//return;
			NFC_DEBUG = 0;
		}
        if(NFC_DEBUG == 0) {
        	startAskSellerPasswordActivity(ID_TRECOUVR);
        }
        setContentView(R.layout.main);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		startHomeActivity();
    	}
    	else {
    		// stay in this activity
    		if(NFC_DEBUG == 0)
    			startAskSellerPasswordActivity(ID_TRECOUVR);
    	}
    }
    
    String ByteArrayToHexString(byte [] inarray) 
    {
    	int i, j, in;
    	String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    	String out= "";

    	for(j = 0 ; j < inarray.length ; ++j) 
        {
    		in = (int) inarray[j] & 0xff;
    		i = (in >> 4) & 0x0f;
    		out += hex[i];
    		i = in & 0x0f;
    		out += hex[i];
        }
    	return out;
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d("NFC_TEST", "Lecture d'un ID NFC");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());
        Log.d("NFC_TEST", id);
        startAskSellerPasswordActivity(id);
    }

    @Override
	protected void onResume() {
		super.onResume();
		if(NFC_DEBUG == 1)
		{
			PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
				new Intent(getBaseContext(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	 
			IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	 
			IntentFilter[] filters = {ndefFilter};
	 
			String[][] techs = {{Ndef.class.getName()}};
	 
			mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techs);
		}
	}
    
	@Override
	protected void onPause() {
		super.onPause();
		if(NFC_DEBUG == 1)
			mNfcAdapter.disableForegroundDispatch(this);
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