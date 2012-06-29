package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import fr.utc.assos.payutc.soap.SoapTask;
import fr.utc.assos.payutc.soap.PBuy.TransactionResult;

public class ResultTransactionActivity extends BaseActivity {
	private static final String LOG_TAG		= "ResultTransactionActivity";
	
	public final static int TRANSACTION_OK		= 0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate ResultTransactionActivity");
        
        new TransactionTask(mSession.getBuyerId() ,mSession.getItems()).execute();
    }
    
    protected void setResultOkView(TransactionResult r) {
        setContentView(R.layout.result_ok);
        TextView tv = (TextView)findViewById(R.id.result_ok_username);
        tv.setText(r.firstName+" "+r.lastName);
        tv = (TextView)findViewById(R.id.result_ok_solde);
        tv.setText(("solde : "+r.solde/100.0)+"€");
    }
    
    protected void setResultFailView(Exception ex) {
    	setContentView(R.layout.result_echec);
        TextView tv = (TextView)findViewById(R.id.result_echec_msg);
        tv.setText(ex.getMessage());
    }
    
    public void onClickOk(View view) {
    	stop(true);
    }
    
    public void onClickOkFromEchec(View view) {
    	stop(RESULT_CANCELED);
    }
    
    protected class TransactionTask extends SoapTask {
    	private ArrayList<Integer> mIds;
    	private String mIdBuyer;
    	private TransactionResult r=null;
    	
    	public TransactionTask(String id, ArrayList<Item> items) {
    		super("Transaction", ResultTransactionActivity.this,
    				"Transaction en cours...", 0);
			mIds = new ArrayList<Integer>();
			for (int i=0; i<items.size(); ++i) {
				Item item = items.get(i);
				mIds.add(item.getId());
			}
    		mIdBuyer = id;
    	}
    	
    	
		@Override
		protected boolean callSoap() throws Exception {
			r = PaulineActivity.PBUY.transaction(mIdBuyer, mIds, "via Pauline");
			return true;
		}
		
		@Override
		protected void onPostExecute(Integer osef) {
			super.onPostExecute(osef);
			if (r!=null) {
				setResultOkView(r);
			}
			else {
				setResultFailView(lastException);
			}
		}
    }
    
}
