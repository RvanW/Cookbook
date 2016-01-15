package nl.mprog.robbert.cookbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 12-1-2016.
 */
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ListViewAdapter extends ArrayAdapter<Recipe> {
    Context context;
    int layoutResourceId;
    ArrayList<Recipe> data = new ArrayList<Recipe>();
    ParseUser current_user;
    ArrayList<String> favoriteRecipeIdList = new ArrayList<>();

    public ListViewAdapter(Context context, int layoutResourceId,
                                 ArrayList<Recipe> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.current_user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "Favorites");
        query.include("recipeId");
        query.include("userId");
        query.whereEqualTo("userId", current_user);
//                // sort by rating
//                query.orderByAscending("rating");
//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        try {
            List<ParseObject> favorites = query.find();
            for (ParseObject favorite : favorites) {
                ParseObject recipePointer = favorite.getParseObject("recipeId");
                String objectId = recipePointer.getObjectId();
                favoriteRecipeIdList.add(objectId);
                Log.d("favRecipeslist: ", String.valueOf(favoriteRecipeIdList));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_title);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            holder.heart = (ImageView) row.findViewById(R.id.heartIcon);
            holder.rating = (TextView) row.findViewById(R.id.item_rating);
            holder.author = (TextView) row.findViewById(R.id.item_author);
//            holder.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar2);
            row.setTag(holder);
        } else { // recycle view if any
            holder = (RecordHolder) row.getTag();
        }

        Recipe item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.rating.setText(item.getRating());
        holder.author.setText(item.getAuthor().getUsername());
        displayImage(item.getImageFile(), (ImageView) row.findViewById(R.id.item_image));
//        holder.ratingBar.setRating(item.getRating());
        if(this.favoriteRecipeIdList != null && this.favoriteRecipeIdList.contains(item.getId())) {
            holder.heart.setImageResource(R.drawable.heart);
        }


        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
        ImageView heart;
        TextView rating;
        TextView author;
    }

    private void displayImage(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {

                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                data.length);

                        if (bmp != null) {

                            Log.e("parse file ok", "");
                            // img.setImageBitmap(Bitmap.createScaledBitmap(bmp,
                            // (display.getWidth() / 5),
                            // (display.getWidth() /50), false));
                            img.setImageBitmap(bmp);
                            // img.setPadding(10, 10, 0, 0);



                        }
                    } else {
                        Log.e("img download failed! ", e.getMessage());
                    }

                }
            });
        } else {

            Log.e("parse file", " null");

            // img.setImageResource(R.drawable.ic_launcher);

            img.setPadding(10, 10, 10, 10);
        }

    }
}
