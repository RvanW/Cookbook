package nl.mprog.robbert.cookbook;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    public LoginFragment() {
        // Required empty public constructor
    }

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        // click listeners
        Button logIn = (Button) view.findViewById(R.id.loginButton);
        Button cancel = (Button) view.findViewById(R.id.cancelButton);
        logIn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        getActivity().setTitle("Log in");

        // set the navigation icon
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Drawable nav_logo = getResources().getDrawable(R.drawable.account_key, null);
        if (nav_logo != null) {
            int color = Color.parseColor("#FFFFFF");
            nav_logo.setTint(color);
            toolbar.setLogo(nav_logo);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.cancelButton : fm.popBackStack();
                break;
            case R.id.loginButton : logIn();
                break;
        }
    }

    private void logIn() {
        EditText username_et = (EditText) view.findViewById(R.id.username);
        String username = username_et.getText().toString().trim();
        String username_cap = username.substring(0,1).toUpperCase() + username.substring(1).toLowerCase();
        EditText password = (EditText) view.findViewById(R.id.password);
        final LinearLayout loadingLayout = (LinearLayout) getActivity().findViewById(R.id.loading);
        final LinearLayout loggedInLayout = (LinearLayout) getActivity().findViewById(R.id.loginLayout);
        loadingLayout.setVisibility(View.VISIBLE);
        loggedInLayout.setVisibility(View.GONE);
        ParseUser.logInInBackground(username_cap, password.getText()
                .toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                loadingLayout.setVisibility(View.GONE);
                if (e == null) {
                    Toast.makeText(getActivity(), "Done! Hello " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    TextView profileName = (TextView) getActivity().findViewById(R.id.header_username);
                    profileName.setText(ParseUser.getCurrentUser().getUsername());

                    // go back
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
                else {
                    Toast.makeText(getActivity(),"Failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    loggedInLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
