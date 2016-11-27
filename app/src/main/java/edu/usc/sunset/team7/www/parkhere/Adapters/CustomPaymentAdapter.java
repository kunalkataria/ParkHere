package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.joda.time.DateTime;

import java.util.ArrayList;

import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/30/16.
 */

public class CustomPaymentAdapter extends BaseAdapter {
    private ArrayList<Booking> allBookings;
    private static LayoutInflater inflater = null;
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public CustomPaymentAdapter(Activity activity, ArrayList<Booking> allBookings) {
        this.context = activity;
        this.allBookings = allBookings;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }
    @Override
    public int getCount() {
        return allBookings.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < allBookings.size() && position >= 0){
            return allBookings.get(position);
        }
        return null;
    }

    public void remove(int position){
        if(position < allBookings.size() && position >= 0){
            allBookings.remove(position);
        }
        return;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemShell item;
        View rowView = convertView;
        if(rowView == null){
            inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.confirm_payment_view, parent, false);

            item = new ItemShell();
            item.bookingLabel = (TextView) rowView.findViewById(R.id.booking_label);
            item.dateLabel = (TextView) rowView.findViewById(R.id.date_range_label);
            item.amountLabel = (TextView) rowView.findViewById(R.id.dollar_amount);
            //item.confirmButton = (AppCompatButton) rowView.findViewById(R.id.confirm_payment_button);
            rowView.setTag(item);
        } else{
            item = (ItemShell) rowView.getTag();
        }

        Booking currBooking = (Booking) getItem(position);

        long startTime = currBooking.getBookStartTime();
        long endTime = currBooking.getBookEndTime();

        item.bookingLabel.setText(currBooking.getMListing().getName());
        item.dateLabel.setText(Tools.getDateString(startTime) + "--" + Tools.getDateString(endTime));
        double userBalance = currBooking.getMListing().getPrice();
        item.amountLabel.setText("$"+String.format("%.2f",userBalance));

//        long startTime = ((Listing) getItem(position)).getStartTime();
//        long endTime = ((Listing) getItem(position)).getStopTime();
//
//        item.bookingLabel.setText(((Listing)getItem(position)).getName());
//        item.dateLabel.setText(Tools.getDateString(startTime) + "--" + Tools.getDateString(endTime));
//        double userBalance = ((Listing)getItem(position)).getPrice();
//        item.amountLabel.setText("$"+String.format("%.2f",userBalance));
        return rowView;
    }

    public class ItemShell {
        TextView bookingLabel;
        TextView dateLabel;
        TextView amountLabel;
    }
}
