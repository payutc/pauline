package fr.utc.assos.payutc;

import java.util.Formatter;
import java.util.Hashtable;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class Item implements Parcelable {
	
	private int mId;
	private String mName;
	private int mIdImg;
	private int mCost;
	private String mEncodedImg;
	private Bitmap mImage;
	private int mQuantity;
	
	public Item() {}
	
	public Item(int id, String name, int idImg, int cost) {
		mImage = null;
		mEncodedImg = "";
		mId = id;
		mName = name;
		mIdImg = idImg;
		mCost = cost;
		mQuantity = 0;
	}
	
	@SuppressWarnings("rawtypes")
	public Item(Hashtable ht) {
		mImage = null;
		mEncodedImg = "";
		mId = Integer.parseInt((String)ht.get("id"));
		mName = (String)ht.get("name"); 
		if (ht.get("idImg") != null) 
			mIdImg = Integer.parseInt((String)ht.get("idImg"));
		else
			mIdImg = 0;
		mCost = Integer.parseInt((String)ht.get("price"));
		mQuantity = 0;
	}
	
	public String getStringPrice() {
		return costToString(mCost/100.0);
	}
	
	public static String costToString(double p) {
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.FRENCH);
		formatter.format("%,.2f", p);
		formatter.close();
		return sb.toString()+" €";
	}
	
	public int getIdImg() {
		return mIdImg;
	}
	
	public void setIdImg(int id) {
		mIdImg = id;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	synchronized public Bitmap getImg() {
		return mImage;
	}
	
	synchronized public void setmImage(String encodedImg) {
		mEncodedImg = encodedImg;
		byte[] decodedString = Base64.decode(encodedImg, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		mImage = decodedByte;
	}
	
	public int getCost() {
		return mCost;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mName);
		dest.writeInt(mIdImg);
		dest.writeInt(mCost);
		dest.writeString(mEncodedImg);
	}
	
	private Item(Parcel in) {
		mId	= in.readInt();
		mName = in.readString();
		mIdImg = in.readInt();
		mCost = in.readInt();
		mEncodedImg = in.readString();
		if (!mEncodedImg.isEmpty()) {
			setmImage(mEncodedImg);
		}
    }
	
	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}
		
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};
	
	public void setImage(Bitmap img) {
		mImage = img;
	}

	public int getQuantity() {
		return mQuantity;
	}

	public void setQuantity(int mQuantity) {
		this.mQuantity = mQuantity;
	}
	
	public void incQuantity() {
		mQuantity++;
	}
	
	public void decQuantity() {
		mQuantity--;
	}
}
