package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fr.utc.assos.payutc.soap.SoapTask;

public class ResultTransactionActivity extends BaseActivity {
	private static final String LOG_TAG		= "ResultTransactionActivity";
	
	public final static int TRANSACTION_OK		= 0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ResultTransactionActivity");
        
        new TransactionTask(mSession.getBuyerId() ,mSession.getItems()).execute();
    }
    
    protected void setResultView(Boolean success) {
    	if (success) {
            setContentView(R.layout.result_ok);
    	}
    	else {
    		setContentView(R.layout.result_echec);
    	}
    }
    
    public void onClickOk(View view) {
    	stop(true);
    }
    
    public void onClickOkFromEchec(View view) {
    	stop(RESULT_CANCELED);
    }
    
    protected class TransactionTask extends SoapTask {
    	private ArrayList<Integer> mIds;
    	private String mIdBuyer;
    	private boolean r=false;
    	
    	public TransactionTask(String id, ArrayList<Item> items) {
    		super("Transaction", ResultTransactionActivity.this,
    				"Transaction en cours...", 2);
			mIds = new ArrayList<Integer>();
			for (int i=0; i<items.size(); ++i) {
				Item item = items.get(i);
				mIds.add(item.getId());
			}
    		mIdBuyer = id;
    	}
    	
    	
		@Override
		protected boolean callSoap() throws Exception {
			r = PaulineActivity.PBUY.transaction(mIdBuyer, mIds, "via Pauline");
			return true;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
			if (r) {
				setResultView(true);
			}
			else {
				setResultView(false);
			}
		}
    }
    
}
