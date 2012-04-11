package fr.utc.assos.payutc;

import fr.utc.assos.payutc.adapters.ListItemAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PanierActivity extends NfcActivity {
	private static final String LOG_TAG		= "PanierActivity";
	
	private PaulineSession mSession;
	ArrayAdapter<Item> mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate PanierActivity");
        setContentView(R.layout.home);
        mSession = PaulineSession.get(getIntent());
        
        ListView lv = (ListView)findViewById(R.id.list_view);
        
        mAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        
        lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Item i = mAdapter.getItem(position);
                Toast.makeText(PanierActivity.this, "" + i.getId(), Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    protected void stop() {
		Intent intent = new Intent();
		mSession.save(intent);
        setResult(RESULT_OK, intent);
        finish();
    }
}
