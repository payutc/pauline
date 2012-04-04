package fr.utc.assos.payutc;

import android.graphics.Bitmap;

public class Item {
	
	private static final String LOG_TAG		= "Item";
	
	private int mId;
	private String mName;
	private String mType;
	private int mIdImg;
	private int mCost;
	private Bitmap mImage;
	
	public Item(int id, String name, String type, int idImg, int cost) {
		mImage = null;
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
	
	synchronized public void setmImage(Bitmap bitmap) {
		mImage = bitmap;
	}
	
	public int getCost() {
		return mCost;
	}
	
	public String getType() {
		return mType;
	}
	
}
