package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
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
import fr.utc.assos.payutc.api.ResponseHandler;
import fr.utc.assos.payutc.api.POSS.CustomerDetails;
import fr.utc.assos.payutc.api.POSS.Purchase;
import fr.utc.assos.payutc.api.responsehandler.DisplayDialogOnError;

public class CustomerInfosActivity extends BaseActivity {
	public final static String LOG_TAG = "BuyerInfoActivity";
	
	protected String mCustomerId;
	protected ListItemAdapter mAdapter;
	protected int money = 0;
	
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
		    	        	   (new CancelTransaction(i)).execute();
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
		refresh();
    }
    
    public void refresh() {
		new GetCustomerDetails(this, mCustomerId, mSession.getFunId(), 
				new GetCustomerDetailsRespHandler()).execute();
    }
	
    /*
     * No retry
     * Exit activity
     * 
     * There is no need for retry button, the user can just pass again the card.
     * The activity exit because without these data the activity is useless.
     */
	protected class GetCustomerDetailsRespHandler extends DisplayDialogOnError<GetCustomerDetailsResult> {

		public GetCustomerDetailsRespHandler() {
			super(CustomerInfosActivity.this, "Impossible de récupérer des informations");
		}

		@Override
		public void onSuccess(GetCustomerDetailsResult r) {
			TextView vUsername = (TextView) findViewById(R.id.username);
			TextView vMoney = (TextView) findViewById(R.id.money);
			vUsername.setText(r.customerDestails.firstname+" "+r.customerDestails.lastname);
			money = r.customerDestails.solde;
			vMoney.setText(Item.costToString(money/100.0));
			ArrayList<Item> l = new ArrayList<Item>();
			for (Purchase p : r.customerDestails.last_purchases) {
				// We will use item.id to store the purchase id, little hack...
				Item item = new Item(p.pur_id, "??????", 0, p.pur_price);
				// Could be optimized by using an hashmap, but since there is never
				// lot of items, this optimization is probably useless
				for (Item i : r.items) {
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
		
	}

	public static class GetCustomerDetailsResult {
		public CustomerDetails customerDestails;
		public ArrayList<Item> items;
	}
	
	public static class GetCustomerDetails extends ApiTask<GetCustomerDetailsResult> {
		protected String badgeId;
		protected int funId;
		
		public GetCustomerDetails(Context ctx, String badgeId, int funId,
				ResponseHandler<GetCustomerDetailsResult> handler) {
			super(ctx, ctx.getString(R.string.userinfo),
					ctx.getString(R.string.userinfo_doing), handler);
			this.badgeId = badgeId;
			this.funId = funId;
		}

		@Override
		protected GetCustomerDetailsResult callSoap() throws Exception {
			// @TODO, could be optimized, get article each time just to
			// get the name is slow... Maybe the server should return the
			// name of the objects directly
			GetCustomerDetailsResult r = new GetCustomerDetailsResult();
			r.customerDestails = PaulineActivity.POSS.getCustomerDetails(badgeId);
			if (r.customerDestails == null) {
				throw new Exception("Aucune information sur l'utilisateur reçue");
			}
			r.items = ShowArticleActivity.getCachedArticlesList();
			if (r.items == null) {
				throw new Exception("Aucun article en cache");
			}
			return r;
		}
	}
	
	protected class CancelResponseHandler extends DisplayDialogOnError<Item> {

		public CancelResponseHandler() {
			super(CustomerInfosActivity.this, "Echec");
		}

		@Override
		public void onSuccess(Item i) {
			String message;
			message = "La transaction #" + i.getId() + " a été annulée.";
			mAdapter.remove(i);
			money += i.getCost();
			TextView vMoney = (TextView) findViewById(R.id.money);
			vMoney.setText(Item.costToString(money/100.0));
			Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
		}
		
	}

	protected class CancelTransaction extends ApiTask<Item> {
		Item item;
		public CancelTransaction(Item item) {
			super(CustomerInfosActivity.this, getString(R.string.canceltrans),
					getString(R.string.canceltrans_doing), new CancelResponseHandler());
			this.item = item;
		}

		@Override
		protected Item callSoap() throws Exception {
			PaulineActivity.POSS.cancelTransaction(mSession.getFunId(), item.getId());
			return item;
		}
	}
}
