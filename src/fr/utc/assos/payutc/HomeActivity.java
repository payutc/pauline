package fr.utc.assos.payutc;

import android.app.ListActivity;
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


public class HomeActivity extends ListActivity {
	public final static String LOG_TAG		= "HomeActivity";
	
	public final static int VENTE_LIBRE		= 0;
	public final static int VENTE_PRODUIT		= 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate HomeActivity");

        final String[] items = new String[] {"Vente libre", "Vente produit", "Annuler une vente"};
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));

        ListView lv = getListView();

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });
    }

    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }
    
    public void onClickVenteLibre(View view) {
    	startShowArticleActivity(VENTE_LIBRE);
    }
    
    public void onClickVenteProduit(View view) {
    	startShowArticleActivity(VENTE_PRODUIT);
    }
    
    public void onClickCancelTransaction(View view) {
    	startCancelTransactionActivity();
    }
    
    private void startShowArticleActivity(int type) {
    	Log.d(LOG_TAG,"startShowArticleActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
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
