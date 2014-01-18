package fr.utc.assos.payutc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.KEY;
import fr.utc.assos.payutc.api.KEY.Application;
import fr.utc.assos.payutc.api.ResponseHandler;
import fr.utc.assos.payutc.api.responsehandler.DisplayDialogOnError;

public class SetupAppActivity extends BaseActivity {
	public static final String LOG_TAG			= "SetupAppActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate SetupActivity");
        setContentView(R.layout.setup);
        EditText et = (EditText)findViewById(R.id.app_name);
        // set default name
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        String defaultName = "Téléphone de "+mSession.getSellerLogin()+" - "+
        		dateFormat.format(new Date());
        et.setText(defaultName);
	}
	
	public void run(View v) {
		EditText et = (EditText)findViewById(R.id.app_name);
		String app_name = et.getText().toString();
		new SetupApplicationTask(PaulineActivity.CAS_SERVICE, app_name, new SetupAppRespHandler()).execute();
	}
	
	/*
	 * No retry
	 * Stay in activity
	 * 
	 * There is no special need to retry with the same data, plus the user can just 
	 * press the button to retry.
	 * There is no need to exit the activity, the user can decide to retry.
	 */
	protected class SetupAppRespHandler extends DisplayDialogOnError<Application> {
		public SetupAppRespHandler() {
			super(SetupAppActivity.this, "Echec");
		}

		@Override
		public void onSuccess(Application app) {
			Log.d(LOG_TAG, "Key : "+app.app_key+" "+app.app_name);
			PaulineActivity.setKey(SetupAppActivity.this, app.app_key);
			PaulineActivity.setAppName(SetupAppActivity.this, app.app_name);
			stop(true);
		}
	}
	
    protected class SetupApplicationTask extends ApiTask<Application> {
    	protected String appUrl, appName;
    	protected KEY key;
    	protected Application app = null;
    	
    	
    	public SetupApplicationTask(String app_url, String app_name,
    			ResponseHandler<Application> handler) {
    		super(SetupAppActivity.this, "SetupApplicationTask", 
    				"Installation de l'application...", handler);
    		appUrl = app_url;
    		appName = app_name;
    		key = new KEY(getString(R.string.api_url) + "KEY");
    	}
    	
    	@Override
    	protected Application callSoap() throws Exception {
    		Application app = key.registerApplication(appUrl, appName);
    		if (app == null) { //should never happen
    			throw new Exception("L'application récupérée est vide");
    		}
    		if (app.app_key == null || app.app_key.isEmpty()) {
				throw new Exception("La key retournée n'est pas valide : "+app.app_key);
			}
    		return app;
    	}
    }
}
