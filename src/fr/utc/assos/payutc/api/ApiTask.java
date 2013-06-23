package fr.utc.assos.payutc.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class ApiTask<T> extends AsyncTask<Object,Object,Object> {
	protected ProgressDialog mProgressDialog;
	protected Exception lastException = null;
	protected Context mContext;
	protected String mTitle;
	protected String mMessage;
	protected String mTag;
	protected T response;
	protected ResponseHandler<T> responseHandler;

	public ApiTask(Context ctx, String title, String msg, ResponseHandler<T> responseHandler) {
		mTitle = title;
		mContext = ctx;
		mMessage = msg;
		mTag = getClass().getName();
		this.responseHandler = responseHandler;
		response = null;
	}

	/**
	 * Must be Override
	 * @return true
	 */
	protected abstract T callSoap() throws Exception;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext, 
				mTitle, 
				mMessage,
				true,
				false);
	}

	@Override
	protected Object doInBackground(Object... args) {

		try {
			response = callSoap();
		} catch (Exception e) {
			Log.e(mTag, "doInBackground", e);
			lastException = e;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Object osef) {
		mProgressDialog.dismiss();
		if (lastException==null) {
			responseHandler.onSuccess(response);
		}
		else {
			responseHandler.onError(lastException);
		}
	}
}
