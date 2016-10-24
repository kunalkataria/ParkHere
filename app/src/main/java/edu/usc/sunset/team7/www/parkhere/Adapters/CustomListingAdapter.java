package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.usc.sunset.team7.www.parkhere.Objects.Listing;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class CustomListingAdapter extends BaseAdapter{

    private Listing[] allListings;
    private static LayoutInflater inflater = null;
    private Context context;

    public CustomListingAdapter(Activity activity, Listing[] allListings) {
        this.context = activity;
        this.allListings = allListings;
    }

    @Override
    public int getCount() {
        return allListings.length;
    }

    @Override
    public Object getItem(int position) {
        if(position < allListings.length && position >= 0) {
            return allListings[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemShell item;
        View rowView = convertView;
        if(rowView != null) {
            inflater=((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.listing_view, parent, false);

            item = new ItemShell();
            item.locationLabel = (TextView) rowView.findViewById(R.id.location_label);
            item.dateLabel = (TextView) rowView.findViewById(R.id.date_range_label);
            item.imgView =  (ImageView) rowView.findViewById(R.id.listing_image);

            rowView.setTag(item);
        } else {
            item = (ItemShell) rowView.getTag();
        }

        item.locationLabel.setText(((Listing)getItem(position)).getName());
        item.dateLabel.setText(((Listing)getItem(position)).getStartTime() + "--" + ((Listing)getItem(position)).getStopTime());
        /* for images later
        String url = userRepos[position].avatarUrl;
        Picasso.with(context).load(url).into(item.imgView);
        * */
        return null;
    }

    public class ItemShell {
        TextView locationLabel;
        TextView dateLabel;
        ImageView imgView;
    }
}
