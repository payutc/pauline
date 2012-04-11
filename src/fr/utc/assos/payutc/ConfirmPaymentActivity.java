package fr.utc.assos.payutc;

import fr.utc.assos.payutc.adapters.ListItemAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConfirmPaymentActivity extends NfcActivity {
	public final static String LOG_TAG = "ConfirmPayment";
	
	private PaulineSession mSession;
	ArrayAdapter<Item> mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ConfirmPayment");
        setContentView(R.layout.confirm);
        mSession = PaulineSession.get(getIntent());
        
        ListView lv = (ListView)findViewById(R.id.confirm_list);

        mAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        
        lv.setAdapter(mAdapter);
    }

    protected void stop(Boolean ok) {
		Intent intent = new Intent();
		if (ok) {
			setResult(RESULT_OK, intent);
		}
		else {
			setResult(RESULT_CANCELED, intent);
		}
		finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (mSession.getHomeChoice() == PaulineSession.VENTE_LIBRE && resultCode == RESULT_OK) {
    		stop(true);
    	}
    }
    
    private void startResultTransaction(String id) {
    	Log.d(LOG_TAG,"startResultTransaction");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ResultTransactionActivity.class);
    	mSession.setBuyerId(id);
    	mSession.save(intent);
    	startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d(LOG_TAG, "new Intent");
    	String id = getNfcResult(intent);
    	startResultTransaction(id);
    }
    
    public void onClickDebug(View view) {
    	startResultTransaction(PaulineActivity.ID_TRECOUVR);
    }
}
