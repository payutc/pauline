package fr.utc.assos.payutc.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonApiClient {
	public static final String LOG_TAG = "JsonApiClient";
	public String api_url;
	HashMap<String, String> cookies = new HashMap<String, String>();
	
	public class Arg {
		public String key, value;
		Arg(String _key, Object _value) {
			key = _key; value = String.valueOf(_value);
		}
	}
	
	public JsonApiClient(String url) {
		api_url = url;
	}
	
	protected String arglist2string(Arg[] args) throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (Arg arg : args) {
			builder.append(URLEncoder.encode(arg.key, "UTF-8")+"="+URLEncoder.encode(arg.value, "UTF-8")+"&");
		}
		return builder.toString();
	}
	
	protected String post(String uri, Arg[] args) 
			throws IOException {
		String data = arglist2string(args);
		Log.d(LOG_TAG, "post data : "+data);
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));
        conn.setRequestProperty("Cookie", getCookiesHeader());
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(data);
        wr.flush();

		StringBuilder builder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String inputLine;
	    while ((inputLine = in.readLine()) != null) {
	    	builder.append(inputLine);
	    }
		in.close();
	    update_cookies(conn.getHeaderField("Set-Cookie"));
		
        return builder.toString();
	}

	protected Object call(String method) 
			throws IOException, JSONException, ApiException {
		Arg[] args = {};
		return call(method, args);
	}
	protected Object call(String method, Arg[] args)
			throws IOException, JSONException, ApiException {
		String post_result = post(api_url+"?method="+URLEncoder.encode(method, "UTF-8"), args);
		//Log.d(LOG_TAG, "post_result : "+post_result);
		
		JSONObject result = null;
		try {
			result = new JSONObject(post_result);
		}
		catch (JSONException e) {
			throw new ApiException(42, e.getMessage()+". Impossible de parser : "+post_result);
		}

		if (result.opt("success") == null) {
			int err_code = 42;
			String err_msg = null;
			try {
				err_code = result.getInt("error");
				err_msg = result.getString("error_msg");
			} catch (Exception e) {
				Log.e(LOG_TAG, "soap", e);
			}
			throw new ApiException(err_code, err_msg);
		}
		
		return result.get("success");
	}
	

	synchronized String getCookiesHeader() {
		StringBuilder sb = new StringBuilder();
        for (String cookie : cookies.keySet()) {
        	sb.append(cookie + "=" + cookies.get(cookie) + "; ");
        }
        return sb.toString();
	}
	
	synchronized void update_cookies(String cookiesHeader) {
		if (cookiesHeader!=null) {
		    Log.d(LOG_TAG, "cookies : "+cookiesHeader);
	    	String[] cookieSplit = cookiesHeader.replace("; ", ";").split(";");
	    	for (String cookieString : cookieSplit) {
	    		String cookieKey = cookieString.substring(0, cookieString.indexOf("="));
	    		String cookieValue = cookieString.substring(cookieString.indexOf("=")+1);
	    		cookies.put(cookieKey, cookieValue);
	    		Log.d(LOG_TAG, cookieKey + " = " + cookieValue);
	    	}
		}
	}
}
