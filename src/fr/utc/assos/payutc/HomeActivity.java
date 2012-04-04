package fr.utc.assos.payutc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HomeActivity extends NfcActivity {
	public final static String LOG_TAG		= "HomeActivity";
	
	public final static int VENTE_LIBRE			= 0;
	public final static int VENTE_PRODUIT		= 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate HomeActivity");
        setContentView(R.layout.home);
        
        ListView lv = (ListView)findViewById(R.id.home_list_view);

        final String[] items = new String[] {"Vente libre", "Vente produit", "Annuler une vente"};
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));


		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
			    	startShowArticleActivity(VENTE_LIBRE);
			    	break;
			    	
				case 1:
			    	startShowArticleActivity(VENTE_PRODUIT);
			    	break;
			    	
				case 2:
					startCancelTransactionActivity();
					break;
				
				default: break;
				}
			}
		});
    }

    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }
    
    private void startShowArticleActivity(int type) {
    	Log.d(LOG_TAG,"startShowArticleActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ShowArticleActivity.class);
    	Bundle b = new Bundle();
    	b.putInt("type", type); //Your id
    	intent.putExtras(b); //Put your id to your next Intent
    	startActivity(intent);
    }
    
    private void startCancelTransactionActivity() {
    	Log.d(LOG_TAG,"startCancelTransactionActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.CancelTransactionActivity.class);
    	startActivity(intent);
    }
    
    public void onClickCancel(View view) {
    	stop();
    }
}
