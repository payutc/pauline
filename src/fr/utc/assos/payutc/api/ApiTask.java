package fr.utc.assos.payutc.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ApiTask<key1 extends Object, key2 extends Object, key3 extends Object> extends AsyncTask<key1,key2,key3> {
	protected ProgressDialog mProgressDialog;
	protected Exception lastException = null;
	protected Context mContext;
	protected String mTitle;
	protected String mMessage;
	protected int mMaxTries;
	protected String mTag;
	
	public ApiTask(String title, Context ctx, String msg) {
		this(title, ctx, msg, 3);
	}
	
	public ApiTask(String title, Context ctx, String msg, int max_tries) {
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
    protected key3 doInBackground(key1... args) {
    	
		try {
			callSoap();
		} catch (Exception e) {
			Log.e(mTag, "doInBackground", e);
			lastException = e;
		}
		
		return null;
    }
	
	@Override
	protected void onPostExecute(Object osef) {
    	mProgressDialog.dismiss();
	}
}
