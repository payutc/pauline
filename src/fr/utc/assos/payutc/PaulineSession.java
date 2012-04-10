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
		b.putParcelable("pauline_session", this);
		intent.putExtras(b);
	}
	
	public static PaulineSession get(Intent intent) {
        Bundle b = intent.getExtras();
        PaulineSession s = b.getParcelable("pauline_session");
        if (s==null) {
        	s = new PaulineSession();
        }
        return s;
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
	
	public void setBuyerId(String id) {
		mIdBuyer = id;
	}
	
	public int getHomeChoice() {
		return mHomeChoice;
	}
	
	public void setHomeChoice(int c) {
		mHomeChoice = c;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
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
		@Override
		public PaulineSession createFromParcel(Parcel in) {
			return new PaulineSession(in);
		}
		
		@Override
		public PaulineSession[] newArray(int size) {
			return new PaulineSession[size];
		}
	};
}
