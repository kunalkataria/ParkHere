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

import java.util.List;

import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class CustomResultsAdapter extends BaseAdapter {
    private List<ResultsPair> allResults;
    private Context context;

    public CustomResultsAdapter(Activity activity, List<ResultsPair> allResults) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemShell item;
        View rowView = convertView;
        if(rowView == null) {
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.results_view, parent, false);

            item = new ItemShell();
            item.searchLabel = (TextView) rowView.findViewById(R.id.results_label);
            item.distanceLabel = (TextView) rowView.findViewById(R.id.distance_label);
            item.imgView = (ImageView) rowView.findViewById(R.id.results_image);

            rowView.setTag(item);
        }
        else {
            item = (ItemShell) rowView.getTag();
        }

        ResultsPair currentResult = (ResultsPair) getItem(position);

        String currentName = currentResult.getListing().getName();

        item.searchLabel.setText(currentName);

        double myDouble = currentResult.getDistance();

        item.distanceLabel.setText(Double.toString(myDouble));
        Picasso.with(context).load(currentResult.getListing().getImageURL()).into(item.imgView);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent detailsIntent = new Intent(context, ListingDetailsActivity.class);
                detailsIntent.putExtra(Consts.LISTING_RESULT_EXTRA, (ResultsPair) getItem(position));
                detailsIntent.putExtra(Consts.MY_OWN_LISTING_EXTRA, false);
                context.startActivity(detailsIntent);
            }
        });
        return rowView;
    }

    public class ItemShell {
        TextView searchLabel;
        TextView distanceLabel;
        ImageView imgView;
    }
}
