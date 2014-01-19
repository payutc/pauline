package fr.utc.assos.payutc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
                
        Button butProduit = (Button)findViewById(R.id.ButtonModeVP);
        butProduit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startShowArticleActivity(PaulineSession.VENTE_LIBRE);
			}
		});
		
        Button butLibre = (Button)findViewById(R.id.ButtonModeVL);
        butLibre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startShowArticleActivity(PaulineSession.VENTE_PRODUIT);
			}
		});
    }
    
    private void startShowArticleActivity(int type) {
    	Log.d(LOG_TAG, "startShowArticleActivity");
    	Intent intent = new Intent(this, fr.utc.assos.payutc.ShowArticleActivity.class);
    	mSession.setHomeChoice(type);
    	startActivityForResult(intent, type);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(LOG_TAG, "requestCode:"+requestCode+" ,resultCode:"+resultCode + " " +RESULT_OK);
        
		switch (requestCode) {
			case PaulineSession.SETUP_APP:
		    	if (resultCode == RESULT_OK) {
		            final String[] items = new String[] {"Vente libre", "Vente produit", "Déconnexion"};
		            ListView lv = (ListView)findViewById(R.id.list_view);
		            lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));
		    	}
		    default:
		    	break;
	    }
    }
}
