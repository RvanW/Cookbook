package nl.mprog.robbert.cookbook;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {


    public AccountFragment() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch(v.getId()) {
            case R.id.signUpButton: fragment = new SignUpFragment();
                break;
            case R.id.loginButton: fragment = new LoginFragment();
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
        ParseUser.logOut();
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_holder);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .detach(this)
                .attach(this)
                .commit();
    }

}
