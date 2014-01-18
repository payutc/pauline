package fr.utc.assos.payutc;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DownloadImgTask extends AsyncTask<Item, Object, Object> {
	private final static String LOG_TAG		= "DownloadImg";
	
	ArrayAdapter<Item> mGridAdapter;
	ImageCache mImageCache;
	
	public DownloadImgTask(ArrayAdapter<Item> gridAdapter, ImageCache imageCache) {
		mGridAdapter = gridAdapter;
		mImageCache = imageCache;
	}

	@Override
	protected Object doInBackground(Item... items) {
		for (Item item : items) {
			Bitmap im = null;
			try {
    			im = mImageCache.getImageFromCache(item.getIdImg());
    		}
    		catch (Exception e) {
				Log.w(LOG_TAG, "getImage #"+item.getIdImg()+" error getFromCache. ", e);
			}
			if (im==null) {
				try {
					im = PaulineActivity.POSS.getImage64(item.getIdImg(), 120, 120);
				}
				catch (Exception e) {
					Log.e(LOG_TAG, "getImage #"+item.getIdImg(), e);
				}
				if (im!=null) {
					try {
						mImageCache.saveImageToCache(item.getIdImg(), im);
					}
					catch (Exception e) {
						Log.e(LOG_TAG, "getImage #"+item.getIdImg()+" error saveToCache. ", e);
					}
				}
			}
			if (im!=null) {
				item.setImage(im);
			}
		}

		
		return null;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		mGridAdapter.notifyDataSetChanged();
	}
 }