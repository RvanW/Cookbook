package nl.mprog.robbert.cookbook;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        getActivity().setTitle("Account");

        // Layout depends on whether user is logged in
        LinearLayout loggedInLayout = (LinearLayout) view.findViewById(R.id.loggedIn);
        LinearLayout loggedOutLayout = (LinearLayout) view.findViewById(R.id.loggedOut);
        if (ParseUser.getCurrentUser() != null) {
            loggedInLayout.setVisibility(View.VISIBLE);
            loggedOutLayout.setVisibility(View.GONE);
            Button logOutButton = (Button) view.findViewById(R.id.logOutButton);
            logOutButton.setOnClickListener(this);
            TextView usernameTv = (TextView) view.findViewById(R.id.loggedUsername);
            usernameTv.setText("Logged in as " + ParseUser.getCurrentUser().getUsername());

        }
        else {
            loggedInLayout.setVisibility(View.GONE);
            loggedOutLayout.setVisibility(View.VISIBLE);
            Button signUpButton = (Button) view.findViewById(R.id.signUpButton);
            Button logInButton = (Button) view.findViewById(R.id.loginButton);
            signUpButton.setOnClickListener(this);
            logInButton.setOnClickListener(this);
        }

        // disable nav drawer highlight
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        for(int i = 0; i < 5; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);

        }

        // set the navigation icon
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.account, null);
        if (nav_logo != null) {
            int color = Color.parseColor("#FFFFFF");
            nav_logo.setTint(color);
            toolbar.setLogo(nav_logo);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch(v.getId()) {
            case R.id.signUpButton:
                fragment = new SignUpFragment();
                break;
            case R.id.loginButton:
                fragment = new LoginFragment();
                break;
            case R.id.logOutButton:
                logOut();
                break;
        }
        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.frag_holder, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void logOut() {
        final Fragment thisFragment = this;
        final LinearLayout loadingLayout = (LinearLayout) getActivity().findViewById(R.id.loading);
        final LinearLayout loggedInLayout = (LinearLayout) getActivity().findViewById(R.id.loggedIn);
        loadingLayout.setVisibility(View.VISIBLE);
        loggedInLayout.setVisibility(View.GONE);
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                loadingLayout.setVisibility(View.GONE);
                if (e == null) {
                    TextView profileName = (TextView) getActivity().findViewById(R.id.header_username);
                    profileName.setText("Logged out");
                    // Reload this fragment by detaching and attaching itself
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .detach(thisFragment)
                            .attach(thisFragment)
                            .commit();

                }
                else {
                    Toast.makeText(getActivity(),"Failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    loggedInLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}
