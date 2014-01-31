package fr.utc.assos.payutc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.utc.assos.payutc.adapters.IconAdapter;
import fr.utc.assos.payutc.adapters.ListItemAdapter;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.ResponseHandler;
import fr.utc.assos.payutc.api.POSS.TransactionResult;
import fr.utc.assos.payutc.api.responsehandler.DisplayDialogOnError;
import fr.utc.assos.payutc.views.PanierSummary;


/**
 * Afficher les articles que l'on peut acheter
 * 
 * @author thomas
 *
 */
public class ShowArticleActivity extends BaseActivity {
	private static final String LOG_TAG = "ShowArticleActivity";
	
	private static final int CONFIRM_PAYMENT 	= 1;
	
	protected static ArrayList<Item> cachedArticles = new ArrayList<Item>();

	ArrayAdapter<Item> mPanierAdapter;
	IconAdapter mGridAdapter;
	
	PanierSummary mPanierSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ShowArticleActivity");
        setContentView(R.layout.showarticles);
        
        mPanierSummary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
        
        new GetItemsTask(new GetItemsTaskRespHandler()).execute();
        /* Decommente pour remplir manuellement les articles
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item(42, "coca", "abc", 1, 30));
        items.add(new Item(42, "biere", "abc", 1, 40));
        items.add(new Item(42, "orangina", "abc", 1, 50));
        items.add(new Item(42, "pepsi", "abc", 1, 60));
        initGridView(items);
    	new DownloadImgTask().execute(items.toArray(new Item[items.size()]));
        //*/
        
        initListView();

		ListView lv = (ListView)findViewById(R.id.panier_list);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Item i = mPanierAdapter.getItem(position);
				mPanierAdapter.remove(i);
				//mSession.removeItem(i);
	        	
				mPanierSummary.set(mSession);
			}
		});
    }
    
    protected void initListView() {
		ListView lv = (ListView)findViewById(R.id.panier_list);
		mPanierAdapter = new ListItemAdapter(this, R.layout.list_item, mSession.getItems());
        lv.setAdapter(mPanierAdapter);
    }
    
    protected void initGridView(ArrayList<Item> items) {
    	mGridAdapter = new IconAdapter(this, R.layout.icon, items);
        
        GridView gridview = (GridView) findViewById(R.id.show_articles_view);
        gridview.setAdapter(mGridAdapter);
        
        
        gridview.setOnItemClickListener(mOnItemClickListener);
    }
    
    protected void initPanierView() {
        PanierSummary summary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
		summary.set(mSession);
    }
    
    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        	Item i = (Item) parent.getAdapter().getItem(position);

        	// ajouter l'item dans le panier
        	i.incQuantity();
        	mGridAdapter.notifyDataSetChanged();
            mSession.addItem(i);
            
        	// affichage du nouveaux résumé
            PanierSummary summary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
            summary.set(mSession);
        }
    };
    
    /*
     * No retry
     * No exit
     * This is not critic, the use can go back and try again, maybe later we should
     * allow retry and exit activity on failure.
     */
    protected class GetItemsTaskRespHandler extends DisplayDialogOnError<ArrayList<Item>> {

		public GetItemsTaskRespHandler() {
			super(ShowArticleActivity.this, getString(R.string.articles_failed));
			againListener = new DialogInterface.OnClickListener() {
 	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	        	   new GetItemsTask(GetItemsTaskRespHandler.this).execute();
	           }
	       };
		}

		@Override
		public void onSuccess(ArrayList<Item> items) {
			// Cache the list of articles
			cachedArticles = items;
			
			// init grid (without images)
			initGridView(items);
			// then download the images (async)
	    	new DownloadImgTask(mGridAdapter, PaulineActivity.imageCache)
	    			.execute(items.toArray(new Item[items.size()]));
		}
    }
    
    protected class GetItemsTask extends ApiTask<ArrayList<Item>> {
    	
    	public GetItemsTask(ResponseHandler<ArrayList<Item>> handler) {
    		super(ShowArticleActivity.this, getString(R.string.loading), 
    				getString(R.string.articles_doing), handler);
    	}
    	
		@Override
		protected ArrayList<Item> callSoap() throws Exception {
			ArrayList<Item> items = PaulineActivity.POSS.getArticles(mSession.getFunId());
			if (items == null) { // should never happen
				throw new Exception("items est vide");
			}
			return items;
		}
    }
    
    public Bitmap getImageFromCache(int id) throws FileNotFoundException {
    	File cacheDir = getCacheDir();
    	String cacheDirPath = cacheDir.getAbsolutePath();
		return BitmapFactory.decodeFile(cacheDirPath+"/"+id+".png");
    }
    
    public void saveImageToCache(int id, Bitmap im) throws FileNotFoundException {
    	File cacheDir = getCacheDir();
    	String cacheDirPath = cacheDir.getAbsolutePath();
    	FileOutputStream fOut = new FileOutputStream(cacheDirPath+"/"+id+".png");
    	im.compress(Bitmap.CompressFormat.PNG, 85, fOut);
    }
    
    public void onClickProducts(View view) {
    	Log.d(LOG_TAG,"onClickProducts");
    	loadProductView();
    }
    
    public void loadProductView() {
    	Log.d(LOG_TAG,"onClickProducts");
    	ImageButton ib = (ImageButton) findViewById(R.id.button_products);
    	ib.setVisibility(View.GONE);
    	TextView tv = (TextView) findViewById(R.id.panier_empty);
    	tv.setVisibility(View.GONE);
    	tv = (TextView) findViewById(R.id.panier_help);
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


		initListView();
        
		switch (requestCode) {
		case CONFIRM_PAYMENT:
	    	if (resultCode == RESULT_OK) {
	    		emptyItems();
	    		loadProductView();
	    		Toast.makeText(this, getString(R.string.transaction_ok), Toast.LENGTH_SHORT).show();
	    	}
		}

		initPanierView();
    }
	
	public static ArrayList<Item> getCachedArticlesList() {
		return cachedArticles;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.showarticles_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_empty:
	            emptyItems();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void emptyItems() {
		mSession.clearItems();
		for(int i = 0; i < mGridAdapter.getCount(); i++){
			Item item = mGridAdapter.getItem(i);
			item.setQuantity(0);
		}
		mGridAdapter.notifyDataSetChanged();
		initPanierView();
	}
    
    protected void startTransaction(String id) {
    	Log.d(LOG_TAG,"startTransaction");
    	mSession.setBuyerId(id);
    	new TransactionTask(mSession.getBuyerId(), mSession.getItems(),
    			new TransactionResponseHandler()).execute();
    }
    
    @Override
    protected void onIdentification(String id) {
    	Log.d(LOG_TAG, "onIdentification");
    	if(mSession.getNbItems() > 0) {
    		startTransaction(id);    		
    	}
    	else {
    		super.onIdentification(id);
    	}
    }
    
    
    /*
     * Custom handler
     * 
     * In case of failure, paint screen in red, vibrate and display a dialog.
     * In case of success, paint screen in green, vibrate (diff than on error) and display a Toast.
     */
    protected class TransactionResponseHandler implements ResponseHandler<TransactionResult> {

		@Override
		public void onError(Exception ex) {
			Vibrator v = (Vibrator) getSystemService(ShowArticleActivity.VIBRATOR_SERVICE);
	        final View screen = findViewById(R.id.show_articles_view);
			long[] pattern = {
    		    0,  // Start immediately
    		    300, 100, 300
    		};
    		v.vibrate(pattern, -1);
    		screen.setBackgroundColor(0xffff0000);
    		AlertDialog.Builder builder = new AlertDialog.Builder(ShowArticleActivity.this);
    		builder.setTitle(getString(R.string.transaction_failed))
    			.setMessage(ex.getMessage())
    			.setCancelable(false)
    			.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
   		        			screen.setBackgroundColor(0xffffffff);
    		                dialog.cancel();
    		           }});
    		
    		builder.create().show();
		}
		
		@Override
		public void onSuccess(TransactionResult _response) {
			Vibrator v = (Vibrator) getSystemService(ShowArticleActivity.VIBRATOR_SERVICE);
	        final View screen = findViewById(R.id.show_articles_view);
    		// Only perform this pattern one time (-1 means "do not repeat")
    		v.vibrate(300);
    		
	        ObjectAnimator colorFade = ObjectAnimator.ofObject(
	        					screen, 
	        					"backgroundColor", 
	        					new ArgbEvaluator(),
	        					0xffffffff, 0xff00ff00, 0xffffffff);
			colorFade.setDuration(700);
    		colorFade.setIntValues();
            colorFade.start();
            
            if (mSession.getHomeChoice() == PaulineSession.VENTE_LIBRE) {
    			emptyItems();
    		}
            
			Toast.makeText(ShowArticleActivity.this, getString(R.string.transaction_ok), Toast.LENGTH_SHORT).show();
		}
    }
    
    protected class TransactionTask extends ApiTask<TransactionResult> {
    	private ArrayList<Integer> itemIds;
    	private String buyerId;
    	
    	public TransactionTask(String buyerId, ArrayList<Item> items, 
    			TransactionResponseHandler respHandler) {
    		super(ShowArticleActivity.this, getString(R.string.transaction),
    				getString(R.string.transaction_doing), respHandler);
    		itemIds = new ArrayList<Integer>();
			for (int i=0; i<items.size(); ++i) {
				Item item = items.get(i);
				itemIds.add(item.getId());
			}
    		this.buyerId = buyerId;
    	}
    	
    	
		@Override
		protected TransactionResult callSoap() throws Exception {
			return PaulineActivity.POSS.transaction(mSession.getFunId(), buyerId, itemIds);
		}
    }
}
