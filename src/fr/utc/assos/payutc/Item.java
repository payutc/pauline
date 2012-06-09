package fr.utc.assos.payutc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class Item implements Parcelable {
	
	private int mId;
	private String mName;
	private String mType;
	private int mIdImg;
	private int mCost;
	private String mEncodedImg;
	private Bitmap mImage;
	
	public Item(int id, String name, String type, int idImg, int cost) {
		mImage = null;
		mEncodedImg = "";
		mId = id;
		mName = name;
		mType = type;
		mIdImg = idImg;
		mCost = cost;
	}
	
	public int getIdImg() {
		return mIdImg;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
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
	
	public String getType() {
		return mType;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mName);
		dest.writeString(mType);
		dest.writeInt(mIdImg);
		dest.writeInt(mCost);
		dest.writeString(mEncodedImg);
	}
	
	private Item(Parcel in) {
		mId	= in.readInt();
		mName = in.readString();
		mType = in.readString();
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
}
