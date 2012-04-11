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

public class ListItemAdapter extends ArrayAdapter<Item> {

	private static class ViewHolder {
		TextView name;
		TextView cost;
		ImageView icon;
	}

    public ListItemAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
		super(context, textViewResourceId, items);
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//Log.d(LOG_TAG, "getView #"+position+" "+convertView);
    	Item item = getItem(position);
    	ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	
        	LayoutInflater li = (LayoutInflater)this.getContext().getSystemService
        		      (Context.LAYOUT_INFLATER_SERVICE);
        	convertView = li.inflate(R.layout.panier_list_item, null);
        	
        	holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.item_image);
			holder.name = (TextView) convertView.findViewById(R.id.item_name);
			holder.cost = (TextView) convertView.findViewById(R.id.item_cost);
			
			convertView.setTag(holder);
			
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }

		holder.name.setText(item.getName());
		holder.cost.setText(""+item.getCost());
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
