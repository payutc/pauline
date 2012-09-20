package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fr.utc.assos.payutc.adapters.IconAdapter;
import fr.utc.assos.payutc.adapters.ListItemAdapter;
import fr.utc.assos.payutc.soap.SoapTask;
import fr.utc.assos.payutc.views.PanierSummary;





/**
 * Afficher les articles que l'on peut acheter
 * 
 * @author thomas
 *
 */
public class ShowArticleActivity extends BaseActivity {
	private static final String LOG_TAG = "ShowArticleActivity";
	
	private static final int PANIER				= 0;
	private static final int CONFIRM_PAYMENT 	= 1;

	ArrayAdapter<Item> mAdapter;
	
	PanierSummary mPanierSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ShowArticleActivity");
        setContentView(R.layout.showarticles);
        
        mPanierSummary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
        
        new GetItemsTask().execute();
        
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
    
    protected void initGridView(ArrayList<Item> items) {
    	IconAdapter adapter = new IconAdapter(this, R.layout.icon, items);
        
        GridView gridview = (GridView) findViewById(R.id.show_articles_view);
        gridview.setAdapter(adapter);
        
        
        gridview.setOnItemClickListener(mOnItemClickListener);
    }
    
    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        	Item i = (Item) parent.getAdapter().getItem(position);

        	// ajouter l'item dans le panier
            mSession.addItem(i);
            
        	// affichage du nouveaux résumé
            PanierSummary summary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
            summary.set(mSession);
        }
    };
    
    private class GetItemsTask extends SoapTask {
    	private ArrayList<Item> mItems=null;
    	
    	public GetItemsTask() {
    		super("Chargement", ShowArticleActivity.this, 
    				"Veuillez patienter", 2);
    	}
    	
		@Override
		protected boolean callSoap() throws Exception {
			mItems = PaulineActivity.PBUY.getArticles();
			return mItems != null;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
	        if (mItems == null) {
	        	Log.e(LOG_TAG, "Error:soap return null");
	        }
	        else {
	        	initGridView(mItems);
	        }
		}
    	
    }
    
    public void onClickCancel(View view) {
    	stop();
    }
    
    public void onClickOk(View view) {
    	startConfirmPaymentActivity();
    }
    
    private void startConfirmPaymentActivity() {
    	Log.d(LOG_TAG,"startConfirmPaymentActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ConfirmPaymentActivity.class);
    	startActivityForResult(intent, CONFIRM_PAYMENT);
    }
    
    public void onClickPanier(View view) {
    	Log.d(LOG_TAG,"onClickPanier");
    	/*Intent intent = new Intent(this, fr.utc.assos.payutc.PanierActivity.class);
    	startActivityForResult(intent, PANIER);*/
    	ImageButton ib = (ImageButton) findViewById(R.id.button_panier);
    	ib.setVisibility(View.GONE);
    	ib = (ImageButton) findViewById(R.id.button_products);
    	ib.setVisibility(View.VISIBLE);

    	GridView gv = (GridView) findViewById(R.id.show_articles_view);
    	gv.setVisibility(View.GONE);
    	ListView lv = (ListView) findViewById(R.id.panier_list);
    	lv.setVisibility(View.VISIBLE);
    	
    	TextView tv = (TextView) findViewById(R.id.panier_help);
    	tv.setVisibility(View.VISIBLE);
    }
    
    public void onClickProducts(View view) {
    	Log.d(LOG_TAG,"onClickProducts");
    	ImageButton ib = (ImageButton) findViewById(R.id.button_products);
    	ib.setVisibility(View.GONE);
    	ib = (ImageButton) findViewById(R.id.button_panier);
    	ib.setVisibility(View.VISIBLE);

    	TextView tv = (TextView) findViewById(R.id.panier_help);
    	tv.setVisibility(View.GONE);
    	ListView lv = (ListView) findViewById(R.id.panier_list);
    	lv.setVisibility(View.GONE);
    	GridView gv = (GridView) findViewById(R.id.show_articles_view);
    	gv.setVisibility(View.VISIBLE);
    	

    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode + " " +RESULT_OK);
		
		switch (requestCode) {
		case CONFIRM_PAYMENT:
	    	if (resultCode == RESULT_OK) {
	    		mSession.clearItems();
	    	}
		}

        PanierSummary summary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
		summary.set(mSession);
    }

	/*private class DownloadImgTask extends AsyncTask<Item, Integer, Integer> {
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
	 }*/
}
