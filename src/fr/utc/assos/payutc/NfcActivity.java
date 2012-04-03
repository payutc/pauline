package fr.utc.assos.payutc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;

public class NfcActivity extends Activity {
	private static final String	LOG_TAG		= "NfcActivity"; 
	
	private NfcAdapter	mNfcAdapter;
	protected Boolean nfcAvailable     	= true; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate NFC");
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if(mNfcAdapter == null) {
			// TODO Make a popup information . NFC is not available
			nfcAvailable = false;
		}
		else if(!mNfcAdapter.isEnabled()) {
			// TODO Make a popup information . NFC is disabled
			nfcAvailable = false;
		}
	}
	
    private String ByteArrayToHexString(byte [] inarray) 
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
    
    public String getNfcResult(Intent intent) {
    	Log.d(LOG_TAG, "Lecture d'un ID NFC");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());
        Log.d(LOG_TAG, id);
        return id;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
	    Log.d(LOG_TAG,"onResume "+nfcAvailable);
		if(nfcAvailable)
		{
			Context ctx = getBaseContext();
			Activity act = this;
			Class<? extends Activity> class1 = getClass();
			
			PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
				new Intent(ctx, class1).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	 
			IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	 
			IntentFilter[] filters = {ndefFilter};
	 
			String[][] techs = {{Ndef.class.getName()}};
	 
			mNfcAdapter.enableForegroundDispatch(act, pendingIntent, filters, techs);
		}
    }
    
    @Override
	protected void onPause() {
    	super.onPause();
    	Log.d(LOG_TAG,"onPause");
		if(nfcAvailable)
			mNfcAdapter.disableForegroundDispatch(this);
	}
    
}
