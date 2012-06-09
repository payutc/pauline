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
	    	mProgressDialog = new ProgressDialog(ResultTransactionActivity.this, ProgressDialog.STYLE_HORIZONTAL);
	    	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	mProgressDialog.setTitle("Commande");
	    	mProgressDialog.setMessage("Envoie des articles au serveur");
	    	mProgressDialog.show();
    	}
    	
		@Override
		protected Integer doInBackground(Integer... _args) {
			Log.d(LOG_TAG, ""+mIdBuyer);
			int r = PaulineActivity.PBUY.loadBuyer(mIdBuyer, PaulineActivity.MEAN_OF_LOGIN, "");
			if (r != 1) {
				return r;
			}
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int i=0; i<mItems.size(); ++i) {
				Item item = mItems.get(i);
				ids.add(item.getId());
				/*@SuppressWarnings("unused")
				int r = PaulineActivity.PBUY.select(item.getId(), item.getCost(), item.getName());
				publishProgress((int) (((i+1) / (float) mItems.size()) * 100));*/
			}
			r = PaulineActivity.PBUY.transaction(ids, "via Pauline");
			publishProgress(100);
			if (r!=1) {
				PaulineActivity.PBUY.endTransaction();
			}
			return r;
		}
    	
		@Override
		protected void onProgressUpdate(Integer... progress) {
			mProgressDialog.setProgress(progress[0]);
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
