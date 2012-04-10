package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

public class ResultTransactionActivity extends NfcActivity {
	private static final String LOG_TAG		= "ResultTransactionActivity";
	
	public final static int TRANSACTION_OK		= 0;
	
	private PaulineSession mSession;
	
	private ProgressBar mProgressBar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        mSession = PaulineSession.get(getIntent());
        
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        
        new TransactionTask(mSession.getItems()).execute();
    }
    
    protected void stop(Boolean success) {
		int r_code = RESULT_OK;
		if (!success) {
			r_code = -1;
		}
	    setResult(r_code);
		finish();
    }
    
    protected void setProgressPercent(int p) {
    	mProgressBar.setProgress(p);
    }
    
    private class TransactionTask extends AsyncTask<Integer, Integer, Integer> {
    	
    	private ArrayList<Item> mItems;
    	
    	public TransactionTask(ArrayList<Item> items) {
    		mItems = items;
    	}
    	
		@Override
		protected Integer doInBackground(Integer... _args) {
			for (int i=0; i<mItems.size(); ++i) {
				Item item = mItems.get(i);
				int r = PaulineActivity.PBUY.select(item.getId(), item.getCost(), item.getName());
				publishProgress((int) (((i+1) / (float) mItems.size()) * 100));
			}
			PaulineActivity.PBUY.endTransaction();
			return null;
		}
    	
		@Override
		protected void onProgressUpdate(Integer... progress) {
			setProgressPercent(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Integer r) {
			stop(true);
		}
    }
    
}
