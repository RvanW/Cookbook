package nl.mprog.robbert.cookbook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Objects;


/**
 * A simple fragment taking in a recipe object and displaying it
 */

public class DetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private View view;
    private Recipe mRecipe = null;
    private ParseUser mUser = null;
    private ParseObject current_user_rating = null;

    public DetailsFragment() {
        // required empty constructor
    }

    public static DetailsFragment newInstance(Recipe recipe) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe",recipe);
        detailsFragment.setArguments(args);
        return detailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = (Recipe) getArguments().getSerializable("recipe");
            mUser = ParseUser.getCurrentUser();
            // query this recipe online
            ParseQuery<ParseObject> query = new ParseQuery<>(
                    "Recipe");
            query.whereEqualTo("objectId",mRecipe.getObjectId());
            try {
                mRecipe = (Recipe) query.getFirst();
                Log.e("Found recipe",mRecipe.getTitle());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mUser != null && mRecipe != null) {
                if (mUser == mRecipe.getAuthor()) {
                    // enable the edit recipe button if the current user owns this recipe.
                    setHasOptionsMenu(true);
                }
                // if user is logged in, see if they have rated this recipe.
                ParseQuery<ParseObject> rating_query = new ParseQuery<>(
                        "Rating");
                rating_query.whereEqualTo("userId",ParseUser.getCurrentUser());
                rating_query.whereEqualTo("recipeId", mRecipe);
                try {
                    current_user_rating = rating_query.getFirst();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_itemdetail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.edit_item:
                Fragment fragment = AddRecipeFragment.newInstance(mRecipe);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frag_holder, fragment, "edit_recipe")
                        .addToBackStack("edit_recipe")
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_details, container, false);
        getActivity().setTitle("Details");
        if (mRecipe != null) {
            TextView title = (TextView) view.findViewById(R.id.detail_title);
            title.setText(mRecipe.getTitle());
            TextView description = (TextView) view.findViewById(R.id.detail_description);
            description.setText(mRecipe.getDescription());

            // check if this recipe is a user's favorite
            ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.toggle_favorite);
            toggleButton.setChecked(mRecipe.isFavorite());
            toggleButton.setOnCheckedChangeListener(this);

            // check if this recipe has been rated by user;

            Button rateButton = (Button) view.findViewById(R.id.rateButton);
            rateButton.setOnClickListener(this);

            // check if there's an image to load
            if (mRecipe.getImageFile() != null) {
                final ParseImageView image = (ParseImageView) view.findViewById(R.id.detail_image);
                image.setParseFile(mRecipe.getImageFile());
                image.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        image.setVisibility(View.VISIBLE);
                    }
                });
            }
            // check if there are ingredients to be listed
            if (mRecipe.getIngredients() != null) {
                String htmlString = "";
                boolean first = true;
                for (String ingredient : mRecipe.getIngredients()) {
                    if (!first) {
                        htmlString += "<br/>";
                    }
                    htmlString += "&#8226;" + ingredient;
                    first = false;
                }
                if (!Objects.equals(htmlString, "")) {
                    TextView ingredients = (TextView) view.findViewById(R.id.detail_ingredients);
                    ingredients.setText(Html.fromHtml(htmlString));
                }
            }

            if (mRecipe.get("numberOfRatings") != null) {
                TextView ratingText = (TextView) view.findViewById(R.id.detail_rating);
                ratingText.setText(String.valueOf(mRecipe.getAvg_Rating()));
                TextView amountOfRatingsText = (TextView) view.findViewById(R.id.detail_ratings_amount);
                amountOfRatingsText.setText("by " + String.valueOf(mRecipe.get("numberOfRatings")) + " users");
            }

            if (current_user_rating != null) {
                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
                ratingBar.setRating(current_user_rating.getNumber("rating").floatValue());
            }

        }
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mUser != null) {
            if (isChecked) {
                ParseObject favorite = new ParseObject("Favorites");
                favorite.put("userId", ParseUser.getCurrentUser());
                favorite.put("recipeId", mRecipe);
                favorite.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d("Error: ", e.getMessage());
                        } else {
                            mRecipe.setFavorite(true);
                        }
                    }
                });

            } else {
                // Delete the current user's favorites from parse
                ParseQuery<ParseObject> favorites_query = new ParseQuery<>(
                        "Favorites");
                favorites_query.include("userId");
                favorites_query.include("recipeId");
                favorites_query.whereEqualTo("userId", ParseUser.getCurrentUser());
                favorites_query.whereEqualTo("recipeId", mRecipe);
                try {
                    ParseObject favorite = favorites_query.getFirst();
                    favorite.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                            } else {
                                mRecipe.setFavorite(false);
                            }

                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please login or sign up to add favorites", Toast.LENGTH_SHORT).show();
            buttonView.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (mUser == null) {
            Toast.makeText(getActivity(),"Create an account or log in to give ratings",Toast.LENGTH_SHORT).show();
            return;
        }
        RatingBar ratingBar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        final Number rating = ratingBar.getRating();
        Number last_rating = 0;
        if (current_user_rating != null) { // if the user rated this item before..
            last_rating = current_user_rating.getNumber("rating");
        }
        else {
            mRecipe.increment("numberOfRatings"); // otherwise it is a new rating
            current_user_rating = new ParseObject("Rating");
        }

        current_user_rating.put("rating", rating);
        current_user_rating.put("recipeId", mRecipe);
        current_user_rating.put("userId", mUser);
        current_user_rating.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("error saving rating", e.getMessage());
                }
            }
        });
        Log.e("Rating",rating + "," + last_rating);
        mRecipe.increment("totalRating", rating.floatValue() - last_rating.floatValue());
        mRecipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("error saving recipe", e.getMessage());
                }
                else {
                    Toast.makeText(getActivity(),"Thanks, your rating has been updated",Toast.LENGTH_SHORT).show();
                }
                TextView ratingTv = (TextView) view.findViewById(R.id.detail_rating);
                ratingTv.setText(String.valueOf(mRecipe.getAvg_Rating()));
                TextView amountOfRatingsText = (TextView) view.findViewById(R.id.detail_ratings_amount);
                amountOfRatingsText.setText("by " + String.valueOf(mRecipe.get("numberOfRatings")) + " users");
            }
        });
    }
}
