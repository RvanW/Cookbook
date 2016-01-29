package nl.mprog.robbert.cookbook;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener {
    // initialize variables
    private ArrayList<Recipe> recipeList;
    private View view;

    private final GalleryFragment thisFragment = this;

    public GalleryFragment() {
        // Required empty public constructor
    }

    private Activity myActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gallery, container, false);

        myActivity.setTitle("Gallery");
        NavigationView navigationView = (NavigationView) myActivity.findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        // set the navigation icon in response
        Toolbar toolbar = (Toolbar) myActivity.findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.ic_menu_gallery, null);
        if (nav_logo != null) {
            int color = Color.parseColor("#FFFFFF"); //The color u want
            nav_logo.setTint(color);
            toolbar.setLogo(nav_logo);
        }


        // get remote data async
        new RemoteDataTask().execute();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Recipe clickedRecipe = recipeList.get(position);
        DetailsFragment fragment = DetailsFragment.newInstance(clickedRecipe);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.frag_holder, fragment)
                .addToBackStack(null)
                .commit();
    }


    // This function gets the remote (online) data from parse.com and sets the adapter
    private class RemoteDataTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading);
            loadingLayout.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ParseUser current_user = ParseUser.getCurrentUser();
            // Create the array
            recipeList = new ArrayList<>();
            try {
                // Locate the class table named "Recipe" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<>(
                        "Recipe");
                // only public recipes
                query.whereEqualTo("public", true);
                query.include("author");
                // sort by number of ratings
                query.orderByDescending("numberOfRatings");
//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                List<ParseObject> parseObjectList = query.find();

                // Also get the current user's favorites from parse
                ParseQuery<ParseObject> favorites_query = new ParseQuery<>(
                        "Favorites");
                favorites_query.include("recipeId");
                favorites_query.whereEqualTo("userId", current_user);
                List<ParseObject> favoriteList = favorites_query.find();
                ArrayList<String> favoriteIdList = new ArrayList<>();
                for (ParseObject favorite : favoriteList) {
                    Recipe recipe = (Recipe) favorite.get("recipeId");
                    if (recipe != null) {
                        Log.d("Recipe ID", recipe.getObjectId() + " ");
                        favoriteIdList.add(recipe.getObjectId());
                    }
                }

                for (ParseObject recipeObject : parseObjectList) {
                    Recipe recipe = (Recipe) recipeObject;
                    Log.d("Recipe object ID",recipeObject.getObjectId());
                    recipe.setFavorite(favoriteIdList.contains(recipeObject.getObjectId()));
                    recipe.getAvg_Rating();
                    recipeList.add(recipe);
                }
                // sort by average rating
                Collections.sort(recipeList,Recipe.COMPARE_BY_RATING);
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading);
            loadingLayout.setVisibility(View.GONE);
            // Locate the listview in listview_main.xml
            ListView listview = (ListView) view.findViewById(R.id.listView);

            // Pass the results into ListViewAdapter.java
            ListAdapter adapter = new ListViewAdapter(myActivity, R.layout.list_item_style,
                    recipeList);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // set the onclick listener
            listview.setOnItemClickListener(thisFragment);

            // Close the progressdialog
//            mProgressDialog.dismiss();
        }
    }


}
