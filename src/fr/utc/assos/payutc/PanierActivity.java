package fr.utc.assos.payutc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
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
        
        mAdapter = new ArrayAdapter<Item>(this, R.layout.list_item, mSession.getItems()) {
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	//Log.d(LOG_TAG, "getView #"+position+" "+convertView);
	        	Item item = getItem(position);
	            View v;
	            if (convertView == null) {  // if it's not recycled, initialize some attributes
	            	LayoutInflater li = (LayoutInflater)this.getContext().getSystemService
	            		      (Context.LAYOUT_INFLATER_SERVICE);
	    			v = li.inflate(R.layout.panier_list_item, null);
	            } else {
	            	v = convertView;
	            }
	            TextView name = (TextView)v.findViewById(R.id.item_name);
	            TextView cost = (TextView)v.findViewById(R.id.item_cost);
	    		ImageView imageView = (ImageView)v.findViewById(R.id.item_image);
	    		name.setText(item.getName());
	    		cost.setText(""+item.getCost());
	            Bitmap img = item.getImg();
	            if (img == null) {
	            	imageView.setImageResource(R.drawable.ic_launcher);
	            }
	            else {
	            	imageView.setImageBitmap(img);
	            }
	            
	            return v;
	        }
        };
        
        lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Item i = mAdapter.getItem(position);
                Toast.makeText(PanierActivity.this, "" + i.getId(), Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    private void stop() {
    	
    }
}
