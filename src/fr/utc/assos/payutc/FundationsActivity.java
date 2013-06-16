package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.utc.assos.payutc.api.ApiException;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.POSS.Fundation;

public class FundationsActivity extends BaseActivity {
	public final static String LOG_TAG		= "FundationsActivity";
	
	protected ArrayList<Fundation> fundations = new ArrayList<Fundation>();
	protected ArrayAdapter<String> adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate FundationsActivity");
        setContentView(R.layout.fundations);
        
        ListView lv = (ListView)findViewById(R.id.list_view);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 0 && position < fundations.size()) {
					mSession.setFunId(fundations.get(position).fun_id);
					startHomeActivity();
				}
			}
		});
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, new ArrayList<String>());
		lv.setAdapter(adapter);
		
		new GetFundationsTask().execute();
    }
    
    public void updateListView() {
        adapter.clear();
        for (Fundation f: fundations) {
        	adapter.add(f.name);
        }
    }

    
    public void startHomeActivity() {
    	Log.d(LOG_TAG,"startHomeActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.HomeActivity.class);
    	startActivity(intent);
    }

    protected void onResultGetFundations(Exception ex) {
    	String errorMessage = null;
		if (ex == null && fundations.size() == 0) {
			errorMessage = "Tu n'a les droits sur aucune fondation !";
		}
		else if (ex != null) {
			errorMessage = ex.getMessage();
		}
    	if (errorMessage != null) {
    		if (ex != null && ex instanceof ApiException) {
    			ApiException aex = (ApiException)ex;
    			if (aex.type.endsWith("CheckRightException")) {
    				errorMessage = "Toi ou ton application n'avez pas le droit de vendre. " +
    						"Contactes le président de ton association pour qu'il te donne" +
    						" des droits. Le nom de ton application est '"+
    						PaulineActivity.getAppName(this)+"'.";
    			}
    		}
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Echec")
    		.setMessage(errorMessage)
    		.setPositiveButton("Encore !", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dialog.cancel();
    				new GetFundationsTask().execute();
    			}
    		})
    		.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				dialog.cancel();
    				stop(false);
    			}});
    		builder.create().show();
    	}
    	else {
    		updateListView();
    		if (fundations.size() == 1) {
    			mSession.setFunId(fundations.get(0).fun_id);
    			startHomeActivity();
    		}
    	}
    }
    
    protected class GetFundationsTask extends ApiTask<Integer, Integer, Object> {
    	public GetFundationsTask() {
    		super("Patientez", FundationsActivity.this, 
    				"Récupération des assos...");
    	}
    	
    	@Override
    	protected boolean callSoap() throws Exception {
    		fundations = PaulineActivity.POSS.getFundations();
    		return true;
    	}
		
		@Override
		protected void onPostExecute(Object osef) {
			super.onPostExecute(osef);
			onResultGetFundations(lastException);
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	new Thread(new Runnable() {
    		public void run() {
    			for (int i=0; i<3; ++i) {
	    			try {
	    				Log.d(LOG_TAG,"onDestroy : logout");
	    				PaulineActivity.POSS.logout();
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
    		}
    		}).start();
    }
}
