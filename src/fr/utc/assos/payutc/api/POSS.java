package fr.utc.assos.payutc.api;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.utc.assos.payutc.Item;

public class POSS extends JsonApiClient {
	public static final String LOG_TAG = "POSS";

	public POSS(String url) {
		super(url);
	}

	public String getCasUrl() throws IOException, JSONException, ApiException {
		String url = call("getCasUrl", String.class);
		Log.d(LOG_TAG, "getCasUrl : "+url);
		return url;
	}

	public void cancelTransaction(int fun_id, int transactionId) throws IOException, JSONException, ApiException {
		Log.i(LOG_TAG, "cancelTransaction("+transactionId+")");
		Arg[] args =  {
			new Arg("fun_id", fun_id),
			new Arg("pur_id", transactionId)
		};
		boolean r = call("cancel", args, Boolean.class);
		Log.d(LOG_TAG, "cancelTransaction : "+r);
	}

	public String loginCas(String ticket, String service) throws IOException, JSONException, ApiException {
		Arg[] args =  {
				new Arg("ticket", ticket),
				new Arg("service", service)
		};
		String r = call("loginCas", args, String.class);
		Log.d(LOG_TAG, "loginCas : "+r);
		return r;
	}

	public boolean loginApp(String key) throws IOException, JSONException, ApiException {
		Arg[] args =  {
				new Arg("key", key),
		};
		boolean r = call("loginApp", args, Boolean.class);
		Log.d(LOG_TAG, "loginApp : "+r);
		return r;
	}

	public boolean logout() throws IOException, JSONException, ApiException {
		boolean r = call("logout", Boolean.class);
		Log.d(LOG_TAG, "loginApp : "+r);
		return r;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class _Item {
		public int id;
		public String name;
		public int image;
		public int price;
		_Item() {};
	}
	public ArrayList<Item> getArticles(int fun_id) throws IOException, JSONException, ApiException {
		Arg[] args = {
				new Arg("fun_id", fun_id)
		};
		_Item[] _items = call("getArticles", args, _Item[].class);
		Log.d(LOG_TAG, "getArticles : " + _items);
		ArrayList<Item> items = new ArrayList<Item>();
		for (_Item i : _items) {
			items.add(new Item(i.id, i.name, i.image, i.price));
		}
		return items;
	}

	public Bitmap getImage64(int id, int width, int height) throws IOException, JSONException, ApiException {
		Arg[] args =  {
				new Arg("img_id", ""+id),
				new Arg("outw", ""+width),
				new Arg("outh", ""+height),
		};
		String encodedImage = call("getImage64", args, String.class);
		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return decodedByte;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TransactionResult {
		public String firstname;
		public String lastname;
		public int solde;
		public TransactionResult() {}
	}

	public TransactionResult transaction(int fun_id, String badge_id, ArrayList<Integer> ids) 
			throws IOException, JSONException, ApiException {
		String s_ids = "";
		for (int id : ids) {
			s_ids += " "+id;
		}
		Arg[] args =  {
				new Arg("badge_id", badge_id),
				new Arg("obj_ids", s_ids),
				new Arg("fun_id", fun_id)
		};
		TransactionResult t = call("transaction", args, TransactionResult.class);
		Log.d(LOG_TAG, "transaction : " + t);
		return t;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Purchase {
		public int pur_price;
		public int pur_id;
		public int obj_id;

		public Purchase() {}  // jackson need a dummy constructor
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CustomerDetails {
		public String firstname, lastname;
		public int solde;
		public ArrayList<Purchase> last_purchases;

		public CustomerDetails() {} // jackson need a dummy constructor
	}

	public CustomerDetails getCustomerDetails(String id) throws IOException, JSONException, ApiException {
		Arg[] args =  {
				new Arg("badge_id", id),
		};
		CustomerDetails d = call("getBuyerInfo", args, CustomerDetails.class);
		Log.d(LOG_TAG, "customer details : " + d);
		return d;
	}

	public static class Fundation {
		public int fun_id;
		public String name;
		Fundation() {};
	}

	public ArrayList<Fundation> getFundations() throws IOException, JSONException, ApiException {
		ArrayList<Fundation> fundations = call("getFundations", 
				new TypeReference<ArrayList<Fundation>>(){});
		return fundations;
	}

}
