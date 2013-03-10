package fr.utc.assos.payutc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache {
	
	File mCacheDir;
	
	public ImageCache(File cacheDir) {
		mCacheDir = cacheDir;
	}
	
	public Bitmap getImageFromCache(int id) throws FileNotFoundException {
    	String cacheDirPath = mCacheDir.getAbsolutePath();
		return BitmapFactory.decodeFile(cacheDirPath+"/"+id+".png");
    }
    
    public void saveImageToCache(int id, Bitmap im) throws FileNotFoundException {
    	String cacheDirPath = mCacheDir.getAbsolutePath();
    	FileOutputStream fOut = new FileOutputStream(cacheDirPath+"/"+id+".png");
    	im.compress(Bitmap.CompressFormat.PNG, 85, fOut);
    }
}
