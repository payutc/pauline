package fr.utc.assos.payutc;

import java.util.ArrayList;

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
import fr.utc.assos.payutc.api.ResponseHandler;
import fr.utc.assos.payutc.api.responsehandler.DisplayDialogOnError;

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
		
		new GetFundationsTask(new GetFundationRespHandler()).execute();
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
    
    /*
     * Allow retry
     * Exit activity
     * 
     * Without the data, the application can't go further, so exit activity !
     */
    protected class GetFundationRespHandler extends DisplayDialogOnError<ArrayList<Fundation>> {

		public GetFundationRespHandler() {
			super(FundationsActivity.this, "Erreur", null, true);
			againListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							new GetFundationsTask(GetFundationRespHandler.this).execute();
						}
					};
		}

		@Override
		public void onSuccess(ArrayList<Fundation> response) {
			fundations = response;
			updateListView();
    		if (fundations.size() == 1) {
    			mSession.setFunId(fundations.get(0).fun_id);
    			startHomeActivity();
    		}
		}
    	
    }
    
    protected class GetFundationsTask extends ApiTask<ArrayList<Fundation>> {
    	
    	public GetFundationsTask(ResponseHandler<ArrayList<Fundation>> handler) {
    		super(FundationsActivity.this, "Patientez", 
    				"Récupération des assos...", handler);
    	}
    	
    	@Override
    	protected ArrayList<Fundation> callSoap() throws Exception {
    		boolean noFundations = false;
    		ArrayList<Fundation> fundations = null;
    		try {
    			 fundations = PaulineActivity.POSS.getFundations();
    		}
    		catch (ApiException ex) {
    			if (ex.type.endsWith("CheckRightException")) {
    				noFundations = true;
    			}
    			else {
    				throw ex;
    			}
    		}
    		if (noFundations || fundations == null || fundations.size() == 0) {
				String errorMessage = "Toi ou ton application n'avez pas le droit de vendre. " +
						"Contactes le président de ton association pour qu'il te donne" +
						" des droits. Le nom de ton application est '"+
						PaulineActivity.getAppName(FundationsActivity.this)+"'.";
				throw new Exception(errorMessage);
			}
    		return fundations;
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
