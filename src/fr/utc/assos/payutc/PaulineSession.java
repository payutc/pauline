package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PaulineSession implements Parcelable {

	public final static int VENTE_LIBRE			= 0;
	public final static int VENTE_PRODUIT		= 1;
	public final static int ANNULER_VENTE		= 2;
	
	private ArrayList<Item> mPanier;
	private int mHomeChoice;
	private String mIdBuyer;
	
	public PaulineSession() {
		mPanier	= new ArrayList<Item>();
		mHomeChoice = -1;
		mIdBuyer = "";
	}
	
	public void save(Intent intent) {
		Bundle b = new Bundle();
		save(b);
		intent.putExtras(b);
	}
	
	public void save(Bundle b) {
		b.putParcelable("pauline_session", this);
	}
	
	public static PaulineSession load(Intent intent) {
		PaulineSession session;
        Bundle b = intent.getExtras();
        if (b==null) {
        	session = new PaulineSession();
        }
        else {
	        session = b.getParcelable("pauline_session");
	        if (session==null) {
	        	session = new PaulineSession();
	        }
        }
        return session;
	}
	
	public ArrayList<Item> getItems() {
		return mPanier;
	}
	
	public void addItem(Item i) {
		mPanier.add(i);
	}
	
	public void removeItem(Item i) {
		mPanier.remove(i);
	}
	
	public void clearItems() {
		mPanier.clear();
	}
	
	public int getTotal() {
		int total = 0;
		for (Item i : mPanier) {
			total += i.getCost();
		}
		return total;
	}
	
	public int getNbItems() {
		return mPanier.size();
	}
	
	public void setBuyerId(String id) {
		mIdBuyer = id;
	}
	
	public String getBuyerId() {
		return mIdBuyer;
	}
	
	public int getHomeChoice() {
		return mHomeChoice;
	}
	
	public void setHomeChoice(int c) {
		mHomeChoice = c;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		Item[] items = mPanier.toArray(new Item[mPanier.size()]);
		dest.writeParcelableArray(items, flags);
		dest.writeInt(mHomeChoice);
		dest.writeString(mIdBuyer);
	}

	private PaulineSession(Parcel in) {
		Parcelable[] parcelables = in.readParcelableArray(Item.class.getClassLoader());
		mPanier = new ArrayList<Item>();
		for (Parcelable parcelable : parcelables) {
			Item item = (Item) parcelable;
			mPanier.add(item);
		}
		mHomeChoice = in.readInt();
		mIdBuyer = in.readString();
    }
	
	public static final Parcelable.Creator<PaulineSession> CREATOR = new Parcelable.Creator<PaulineSession>() {
		public PaulineSession createFromParcel(Parcel in) {
			return new PaulineSession(in);
		}
		
		public PaulineSession[] newArray(int size) {
			return new PaulineSession[size];
		}
	};
}
