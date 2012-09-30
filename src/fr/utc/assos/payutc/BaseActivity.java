package fr.utc.assos.payutc;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import fr.utc.assos.payutc.api.ApiTask;
import fr.utc.assos.payutc.api.POSS.CustomerDetails;

public class BaseActivity extends NfcActivity {
	private static final String LOG_TAG		= "BaseActivity";
	
	protected PaulineSession mSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mSession = PaulineSession.load(getIntent());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle b) {
		mSession.save(b);
	}
	
	@Override 
	final public void startActivityForResult(Intent intent, int requestCode) {
		mSession.save(intent);
		super.startActivityForResult(intent, requestCode);
	}
	
	@Override 
	final public void startActivity(Intent intent) {
		mSession.save(intent);
		super.startActivity(intent);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode!=RESULT_CANCELED) {
			mSession = PaulineSession.load(data);
		}
	}
	
	/**
	 * Méthode à Override si l'on veut utiliser l'event onIdentification
	 * @param {String} id
	 */
	@Override
	protected void onIdentification(String id) {
		Log.d(LOG_TAG, "Identified : "+id);
		new GetCustomerDetails().execute(id);
	}
	
	/**
	 * Finir l'activité courante, si l'activité ne s'est pas finie avec succès,
	 * on ne sauvegarde pas l'état courant de la session.
	 * @param {Boolean} success
	 */
	final protected void stop(Boolean success) {
		if (success) {
			stop(RESULT_OK);
		}
		else {
			stop(RESULT_CANCELED);
		}
	}
	
	/**
	 * Finir l'activité courante
	 * @param returnCode si == RESULT_CANCEL, la session n'est pas sauvegardée
	 */
	final protected void stop(int returnCode) {
		Intent intent = new Intent();
		if (returnCode != RESULT_CANCELED) {
			mSession.save(intent);
		}
        setResult(returnCode, intent);
		finish();
	}
	
	final protected void stop() {
		stop(RESULT_OK);
	}
	
	protected void onGetCustomerDetailsFails(Exception e) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	protected void onGetCustomerDetailsSuccess(CustomerDetails details) {
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
		builder.setMessage("Solde : "+Item.costToString(details.mSolde/100.0))
		       .setCancelable(true)
		       .setTitle(details.mFirstName+" "+details.mLastName)
		       .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	protected class GetCustomerDetails extends ApiTask<String,Integer,CustomerDetails> {

		public GetCustomerDetails() {
			super("Récupération des infos", BaseActivity.this, "Un instant s'il vous plait");
		}

		@Override
		protected CustomerDetails doInBackground(String... ids) {
			String id = ids[0];
			CustomerDetails details = null;
			try {
				details = PaulineActivity.POSS.getCustomerDetails(id);
			} catch (Exception e) {
				Log.e(mTag, "doInBackground", e);
				lastException = e;
			}
			return details;
		}
		
		@Override
		protected void onPostExecute(CustomerDetails details) {
			super.onPostExecute(details);
			if (details==null) {
				onGetCustomerDetailsFails(lastException);
			}
			else {
				onGetCustomerDetailsSuccess(details);
			}
		}
	}
}
