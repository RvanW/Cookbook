package nl.mprog.robbert.cookbook;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.File;


public class AddRecipeFragment extends Fragment implements View.OnClickListener {
    private Recipe recipe;

    public AddRecipeFragment() {
        recipe = new Recipe();
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        // set click listener for save button
        Button saveButton = (Button) view.findViewById(R.id.save);
        saveButton.setOnClickListener(this);
        getActivity().setTitle("New recipe");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        EditText title = (EditText) view.findViewById(R.id.newTitel);
        recipe.setTitle(title.getText().toString());

        EditText description = (EditText) view.findViewById(R.id.newDescription);
        recipe.setDescription(description.getText().toString());

        recipe.setRating("0");

        CheckBox savePublic = (CheckBox) view.findViewById(R.id.savePublic);
        recipe.setPublic(savePublic.isChecked());

        // Save the recipe and return
        recipe.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Fragment fragment = new MyRecipesFragment();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.frag_holder, fragment)
                            .commit();

                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}
