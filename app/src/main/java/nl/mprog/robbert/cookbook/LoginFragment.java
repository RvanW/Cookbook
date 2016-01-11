package nl.mprog.robbert.cookbook;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    View view;
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

    public void logIn() {
        EditText username = (EditText) view.findViewById(R.id.username);
        EditText password = (EditText) view.findViewById(R.id.password);
        ParseUser.logInInBackground(username.getText().toString(), password.getText()
                .toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Done! Hello " + user.getUsername(), Toast.LENGTH_SHORT).show();
                    TextView username = (TextView) getActivity().findViewById(R.id.header_username);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
                else {
                    Toast.makeText(getActivity(),"Failed! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
