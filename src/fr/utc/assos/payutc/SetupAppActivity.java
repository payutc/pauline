package fr.utc.assos.payutc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.KEY;
import fr.utc.assos.payutc.api.KEY.Application;

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
	
	public void onResultSetupTask(Application app, String errorMessage) {
		if (app != null) {
			if (app.app_key == null || app.app_key.isEmpty()) {
				errorMessage = "La key retournée n'est pas valide : "+app.app_key;
			}
		}
		if (errorMessage != null && !errorMessage.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Échec")
    			.setMessage(errorMessage)
    			.setNegativeButton("J'ai compris", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }});
    		builder.create().show();
		}
		else {
			Log.d(LOG_TAG, "Key : "+app.app_key+" "+app.app_name);
			PaulineActivity.setKey(this, app.app_key);
			PaulineActivity.setAppName(this, app.app_name);
			stop(true);
		}
	}
	
	public void run(View v) {
		EditText et = (EditText)findViewById(R.id.app_name);
		String app_name = et.getText().toString();
		(new SetupApplicationTask(PaulineActivity.CAS_SERVICE, app_name)).execute();
	}

    private class SetupApplicationTask extends ApiTask<Integer, Integer, Object> {
    	protected String appUrl, appName;
    	protected KEY key;
    	protected Application app = null;
    	
    	
    	public SetupApplicationTask(String app_url, String app_name) {
    		super("SetupApplicationTask", SetupAppActivity.this, 
    				"Installation de l'application...");
    		appUrl = app_url;
    		appName = app_name;
    		key = new KEY(PaulineActivity.KEY_API_URL);
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		app = key.registerApplication(appUrl, appName);
    		return true;
    	}
		
		@Override
		protected void onPostExecute(Object osef) {
			super.onPostExecute(osef);
			Log.d(SetupAppActivity.LOG_TAG, "result SetupApplicationTask : "+app);
			String lastExceptionMessage = null;
			if (lastException!=null) {
				lastExceptionMessage = lastException.getMessage();
			}
			onResultSetupTask(app, lastExceptionMessage);
		}
    }
    
}
