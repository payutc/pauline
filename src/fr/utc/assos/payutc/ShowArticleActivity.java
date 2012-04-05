package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import fr.utc.assos.payutc.soap.GetImageResult;
import fr.utc.assos.payutc.soap.GetPropositionResult;

public class ShowArticleActivity extends NfcActivity {
	private static final String LOG_TAG = "ShowArticleActivity";
	
	public int type;
	
	private ImageAdapter adapter;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ShowArticleActivity");
        setContentView(R.layout.showarticles);
        Bundle b = getIntent().getExtras();
        type = b.getInt("type", HomeActivity.VENTE_LIBRE);

        ArrayList<Item> items = getItems();
        adapter = new ImageAdapter(this, items);

        for (Item item : items) {
        	new DownloadImgTask(adapter).execute(item);
        }
        
        GridView gridview = (GridView) findViewById(R.id.show_articles_view);
        gridview.setAdapter(adapter);
        
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Item i = adapter.getItem(position);
                Toast.makeText(ShowArticleActivity.this, "" + i.getCost(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private ArrayList<Item> getItems() {
    	GetPropositionResult propositionResult = PaulineActivity.PBUY.getProposition();
        ArrayList<Item> items = new ArrayList<Item>();
        if (propositionResult == null) {
        	Log.e(LOG_TAG, "Error:soap return null");
        }
        else if (propositionResult.getErrorCode() != 0) {
        	Log.e(LOG_TAG, "Error:"+propositionResult.getErrorCode());
            Toast.makeText(ShowArticleActivity.this, "Error:"+propositionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
        else {
        	items = propositionResult.getItems();
        }
        
        return items;
    }
    
    protected void stop() {
		Intent intent = new Intent();
        setResult(RESULT_OK, intent);
		finish();
    }
    
    public void onClickCancel(View view) {
    	stop();
    }
    
    public void onClickOk(View view) {
    	startConfirmPaymentActivity(42);
    }
    
    private void startConfirmPaymentActivity(int prix) {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ConfirmPaymentActivity.class);
    	Bundle b = new Bundle();
    	b.putInt("prix", prix);
    	intent.putExtras(b);
    	startActivityForResult(intent, 0);
    }
    

	private class DownloadImgTask extends AsyncTask<Item, Integer, Integer> {
		private final static String LOG_TAG		= "DownloadImg";
		ImageAdapter mAdapter;
		
		public DownloadImgTask(ImageAdapter adapter) {
			mAdapter = adapter;
		}

		@Override
		protected Integer doInBackground(Item... items) {
			Item item = items[0];
			GetImageResult result = PaulineActivity.PBUY.getImage(item.getIdImg());
			if (result == null) {
				Log.e(LOG_TAG, "Error (img#"+item.getIdImg()+"): soap return null");
			}
			else if (result.getErrorCode() != 0) {
				Log.e(LOG_TAG, "Error (img#"+item.getIdImg()+"):"+result.getErrorCode());
			}
			else {
				byte[] decodedString = Base64.decode(result.getEncoded(), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				item.setmImage(decodedByte);
			}
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			mAdapter.notifyDataSetChanged();
		}
	 }
}
