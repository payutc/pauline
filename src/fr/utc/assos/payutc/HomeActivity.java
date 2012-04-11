package fr.utc.assos.payutc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;



/**
 * Affiche les 3 actiones possiblent de cette application
 * - vente libre : caisse normale
 * - vente produit : après avoir choisit une selection de produits, on peut enchainer les payements
 * sans reselectionner les articles à chaque fois.
 * - annuler transaction : pour les boulets
 * 
 * @author thomas
 *
 */
public class HomeActivity extends BaseActivity {
	public final static String LOG_TAG		= "HomeActivity";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate HomeActivity");
        setContentView(R.layout.home);
        
        ListView lv = (ListView)findViewById(R.id.list_view);

        final String[] items = new String[] {"Vente libre", "Vente produit", "Annuler une vente"};
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));


		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
			    	startShowArticleActivity(PaulineSession.VENTE_LIBRE);
			    	break;
			    	
				case 1:
			    	startShowArticleActivity(PaulineSession.VENTE_PRODUIT);
			    	break;
			    	
				case 2:
					startCancelTransactionActivity();
					break;
				
				default: break;
				}
			}
		});
    }
    
    private void startShowArticleActivity(int type) {
    	Log.d(LOG_TAG,"startShowArticleActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ShowArticleActivity.class);
    	mSession.setHomeChoice(type);
    	startActivity(intent);
    }
    
    private void startCancelTransactionActivity() {
    	Log.d(LOG_TAG,"startCancelTransactionActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.CancelTransactionActivity.class);
    	startActivity(intent);
    }
    
    public void onClickCancel(View view) {
    	stop();
    }
}
