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
import android.widget.Toast;

abstract public class NfcActivity extends Activity {
	private static final String	LOG_TAG		= "NfcActivity"; 
	
	private NfcAdapter	mNfcAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate NFC");
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if(mNfcAdapter == null) {
            Toast.makeText(this, "Nfc not available", Toast.LENGTH_SHORT).show();
		}
		else if(!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Nfc not disabled", Toast.LENGTH_SHORT).show();
		}
	}
	
	final protected Boolean identifierIsAvailable() {
		return mNfcAdapter != null;
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
    
    abstract protected void onIdentification(String id);
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d(LOG_TAG, "Lecture d'un ID NFC");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = ByteArrayToHexString(tag.getId());
        Log.d(LOG_TAG, id);
        onIdentification(id);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
	    Log.d(LOG_TAG,"onResume");
		if(identifierIsAvailable()) {
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	 
			IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	 
			IntentFilter[] filters = {ndefFilter};
	 
			String[][] techs = {{Ndef.class.getName()}};
	 
			mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techs);
		}
    }
    
    @Override
	protected void onPause() {
    	super.onPause();
    	Log.d(LOG_TAG,"onPause");
		if(identifierIsAvailable())
			mNfcAdapter.disableForegroundDispatch(this);
	}
    
}
