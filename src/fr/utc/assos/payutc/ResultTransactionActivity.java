package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    
    protected class TransactionTask extends AsyncTask<Integer, Integer, Integer> {
    	
    	private ArrayList<Item> mItems;
    	private String mIdBuyer;
    	
    	private ProgressDialog mProgressDialog;
    	
    	public TransactionTask(String id, ArrayList<Item> items) {
    		mItems = items;
    		mIdBuyer = id;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
        	mProgressDialog = ProgressDialog.show(ResultTransactionActivity.this, 
        			"Transaction", 
        			"Transaction en cours...",
        			true,
        			false
        	);
    	}
    	
		@Override
		protected Integer doInBackground(Integer... _args) {
			Log.d(LOG_TAG, ""+mIdBuyer);
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int i=0; i<mItems.size(); ++i) {
				Item item = mItems.get(i);
				ids.add(item.getId());
			}
			int r = PaulineActivity.PBUY.transaction(mIdBuyer, ids, "via Pauline");
			return r;
		}
		
		@Override
		protected void onPostExecute(Integer r) {
			mProgressDialog.dismiss();
			if (r==1) {
				setResultView(true);
			}
			else {
				setResultView(false);
			}
		}
    }
    
}
