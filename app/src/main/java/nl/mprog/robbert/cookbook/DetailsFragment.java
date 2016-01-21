package nl.mprog.robbert.cookbook;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */

public class DetailsFragment extends Fragment {

    View view;
    Recipe mRecipe = null;

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
            if (ParseUser.getCurrentUser() != null && mRecipe != null) {
                if (ParseUser.getCurrentUser()== mRecipe.getAuthor()) {
                    // enable the edit recipe button if the current user owns this recipe.
                    setHasOptionsMenu(true);
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
        if (mRecipe != null) {
            TextView title = (TextView) view.findViewById(R.id.detail_title);
            title.setText(mRecipe.getTitle());
            TextView description = (TextView) view.findViewById(R.id.detail_description);
            description.setText(mRecipe.getDescription());
            ImageView image = (ImageView) view.findViewById(R.id.detail_image);
            displayImage(mRecipe.getImageFile(), image);
            if (mRecipe.getIngredients() != null) {
                String htmlString = "";
                for (String ingredient : mRecipe.getIngredients()) {
                    htmlString += "&#8226;    " + ingredient + "<br/>";
                }
                if (!Objects.equals(htmlString, "")) {
                    TextView ingredients = (TextView) view.findViewById(R.id.detail_ingredients);
                    ingredients.setText(Html.fromHtml(htmlString));
                }
            }
        }
        return view;
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
            img.setImageResource(R.drawable.image_icon);
            img.setPadding(10, 10, 10, 10);

        }

    }
}
