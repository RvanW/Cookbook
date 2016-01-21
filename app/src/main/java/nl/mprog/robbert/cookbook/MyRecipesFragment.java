package nl.mprog.robbert.cookbook;


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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MyRecipesFragment extends Fragment implements AdapterView.OnItemClickListener {
    public MyRecipesFragment() {
        // Required empty public constructor
    }

    ArrayList<Recipe> recipeList;
    List<ParseObject> parseObjectList;
    ListView listview;
    ListAdapter adapter;
    Fragment thisFragment = this;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        getActivity().setTitle("My Recipes");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        // set the navigation icon in response
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.book_open_variant, null);
        if (nav_logo != null) {
            int color = Color.parseColor("#FFFFFF"); //The color u want
            nav_logo.setTint(color);
            toolbar.setLogo(nav_logo);
        }
        ParseUser current_user = ParseUser.getCurrentUser();
        if(current_user != null) {
            new RemoteDataTask().execute();
        }

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


    private class RemoteDataTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            // show loading spinner
            LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading);
            loadingLayout.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            // Create the array
            recipeList = new ArrayList<Recipe>();
            ParseObject user = ParseUser.getCurrentUser();
            try {
                // Locate the class table named "Recipe" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Recipe");
                query.include("author");
                query.whereEqualTo("author", user);
//                // sort by rating
                query.orderByDescending("rating");

                // Also get the current user's favorites from parse
                ParseQuery<ParseObject> favorites_query = new ParseQuery<ParseObject>(
                        "Favorites");
                favorites_query.include("recipeId");
                favorites_query.whereEqualTo("userId", user);
                List<ParseObject> favoriteList = favorites_query.find();
                ArrayList<String> favoriteIdList = new ArrayList<>();
                for (ParseObject favorite : favoriteList) {
                    Recipe recipe = (Recipe) favorite.get("recipeId");
                    favoriteIdList.add(recipe.getObjectId());
                }

//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                parseObjectList = query.find();
                for (ParseObject recipe : parseObjectList) {

                    // use the parse object to create a java object
                    ParseFile image = (ParseFile) recipe.getParseFile("image");
                    Recipe recipeItem = new Recipe();
                    recipeItem.setId(recipe.getObjectId());
                    recipeItem.setAuthor((ParseUser) recipe.get("author"));
                    recipeItem.setTitle((String) recipe.get("title"));
                    recipeItem.setDescription((String) recipe.get("description"));
                    recipeItem.setRating((String) recipe.get("rating"));
                    recipeItem.setImageFile(image);
                    recipeItem.setFavorite(favoriteIdList.contains(recipeItem.getId()));
                    List<String> ingredientList = recipe.getList("ingredients");
                    recipeItem.setIngredients(ingredientList);

                    recipeList.add(recipeItem);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            // hide the loading screen
            LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading);
            loadingLayout.setVisibility(View.GONE);
            // Locate the listview in listview_main.xml
            listview = (ListView) getActivity().findViewById(R.id.listView);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(getActivity(),R.layout.list_item_style,
                    recipeList);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // set the onclick listener
            listview.setOnItemClickListener((AdapterView.OnItemClickListener) thisFragment);
        }
    }


}
