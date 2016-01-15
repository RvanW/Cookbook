package nl.mprog.robbert.cookbook;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class MyRecipesFragment extends Fragment {
    public MyRecipesFragment() {
        // Required empty public constructor
    }
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
    ArrayList<Recipe> recipeList;
    List<ParseObject> parseObjectList;
    ListView listview;
    ListAdapter adapter;
    private class RemoteDataTask extends AsyncTask<Void,Void,Void> {
        ParseObject user = ParseUser.getCurrentUser();
        protected Void doInBackground(Void... params) {
            // Create the array
            recipeList = new ArrayList<Recipe>();
            try {
                // Locate the class table named "Recipe" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "Recipe");
                query.include("author");
                query.whereEqualTo("author", user);
//                // sort by rating
                query.orderByDescending("rating");
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
            // Locate the listview in listview_main.xml
            listview = (ListView) getActivity().findViewById(R.id.listView);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(getActivity(),R.layout.list_item_style,
                    recipeList);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
//            mProgressDialog.dismiss();
        }
    }


}
