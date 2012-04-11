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
        
        new TransactionTask(mSession.getItems()).execute();
    }
    
    protected void setResultView(Boolean success) {
    	if (success) {
            setContentView(R.layout.result_ok);
    	}
    }
    
    public void onClickOk(View view) {
    	stop(true);
    }
    
    protected class TransactionTask extends AsyncTask<Integer, Integer, Integer> {
    	
    	private ArrayList<Item> mItems;
    	
    	private ProgressDialog mProgressDialog;
    	
    	public TransactionTask(ArrayList<Item> items) {
    		mItems = items;
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
			for (int i=0; i<mItems.size(); ++i) {
				Item item = mItems.get(i);
				@SuppressWarnings("unused")
				int r = PaulineActivity.PBUY.select(item.getId(), item.getCost(), item.getName());
				publishProgress((int) (((i+1) / (float) mItems.size()) * 100));
			}
			PaulineActivity.PBUY.endTransaction();
			return null;
		}
    	
		@Override
		protected void onProgressUpdate(Integer... progress) {
			mProgressDialog.setProgress(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Integer r) {
			mProgressDialog.dismiss();
			setResultView(true);
		}
    }
    
}
