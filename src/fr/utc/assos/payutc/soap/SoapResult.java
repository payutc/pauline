package fr.utc.assos.payutc.soap;

import java.io.StringReader;

import com.Ostermiller.util.CSVParser;

import android.util.Log;

public abstract class SoapResult {
	
	private static final String LOG_TAG		= "Result";
	
	public static final int ERR_EMPTY_STRING	= -1;
	public static final int ERR_NULL_STRING		= -2;
	public static final int ERR_UNKNOWN			= -2;

	private int mErrorCode = 0;
	
	public SoapResult(String wsdl_response) {
		tryParse(wsdl_response);
	}
	
	public int getErrorCode() {
		return mErrorCode;
	}
	
	abstract void processValues(String[][] values);
	
	protected void tryParse(String wsdl_response) {
		if (wsdl_response == null) {
			mErrorCode = ERR_NULL_STRING;
		}
		else if (wsdl_response.isEmpty()) {
			mErrorCode = ERR_EMPTY_STRING;
		}
		else {
			try {
				int err = Integer.parseInt(wsdl_response);
				mErrorCode = err;
			}
			catch (NumberFormatException ex) {
				try {
					CSVParser lcsvp = new CSVParser(
							new StringReader(
								wsdl_response + "\n"
							)
						);
					processValues(lcsvp.getAllValues());
				}
				catch (Exception ex2) {
					mErrorCode = ERR_UNKNOWN;
					Log.e(LOG_TAG, "", ex2);
				}
			}
		}
	}
	
}
