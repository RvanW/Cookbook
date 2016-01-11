package nl.mprog.robbert.cookbook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    public SignUpFragment() {
        // Required empty public constructor
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        // click listeners
        Button signUp = (Button) view.findViewById(R.id.signUpButton);
        Button cancel = (Button) view.findViewById(R.id.cancelButton);
        signUp.setOnClickListener(this);
        cancel.setOnClickListener(this);
        getActivity().setTitle("Sign up");
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        for(int i = 0; i < 5; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (v.getId()) {
            case R.id.cancelButton : fm.popBackStack();
                break;
            case R.id.signUpButton : signUp();
                break;
        }

    }

    private void signUp() {
        EditText username_edittext = (EditText) view.findViewById(R.id.username);
        String username = username_edittext.getText().toString().trim();
        EditText password_edittext = (EditText) view.findViewById(R.id.password1);
        String password = password_edittext.getText().toString().trim();
        EditText confirm_edittext = (EditText) view.findViewById(R.id.password2);
        String confirm = confirm_edittext.getText().toString().trim();

        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder("Error: ");
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append("Empty username");
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(", ");
            }
            validationError = true;
            validationErrorMessage.append("Empty password");
        }
        if (!password.equals(confirm)) {
            if (validationError) {
                validationErrorMessage.append(", ");
            }
            validationError = true;
            validationErrorMessage.append("Passwords do not match");
        }
        validationErrorMessage.append(".");
        if (validationError) {
            Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
        }
        else {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            // Call the Parse signup method
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Succes!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Error saving: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
