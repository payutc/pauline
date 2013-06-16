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
import java.util.List;

import org.json.JSONException;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonApiClient {
	public static final String LOG_TAG = "JsonApiClient";
	public String api_url;
	static HashMap<String, String> cookies = new HashMap<String, String>();
	ObjectMapper mapper;
	
	protected class Arg {
		public String key, value;
		Arg(String _key, Object _value) {
			key = _key; value = String.valueOf(_value);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	protected static class ServiceException {
		public ApiException error;
		public ServiceException() {}
	}
	
	
	public JsonApiClient(String url) {
		api_url = url;
		mapper = new ObjectMapper();
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
		Log.d(LOG_TAG, "post "+uri+", data : "+data);
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
		InputStreamReader is;
		if (conn.getResponseCode() == 200) {
			is = new InputStreamReader(conn.getInputStream(), "UTF-8");
		}
		else {
			is = new InputStreamReader(conn.getErrorStream(), "UTF-8");
		}
        BufferedReader in = new BufferedReader(is);
	    String inputLine;
	    while ((inputLine = in.readLine()) != null) {
	    	builder.append(inputLine);
	    }
		in.close();
	    updateCookies(conn.getHeaderFields().get("Set-Cookie"));
		
        return builder.toString();
	}
	

	protected <T> T call(String method, TypeReference<T> valueTypeRef) 
			throws UnsupportedEncodingException, ApiException, IOException {
		Arg[] args = {};
		return call(method, args, valueTypeRef);
	}
	protected <T> T call(String method, Class<T> valueType) 
			throws IOException, JSONException, ApiException {
		Arg[] args = {};
		return call(method, args, valueType);
	}
	protected <T> T call(String method, Arg[] args, TypeReference<T> valueTypeRef) 
			throws ApiException, UnsupportedEncodingException, IOException {
		String post_result = _call(method, args);
		// tente de parser le résultat
		T result = null;
		try {
			result = mapper.readValue(post_result, valueTypeRef);
		}
		catch (IOException e) {
			throw new ApiException("LocalParseError", "42", e.getMessage()+". Impossible de parser : "+post_result);
		}
		return result;
	}
	protected <T> T call(String method, Arg[] args, Class<T> valueType) 
			throws ApiException, UnsupportedEncodingException, IOException {
		String post_result = _call(method, args);
		// tente de parser le résultat
		T result = null;
		try {
			result = mapper.readValue(post_result, valueType);
		}
		catch (IOException e) {
			throw new ApiException("LocalParseError", "42", e.getMessage()+". Impossible de parser : "+post_result);
		}
		return result;
	}
	protected String _call(String method, Arg[] args)
			throws ApiException, UnsupportedEncodingException, IOException {
		String post_result = post(api_url+"/"+URLEncoder.encode(method, "UTF-8"), args);
		Log.d(LOG_TAG, "post_result : '"+post_result+"'");
		
		if (post_result == null) {
			return null;
		}
		
		// test si c'est une exception
		try {
			ServiceException e = mapper.readValue(post_result, ServiceException.class);
			if (e != null && e.error != null) {
				throw e.error;
			}
		}
		catch (IOException _osef) {}
		return post_result;
	}
	

	synchronized String getCookiesHeader() {
		StringBuilder sb = new StringBuilder();
        for (String cookie : cookies.keySet()) {
        	sb.append(cookie + "=" + cookies.get(cookie) + "; ");
        }
        return sb.toString();
	}
	
	synchronized void updateCookies(List<String> cookiesHeader) {
		if (cookiesHeader!=null) {
		    Log.d(LOG_TAG, "cookies : "+cookiesHeader);
		    for (String cookie : cookiesHeader) {
		    	try {
			    	cookie = cookie.substring(0, cookie.indexOf(";"));
			        String cookieName = cookie.substring(0, cookie.indexOf("="));
			        String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
				    cookies.put(cookieName, cookieValue);
				    Log.d(LOG_TAG, cookieName + " = " + cookieValue);
		    	}
		    	catch (Exception ex) {
		    		Log.w(LOG_TAG, "error parsing cookie : '"+cookie+"'", ex);
		    	}
		    }
		}
	}
}
