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
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener {
    // initialize variables
    ArrayList<Recipe> recipeList;
    List<ParseObject> parseObjectList;
    ListView listview;
    ListAdapter adapter;
    View view;

    GalleryFragment thisFragment = this;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gallery, container, false);

        getActivity().setTitle("Gallery");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        // set the navigation icon in response
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
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
        protected Void doInBackground(Void... params) {
            if (recipeList == null) {
                // Create the array
                recipeList = new ArrayList<Recipe>();
                try {
                    // Locate the class table named "Country" in Parse.com
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                            "Recipe");
                    // only public recipes
                    query.whereEqualTo("public", true);
                    query.include("author");
                    // sort by rating
                    query.orderByDescending("rating");
//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                    parseObjectList = query.find();
                    ParseObject.pinAllInBackground(parseObjectList);
                    for (ParseObject recipe : parseObjectList) {
                        // use the parse object to create a java object
                        ParseFile image = (ParseFile) recipe.getParseFile("image");
                        Recipe recipeItem = new Recipe();
                        recipeItem.setId(recipe.getObjectId());
                        recipeItem.setAuthor(recipe.getParseUser("author"));
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
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) view.findViewById(R.id.listView);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(getActivity(),R.layout.list_item_style,
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
