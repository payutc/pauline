package fr.utc.assos.payutc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends Activity {
	public final static String LOG_TAG		= "HomeActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate HomeActivity");
    }
    
    private void startShowArticleActivity () {
    	Log.d(LOG_TAG,"startShowArticleActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
    	startActivity(intent);
    }
}
