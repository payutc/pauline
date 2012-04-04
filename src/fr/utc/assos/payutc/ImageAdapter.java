package fr.utc.assos.payutc;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private static final String LOG_TAG		= "ImageAdapter";
	
	private Context mContext;
    private ArrayList<Item> mItems;

    public ImageAdapter(Context c, ArrayList<Item> items) {
        mContext = c;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//Log.d(LOG_TAG, "getView #"+position+" "+convertView);
    	Item item = getItem(position);
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        Bitmap img = item.getImg();
        if (img == null) {
        	imageView.setImageResource(R.drawable.ic_launcher);
        }
        else {
        	imageView.setImageBitmap(img);
        }
        return imageView;
    }
}
