package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class CustomBookingAdapter extends BaseAdapter {
    private Booking[] allBookings;
    private static LayoutInflater inflater = null;
    private Context context;

    public CustomBookingAdapter(Activity activity, Booking[] allBookings) {
        this.context = activity;
        this.allBookings = allBookings;
    }

    @Override
    public int getCount() {
        return allBookings.length;
    }

    @Override
    public Object getItem(int position) {
        if(position < allBookings.length && position >= 0){
            return allBookings[position];
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
        if(rowView == null){
            inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.booking_view, parent, false);

            item = new ItemShell();
            item.bookingLabel = (TextView) rowView.findViewById(R.id.booking_label);
            item.dateLabel = (TextView) rowView.findViewById(R.id.date_range_label);
            item.imgView = (ImageView) rowView.findViewById(R.id.booking_image);

            rowView.setTag(item);
        } else {
            item = (ItemShell) rowView.getTag();
        }

        item.bookingLabel.setText(((Booking)getItem(position)).getMListing().getName());
        item.dateLabel.setText(((Booking)getItem(position)).getBookStartTime() + "--" + ((Booking)getItem(position)).getBookEndTime());
        //need for item
        return rowView;
    }

    public class ItemShell{
        TextView bookingLabel;
        TextView dateLabel;
        ImageView imgView;
    }
}
