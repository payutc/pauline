package fr.utc.assos.payutc;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ConfirmPaymentActivity extends NfcActivity {
	public final static String LOG_TAG = "ConfirmPayment";
	
	private PaulineSession mSession;
	ArrayAdapter<Item> mAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ConfirmPayment");
        setContentView(R.layout.confirm);
        mSession = PaulineSession.get(getIntent());
        
        ListView lv = (ListView)findViewById(R.id.confirm_list);
        
        mAdapter = new ArrayAdapter<Item>(this, R.layout.panier_list_item, mSession.getItems()) {
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
    }

    protected void stop(Boolean ok) {
		Intent intent = new Intent();
		if (ok) {
			setResult(RESULT_OK, intent);
		}
		else {
			setResult(RESULT_CANCELED, intent);
		}
		finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (mSession.getHomeChoice() == PaulineSession.VENTE_LIBRE && resultCode == RESULT_OK) {
    		stop(true);
    	}
    }
    
    private void startResultTransaction(String id) {
    	Log.d(LOG_TAG,"startResultTransaction");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ResultTransactionActivity.class);
    	mSession.setBuyerId(id);
    	mSession.save(intent);
    	startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d(LOG_TAG, "new Intent");
    	String id = getNfcResult(intent);
    	startResultTransaction(id);
    }
    
    public void onClickDebug(View view) {
    	startResultTransaction(PaulineActivity.ID_TRECOUVR);
    }
}
