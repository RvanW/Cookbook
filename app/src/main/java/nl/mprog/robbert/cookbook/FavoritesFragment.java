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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener {
    public FavoritesFragment() {
        // Required empty public constructor
    }
    private View view;
    private final Fragment thisFragment = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        getActivity().setTitle("Favorites");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);

        // set the navigation icon in response
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.heart, null);
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
    private ArrayList<Recipe> recipeList;

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
            LinearLayout loadingLayout = (LinearLayout) view.findViewById(R.id.loading);
            loadingLayout.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        final ParseObject user = ParseUser.getCurrentUser();
        protected Void doInBackground(Void... params) {
            // Create the array
            recipeList = new ArrayList<>();

            // Locate the class table named "Favorites" in Parse.com
            ParseQuery<ParseObject> query = new ParseQuery<>(
                    "Favorites");
            query.include("recipeId");
            query.include("userId");
            query.whereEqualTo("userId",user);
//                // sort by rating
//                query.orderByAscending("rating");
//                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
            try {
                List<ParseObject> parseObjectList = query.find();
                for (ParseObject favorite : parseObjectList) {
                    Recipe recipePointer = (Recipe) favorite.get("recipeId");
                    if (recipePointer != null) {
                        recipePointer.setFavorite(true);
                        recipeList.add(recipePointer);
                    }
                }
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
            ListView listview = (ListView) getActivity().findViewById(R.id.listView);
            // Pass the results into ListViewAdapter.java
            ListAdapter adapter = new ListViewAdapter(getActivity(), R.layout.list_item_style,
                    recipeList);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // set the onclick listener
            listview.setOnItemClickListener((AdapterView.OnItemClickListener) thisFragment);
        }
    }


}
