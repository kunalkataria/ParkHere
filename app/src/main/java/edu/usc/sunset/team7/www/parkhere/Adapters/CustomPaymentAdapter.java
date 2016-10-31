package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;

/**
 * Created by johnsonhui on 10/30/16.
 */

public class CustomPaymentAdapter extends BaseAdapter {
    private Booking[] allBookings;
    private static LayoutInflater inflater = null;
    private Context context;

    public CustomPaymentAdapter(Activity activity, Booking[] allBookings) {
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
        return null;
    }
}
