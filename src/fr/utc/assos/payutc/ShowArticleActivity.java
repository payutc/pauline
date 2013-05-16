package fr.utc.assos.payutc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

	ArrayAdapter<Item> mPanierAdapter;
	IconAdapter mGridAdapter;
	
	PanierSummary mPanierSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ShowArticleActivity");
        setContentView(R.layout.showarticles);
        
        mPanierSummary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
        
        new GetItemsTask().execute();
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

	        	ImageButton ib = (ImageButton) findViewById(R.id.button_panier);
	        	if (mSession.getNbItems()<1) {
	        		ib.setImageResource(R.drawable.panier);
	        	}
	        	
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
            mSession.addItem(i);

        	ImageButton ib = (ImageButton) findViewById(R.id.button_panier);
        	if (mSession.getNbItems()==1) {
        		ib.setImageResource(R.drawable.panierfull);
        	}
            
        	// affichage du nouveaux résumé
            PanierSummary summary = (PanierSummary) findViewById(R.id.show_articles_panier_summary);
            summary.set(mSession);
        }
    };
    
    protected void onGetItemsFails(Exception e) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Impossible de recuperer les articles")
    		   .setMessage(e.getMessage())
    	       .setCancelable(false)
    	       .setPositiveButton("Encore !", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   dialog.cancel();
    	        	   new GetItemsTask().execute();
    	           }
    	       })
    	       .setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	                	ShowArticleActivity.this.finish();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private class GetItemsTask extends ApiTask<Integer, Integer, Integer> {
    	private ArrayList<Item> mItems=null;
    	
    	public GetItemsTask() {
    		super("Chargement", ShowArticleActivity.this, 
    				"Veuillez patienter");
    	}
    	
		@Override
		protected boolean callSoap() throws Exception {
			mItems = PaulineActivity.POSS.getArticles();
			return mItems != null;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
	        if (mItems == null) {
	        	onGetItemsFails(lastException);
	        }
	        else {
	        	onGetItemsSuccess(mItems);
	        }
		}
    	
    }
    
    protected void onGetItemsSuccess(ArrayList<Item> items) {
    	initGridView(items);
    	new DownloadImgTask(mGridAdapter, PaulineActivity.imageCache).execute(items.toArray(new Item[items.size()]));
    	/*
    	for (Item i : items) {
    		Bitmap im = null;
    		try {
    			im = getImageFromCache(i.getIdImg());
    		}
    		catch (Exception _e) {}
    		if (im==null) {
    			new DownloadImgTask(mGridAdapter).execute(i);
    		}
    		else {
				i.setImage(im);
				mGridAdapter.notifyDataSetChanged();
    		}
    		break;
    	}
    	*/
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
    	ImageButton ib = (ImageButton) findViewById(R.id.button_panier);
    	ib.setVisibility(View.GONE);
    	GridView gv = (GridView) findViewById(R.id.show_articles_view);
    	gv.setVisibility(View.GONE);

    	ib = (ImageButton) findViewById(R.id.button_products);
    	ib.setVisibility(View.VISIBLE);
    	if (mSession.getNbItems()>0) {
	    	ListView lv = (ListView) findViewById(R.id.panier_list);
	    	lv.setVisibility(View.VISIBLE);
	    	TextView tv = (TextView) findViewById(R.id.panier_help);
	    	tv.setVisibility(View.VISIBLE);
    	}
    	else {
	    	TextView tv = (TextView) findViewById(R.id.panier_empty);
	    	tv.setVisibility(View.VISIBLE);
    	}
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
    	
    	ib = (ImageButton) findViewById(R.id.button_panier);
    	ib.setVisibility(View.VISIBLE);
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
	    		mSession.clearItems();
	        	ImageButton ib = (ImageButton) findViewById(R.id.button_panier);
	        	ib.setImageResource(R.drawable.panier);
	    		loadProductView();
	    		Toast.makeText(this, R.string.success_transaction, Toast.LENGTH_SHORT).show();
	    	}
		}

		initPanierView();
    }
}
