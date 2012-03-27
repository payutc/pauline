package fr.utc.assos.payutc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fr.utc.assos.payutc.soap.PBuy;

public class AskSellerPasswordActivity extends Activity {
	public final static String LOG_TAG = "AskSellerPasswordActivity";
	private final int MEAN_OF_LOGIN		= 4; 
	
	
	private Nfc myNfc = new Nfc();

	public PBuy pbuy;
	
	String id_seller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate AskSellerPasswordActivity");
        myNfc.onCreate(getApplicationContext());

        Bundle b = getIntent().getExtras();
        id_seller = b.getString("id");		// récupération du login renvoyé par le nfc
        Log.d(LOG_TAG, "badge #"+id_seller);

        pbuy = new PBuy();
        setContentView(R.layout.asksellerpassword);
    }
    
    public void onClick(View view) {
    	pbuy.loadSeller(id_seller, MEAN_OF_LOGIN, "1234", PaulineActivity.ID_POI);
    }

    protected void stop(Boolean success) {
		int r_code = RESULT_OK;
		if (!success) {
			r_code = -1;
		}
	    setResult(r_code);
		finish();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
    	myNfc.onResume(getBaseContext(), this, getClass());
	}
    
	@Override
	protected void onPause() {
		super.onPause();
    	myNfc.onPause(this);
	}
}
