package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.usc.sunset.team7.www.parkhere.Objects.Listing;
import edu.usc.sunset.team7.www.parkhere.Objects.ResultsPair;
import edu.usc.sunset.team7.www.parkhere.Objects.SearchResult;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class CustomResultsAdapter extends BaseAdapter {
    //private Listing[] allResults;
    //private HashMap<Listing, Double> allResults;
    private ArrayList<ResultsPair> allResults;
    private static LayoutInflater inflater = null;
    private Context context;

    public CustomResultsAdapter(Activity activity, ArrayList<ResultsPair> allResults) {
        this.context = activity;
        this.allResults = allResults;
    }


    @Override
    public int getCount() {
        return allResults.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < allResults.size() && position >= 0) {
            return allResults.get(position);
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
            inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.results_view, parent, false);

            item = new ItemShell();
            item.searchLabel = (TextView) rowView.findViewById(R.id.results_label);
            item.distanceLabel = (TextView) rowView.findViewById(R.id.distance_label);
            item.imgView = (ImageView) rowView.findViewById(R.id.results_image);
        }
        else {
            item = (ItemShell) rowView.getTag();
        }

        item.searchLabel.setText(((ResultsPair)getItem(position)).getListing().getName());
        double myDouble = ((ResultsPair) getItem(position)).getDistance();
        item.distanceLabel.setText(Double.toString(myDouble));
        //image stuff later
        return rowView;
    }

    public class ItemShell {
        TextView searchLabel;
        TextView distanceLabel;
        ImageView imgView;
    }
}
