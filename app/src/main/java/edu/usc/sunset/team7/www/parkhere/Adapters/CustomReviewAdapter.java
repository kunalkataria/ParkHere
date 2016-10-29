package edu.usc.sunset.team7.www.parkhere.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Justin on 10/28/2016.
 */

public class CustomReviewAdapter extends BaseAdapter {

    private List <Review> allReviews;
    private static LayoutInflater inflater = null;
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
        ItemShell item;
        View rowView = convertView;
        if(rowView != null){
            inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.review_view, parent, false);

            item = new ItemShell();
            item.ratingBar = (RatingBar) rowView.findViewById(R.id.review_rating_bar);
            item.reviewLabel = (TextView) rowView.findViewById(R.id.review_text);

            rowView.setTag(item);
        } else{
            item = (ItemShell) rowView.getTag();
        }

        item.ratingBar.setRating((float)((Review)getItem(position)).getReviewRating());
        Drawable drawable = item.ratingBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFCC00"), PorterDuff.Mode.SRC_ATOP);

        item.reviewLabel.setText(((Review)getItem(position)).getReview());

        return rowView;
    }

    public class ItemShell {
        RatingBar ratingBar;
        TextView reviewLabel;
    }
}
