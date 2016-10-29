package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Justin on 10/28/2016.
 */

public class CustomReviewAdapter extends BaseAdapter {

    private List <Review> allReviews;
    private Context context;

    public CustomReviewAdapter(Context context, List<Review> allReviews){
        this.context = context;
        this.allReviews = allReviews;
    }

    @Override
    public int getCount() { return allReviews.size(); }

    @Override
    public Object getItem(int position) {
        if(position < allReviews.size() && position >= 0) {
            return allReviews.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO
        return null;
    }
}
