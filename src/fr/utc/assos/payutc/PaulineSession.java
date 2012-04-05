package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PaulineSession implements Parcelable {
	
	
	private ArrayList<Item> mPanier;
	
	private int mHomeChoice;
	
	public PaulineSession() {
		mPanier	= new ArrayList<Item>();
		mHomeChoice = -1;
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
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Item[] items = mPanier.toArray(new Item[mPanier.size()]);
		dest.writeParcelableArray(items, flags);
	}

	private PaulineSession(Parcel in) {
		Parcelable[] parcelables = in.readParcelableArray(Item.class.getClassLoader());
		mPanier = new ArrayList<Item>();
		for (Parcelable parcelable : parcelables) {
			Item item = (Item) parcelable;
			mPanier.add(item);
		}
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
