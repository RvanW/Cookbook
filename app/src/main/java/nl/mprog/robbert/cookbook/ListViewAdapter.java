package nl.mprog.robbert.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Robbert van Waardhuizen (10543147) on 12-1-2016.
 * Project for University of Amsterdam
 */
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;

class ListViewAdapter extends ArrayAdapter<Recipe> {
    private final Context context;
    private final int layoutResourceId;
    private ArrayList<Recipe> data = new ArrayList<>();

    public ListViewAdapter(Context context, int layoutResourceId,
                           ArrayList<Recipe> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_title);
            holder.imageItem = (ParseImageView) row.findViewById(R.id.item_image);
            holder.heart = (ImageView) row.findViewById(R.id.heartIcon);
            holder.rating = (TextView) row.findViewById(R.id.item_rating);
            holder.author = (TextView) row.findViewById(R.id.item_author);
            holder.description = (TextView) row.findViewById(R.id.item_description);
//            holder.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar2);
            row.setTag(holder);
        } else { // recycle view if any
            holder = (RecordHolder) row.getTag();
        }

        Recipe item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.rating.setText(String.valueOf(item.getAvg_Rating()));
        holder.author.setText(item.getAuthor().getUsername());
        holder.heart.setImageResource(item.isFavorite() ? R.drawable.heart : R.drawable.heart_outline);
        if (item.getImageFile() != null) {
            holder.imageItem.setParseFile(item.getImageFile());
            final RecordHolder finalHolder = holder;
            holder.imageItem.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    finalHolder.imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    finalHolder.imageItem.setVisibility(View.VISIBLE);

                }
            });
        } else {
            holder.imageItem.setImageResource(R.drawable.image_icon);
            holder.imageItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        //displayImage(item.getImageFile(), (ImageView) row.findViewById(R.id.item_image));
//        holder.ratingBar.setRating(item.getRating());

        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        ParseImageView imageItem;
        ImageView heart;
        TextView rating;
        TextView author;
        TextView description;
    }
}
