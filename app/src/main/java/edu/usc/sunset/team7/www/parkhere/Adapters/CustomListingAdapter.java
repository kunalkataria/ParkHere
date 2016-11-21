package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class CustomListingAdapter extends BaseAdapter {

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemShell item;
        View rowView = convertView;
        if(rowView == null) {
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
        item.dateLabel.setText(Tools.convertUnixTimeToDateString(((Listing)getItem(position)).getStartTime()) + " - " + Tools.convertUnixTimeToDateString(((Listing)getItem(position)).getStopTime()));
        Picasso.with(context).load(((Listing)getItem(position)).getParkingSpot().getImageURL()).into(item.imgView);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent detailsIntent = new Intent(context, ListingDetailsActivity.class);
                detailsIntent.putExtra(Consts.LISTING_EXTRA, (Listing) getItem(position));
                context.startActivity(detailsIntent);
            }
        });

        return rowView;
    }

    public class ItemShell {
        TextView locationLabel;
        TextView dateLabel;
        ImageView imgView;
    }
}
