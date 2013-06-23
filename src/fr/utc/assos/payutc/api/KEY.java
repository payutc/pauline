package fr.utc.assos.payutc.api;

import java.io.IOException;

import org.json.JSONException;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class KEY extends JsonApiClient {
	public static final String LOG_TAG = "KEY";

	public KEY(String url) {
		super(url);
	}
	

	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Application {
		public int app_id;
		public String app_url;
		public String app_name;
		public String app_desc;
		public String app_creator;
		public String app_key;
		public Application() {}  // jackson need a dummy constructor
	}
	
	public Application registerApplication(String app_url, String app_name) 
			throws IOException, JSONException, ApiException {
		return registerApplication(app_url, app_name, "");
	}
	
	public Application registerApplication(String app_url, String app_name, String app_desc) 
			throws IOException, JSONException, ApiException {
		Arg[] args =  {
				new Arg("app_url", app_url),
				new Arg("app_name", app_name),
				new Arg("app_desc", app_desc)
		};
		Application app = call("registerApplication", args, Application.class);
		Log.d(LOG_TAG, "registerApplication : "+app.app_name+" "+app.app_key);
		return app;
	}

}
