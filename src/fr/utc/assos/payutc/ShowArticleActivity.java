package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import fr.utc.assos.payutc.soap.GetImageResult;
import fr.utc.assos.payutc.soap.GetPropositionResult;





/**
 * Afficher les articles que l'on peut acheter
 * 
 * @author thomas
 *
 */
public class ShowArticleActivity extends NfcActivity {
	private static final String LOG_TAG = "ShowArticleActivity";
	
	private static final int PANIER				= 0;
	private static final int CONFIRM_PAYMENT 	= 1;
	
	private IconAdapter adapter;
	
	private PaulineSession mSession;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ShowArticleActivity");
        setContentView(R.layout.showarticles);
        mSession = PaulineSession.get(getIntent());
        
        new GetItemsTask().execute();
    }
    
    protected void initGridView(ArrayList<Item> items) {
    	adapter = new IconAdapter(this, items);

        for (Item item : items) {
        	new DownloadImgTask(adapter).execute(item);
        }
        
        GridView gridview = (GridView) findViewById(R.id.show_articles_view);
        gridview.setAdapter(adapter);
        
        
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Item i = adapter.getItem(position);
            	
            	// récupération du prix courant
            	TextView tv = (TextView) findViewById(R.id.show_articles_prix);
            	String sCurrentPrix = (String) tv.getText();
            	Float fCurrentPrix = Float.parseFloat(sCurrentPrix.substring(0, sCurrentPrix.length()-1));
            	
            	// affichage du nouveaux prix
            	tv.setText(""+(Math.round(fCurrentPrix*100.0)+i.getCost())/100.0+"€");
            	
            	// ajouter l'item dans le panier
                mSession.addItem(i);

                Toast.makeText(ShowArticleActivity.this, "" + i.getCost(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    protected void setProgressPercent(int p) {
    	
    }
    
    private class GetItemsTask extends AsyncTask<Integer, Integer, GetPropositionResult> {
    	
    	private ProgressDialog mProgressDialog;
    	
		@Override
		protected GetPropositionResult doInBackground(Integer... params) {
			return PaulineActivity.PBUY.getProposition();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	    	mProgressDialog = ProgressDialog.show(ShowArticleActivity.this, 
	    			"Chargement",
	    			"Veuillez patienter"
	    	);
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			setProgressPercent(progress[0]);
	    }
		
		@Override
		protected void onPostExecute(GetPropositionResult result) {
			ArrayList<Item> items = new ArrayList<Item>();
	        if (result == null) {
	        	Log.e(LOG_TAG, "Error:soap return null");
	        }
	        else if (result.getErrorCode() != 0) {
	        	Log.e(LOG_TAG, "Error:"+result.getErrorCode());
	            Toast.makeText(ShowArticleActivity.this, "Error:"+result.getErrorCode(), Toast.LENGTH_SHORT).show();
	        }
	        else {
	        	items = result.getItems();
	        }
	        
	        initGridView(items);
	        mProgressDialog.dismiss();
		}
    	
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
    	startConfirmPaymentActivity();
    }
    
    private void startConfirmPaymentActivity() {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ConfirmPaymentActivity.class);
    	mSession.save(intent);
    	startActivityForResult(intent, CONFIRM_PAYMENT);
    }
    
    public void onClickPanier(View view) {
    	Log.d(LOG_TAG,"startAskSellerPassword");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.PanierActivity.class);
    	mSession.save(intent);
    	startActivityForResult(intent, PANIER);
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode + " " +RESULT_OK);
		switch (requestCode) {
		case CONFIRM_PAYMENT:
	    	if (resultCode == RESULT_OK) {
	    		mSession.clearItems();
	    	}
		}
    }

	private class DownloadImgTask extends AsyncTask<Item, Integer, Integer> {
		private final static String LOG_TAG		= "DownloadImg";
		IconAdapter mAdapter;
		
		public DownloadImgTask(IconAdapter adapter) {
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
				item.setmImage(result.getEncoded());
			}
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			mAdapter.notifyDataSetChanged();
		}
	 }
}
