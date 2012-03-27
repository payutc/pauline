package fr.utc.assos.payutc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;

public class Nfc {
	public static final String LOG_TAG		= "NFC";
	private NfcAdapter	mNfcAdapter;
	public int NFC_HERE     = 1; 
	
	public void onCreate(Context ctx) {
        Log.d(LOG_TAG, "onCreate NFC");
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(ctx);
		if(mNfcAdapter == null) {
			// TODO Make a popup information . NFC is not available
			NFC_HERE = 0;
		}
		else if(!mNfcAdapter.isEnabled()) {
			// TODO Make a popup information . NFC is disabled
			NFC_HERE = 0;
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
    
    protected String onNewIntent(Intent intent) {
    	Log.d(LOG_TAG, "Lecture d'un ID NFC");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());
        Log.d(LOG_TAG, id);
        return id;
    }
    
    protected void onResume(Context ctx, Activity act, Class<? extends PaulineActivity> class1) {
	    Log.d(LOG_TAG,"onResume "+NFC_HERE);
		if(NFC_HERE == 1)
		{
			PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
				new Intent(ctx, class1).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	 
			IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	 
			IntentFilter[] filters = {ndefFilter};
	 
			String[][] techs = {{Ndef.class.getName()}};
	 
			mNfcAdapter.enableForegroundDispatch(act, pendingIntent, filters, techs);
		}
    }
    
	protected void onPause(Activity act) {
    	Log.d(LOG_TAG,"onPause");
		if(NFC_HERE == 1)
			mNfcAdapter.disableForegroundDispatch(act);
	}
}
