package fr.utc.assos.payutc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.utc.assos.payutc.adapters.ListItemAdapter;
import fr.utc.assos.payutc.views.PanierSummary;

public class PanierActivity extends BaseActivity {
	private static final String LOG_TAG		= "PanierActivity";
	
	ArrayAdapter<Item> mAdapter;
	
	private PanierSummary mPanierSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PanierActivity");
        setContentView(R.layout.panier);

        mPanierSummary = (PanierSummary) findViewById(R.id.panier_panier_summary);
        mPanierSummary.set(mSession);
        
        ListView lv = (ListView)findViewById(R.id.panier_list);
        
        mAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        
        lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Item i = mAdapter.getItem(position);
				mAdapter.remove(i);
				mSession.removeItem(i);
				mPanierSummary.set(mSession);
			}
		});
    }
    
    /**
     * Override le bouton back pour qu'il sauvegarde l'état actuel du panier
     */
    @Override
    public void onBackPressed() {
    	stop(RESULT_OK);
    }
}
