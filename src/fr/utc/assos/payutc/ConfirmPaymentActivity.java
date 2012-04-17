package fr.utc.assos.payutc;

import fr.utc.assos.payutc.adapters.ListItemAdapter;
import fr.utc.assos.payutc.views.PanierSummary;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConfirmPaymentActivity extends BaseActivity {
	public final static String LOG_TAG = "ConfirmPayment";
	
	ArrayAdapter<Item> mAdapter;
	
	private PanierSummary mPanierSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ConfirmPayment");
        setContentView(R.layout.confirm);

        mPanierSummary = (PanierSummary) findViewById(R.id.confirm_panier_summary);
        mPanierSummary.set(mSession);
        		
        ListView lv = (ListView)findViewById(R.id.confirm_list);

        mAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        
        lv.setAdapter(mAdapter);
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
    	startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onIdentification(String id) {
    	Log.d(LOG_TAG, "onIdentification");
    	startResultTransaction(id);
    }
    
    public void onClickDebug(View view) {
    	startResultTransaction(PaulineActivity.ID_TRECOUVR);
    }
}
