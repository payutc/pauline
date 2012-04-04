package fr.utc.assos.payutc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AskSellerPasswordActivity extends NfcActivity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	private final int MEAN_OF_LOGIN		= 5; 
	
	

	
	String id_seller;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");

        Bundle b = getIntent().getExtras();
        id_seller = b.getString("id");		// récupération du login renvoyé par le nfc
        Log.d(LOG_TAG, "badge #"+id_seller);

        setContentView(R.layout.asksellerpassword);
    }
    
    public void onOk(View view) {
    	String pass = (String) ((TextView) findViewById(R.id.input_login)).getText();
    	int r = pbuy.loadSeller(id_seller, MEAN_OF_LOGIN, pass, PaulineActivity.ID_POI);
    	if (r==1) {
    		stop(true);
    	}
    	else {
    		stop(false);
    	}
    }
    
    public void onCancel(View view) {
    	stop(false);
    }

    protected void stop(Boolean success) {
		if (success) {
			setResult(RESULT_OK);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		finish();
    }
}
