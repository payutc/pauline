package fr.utc.assos.payutc;

import java.util.ArrayList;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fr.utc.assos.payutc.adapters.ListItemAdapter;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.POSS.CustomerDetails;
import fr.utc.assos.payutc.api.POSS.Purchase;

public class CustomerInfosActivity extends BaseActivity {
	public final static String LOG_TAG = "BuyerInfoActivity";
	
	protected String mCustomerId;
	protected ListItemAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_infos);
        Log.d(LOG_TAG, "onCreate BuyerInfoActivity");
        mCustomerId = getIntent().getExtras().getString("CustomerId");
        
        ListView lv = (ListView)findViewById(R.id.transactions_list);
        
        mAdapter = new ListItemAdapter(this, R.layout.list_item, new ArrayList<Item>());
        lv.setAdapter(mAdapter);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Item i = mAdapter.getItem(position);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(CustomerInfosActivity.this);
		    	builder.setTitle("Annuler la vente #"+i.getId())
		    		   .setMessage("Annuler la vente #"+i.getId()+" "+i.getName()+" "+Item.costToString(i.getCost()/100.0)+ " ?")
		    	       .setCancelable(false)
		    	       .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   new cancelTransaction(i);
		    	        	   dialog.cancel();
		    	           }
		    	       })
		    	       .setNegativeButton("Non", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   dialog.cancel();
		    	           }
		    	       });
		    	AlertDialog alert = builder.create();
		    	alert.show();
			}
		});
        refresh();
    }
    
    public void onClickBack(View view) {
    	stop();
    }
    
    public void onClickRefresh(View view) {
		new GetCustomerDetails(mCustomerId).execute();
    }
    
    public void refresh() {
		new GetCustomerDetails(mCustomerId).execute();
    }
    
	protected void onGetCustomerDetailsFails(Exception e) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		stop();
	}
	
	protected void onGetCustomerDetailsSuccess(CustomerDetails details, ArrayList<Item> items) {
		TextView username = (TextView) findViewById(R.id.username);
		TextView money = (TextView) findViewById(R.id.money);
		username.setText(details.firstname+" "+details.lastname);
		money.setText(Item.costToString(details.solde/100.0));
		
		Set<String> keys = details.last_purchase.keySet();
		ArrayList<Item> l = new ArrayList<Item>();
		for (String key : keys) {
			Purchase p = details.last_purchase.get(key);
			// We will use item.id to store the purchase id, little hack...
			Item item = new Item(p.pur_id, "???????", "??????", 0, p.pur_price);
			// Could be optimized by using an hashmap, but since there is never
			// lot of items, this optimization is probably useless
			for (Item i : items) {
				if (i.getId() == p.obj_id) {
					item.setName(i.getName());
					item.setIdImg(i.getIdImg());
					break;
				}
			}
			l.add(item);
		}

		mAdapter.clear();
		mAdapter.addAll(l);
		
        new DownloadImgTask(mAdapter, PaulineActivity.imageCache).execute(l.toArray(new Item[l.size()]));
	}
	
	protected void onCancelTransactionResult(Item i, Exception lastException) {
		String title, message;
		if (lastException == null) {
			title = "Annulation enregistrée";
			message = "La transaction a été annulée";
		}
		else {
			title = "Echec";
			message = "Une erreur est survenue." + lastException.getMessage();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(message);
		
		builder.create().show();
	}
	
	protected class GetCustomerDetails extends ApiTask<String,Integer,Integer> {
		
		protected String mId;
		CustomerDetails mDetails = null;
		private ArrayList<Item> mItems = null;
		
		public GetCustomerDetails(String id) {
			super("Récupération des infos", CustomerInfosActivity.this, "Un instant s'il vous plait");
			mId = id;
		}

		@Override
		protected boolean callSoap() throws Exception {
			// @TODO, could be optimized, get article each time just to
			// get the name is slow... Maybe the server should return the
			// name of the objects directly
			mDetails = PaulineActivity.POSS.getCustomerDetails(mId);
			mItems = PaulineActivity.POSS.getArticles();
			return mDetails != null && mItems != null;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
			if (mDetails==null || mItems==null) {
				onGetCustomerDetailsFails(lastException);
			}
			else {
				onGetCustomerDetailsSuccess(mDetails, mItems);
			}
		}
	}

	protected class cancelTransaction extends ApiTask<String,Integer,Integer> {
		Item item;
		public cancelTransaction(Item item) {
			super("Récupération des infos", CustomerInfosActivity.this, "Un instant s'il vous plait");
			this.item = item;
		}

		@Override
		protected boolean callSoap() throws Exception {
			// @TODO, could be optimized, get article each time just to
			// get the name is slow... Maybe the server should return the
			// name of the objects directly
			PaulineActivity.POSS.cancelTransaction(item.getId());
			return true;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
			onCancelTransactionResult(item, lastException);
		}
	}
	
}
