package fr.utc.assos.payutc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.utc.assos.payutc.adapters.ListItemAdapter;

public class PanierActivity extends BaseActivity {
	private static final String LOG_TAG		= "PanierActivity";
	
	ArrayAdapter<Item> mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PanierActivity");
        setContentView(R.layout.home);
        
        ListView lv = (ListView)findViewById(R.id.list_view);
        
        mAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        
        lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Item i = mAdapter.getItem(position);
				mAdapter.remove(i);
				mSession.removeItem(i);
			}
		});
    }
}
