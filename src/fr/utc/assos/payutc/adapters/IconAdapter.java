package fr.utc.assos.payutc.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.utc.assos.payutc.Item;
import fr.utc.assos.payutc.R;

public class IconAdapter extends ArrayAdapter<Item> {
	
	private static class ViewHolder {
		TextView text;
		ImageView icon;
	}
	
    public IconAdapter(Context c, int resourceId, ArrayList<Item> items) {
    	super(c, resourceId, items);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//Log.d(LOG_TAG, "getView #"+position+" "+convertView);
    	Item item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	
        	LayoutInflater li = (LayoutInflater)this.getContext().getSystemService
        		      (Context.LAYOUT_INFLATER_SERVICE);
        	convertView = li.inflate(R.layout.icon, null);
        	
        	holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.icon_text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
			
			convertView.setTag(holder);
			
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }
        
		holder.text.setText(item.getName());
        Bitmap img = item.getImg();
        if (img == null) {
        	holder.icon.setImageResource(R.drawable.ic_launcher);
        }
        else {
        	holder.icon.setImageBitmap(img);
        }
        
        return convertView;
    }
}
