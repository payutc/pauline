package fr.utc.assos.payutc.soap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SoapTask extends AsyncTask<Integer, Integer, Integer> {
	private ProgressDialog mProgressDialog;
	private Exception lastException = null;
	protected Context mContext;
	protected String mTitle;
	protected String mMessage;
	protected int mMaxTries;
	protected String mTag;
	
	public SoapTask(String title, Context ctx, String msg, int max_tries) {
		mTitle = title;
		mContext = ctx;
		mMessage = msg;
		mMaxTries = max_tries;
		mTag = getClass().getName();
	}
	
	/**
	 * Must be Override
	 * @return true
	 */
	protected boolean callSoap() throws Exception {
		return true;
	}

    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	mProgressDialog = ProgressDialog.show(mContext, 
    			mTitle, 
    			mMessage,
    			true,
    			false
    	);
    }
    
    @Override
    protected Integer doInBackground(Integer... args) {
    	boolean ok = false;
		try {
			ok = callSoap();
		} catch (Exception e) {
			Log.e(mTag, "doInBackground", e);
			lastException = e;
		}
    	for (int i=1; i<=mMaxTries; ++i) {
    		if (ok) break;
    		else {
        		publishProgress(i);
        		try {
					Thread.sleep(2000L);
	        		ok = callSoap();
				} catch (Exception e) {
					Log.e(mTag, "doInBackground", e);
					lastException = e;
				}
    		}
    	}
    	return 0;
    }

	@Override
	protected void onProgressUpdate(Integer... progress) {
		String lastExceptionMsg = "";
		if (lastException!=null) {
			lastExceptionMsg = lastException.getMessage();
		}
		mProgressDialog.setMessage(mMessage + " (nb. echec : "+progress[0]+")" + lastExceptionMsg);
	}
	
	@Override
	protected void onPostExecute(Integer osef) {
		super.onPostExecute(osef);
    	mProgressDialog.dismiss();
	}
}