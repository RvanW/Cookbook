package nl.mprog.robbert.cookbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            // load initial fragment (gallery) if there is none loaded yet
            Fragment fragment = new GalleryFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.frag_holder, fragment).commit();

        }
        TextView profileName = (TextView) findViewById(R.id.header_username);
        if (ParseUser.getCurrentUser() != null) {
            profileName.setText(ParseUser.getCurrentUser().getUsername());
        }
        else {
            profileName.setText("Logged out");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Create a new fragment based on navigation selection
        Fragment fragment = null;
        String TAG = "";
        if (id == R.id.nav_gallery) {
            GalleryFragment galleryFragment = new GalleryFragment();
            fragment = galleryFragment;
            TAG = "gallery";
        }
        else if (ParseUser.getCurrentUser() == null) { // send user to account panel if they chose anything besides the public gallery..
            fragment = new AccountFragment();
            TAG = "account";
            if (id != R.id.nav_account)
                Toast.makeText(this,"Please login or sign up first to use this feature",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_camera) {
            AddRecipeFragment addRecipeFragment = AddRecipeFragment.newInstance(new Recipe());
            fragment = addRecipeFragment;
            TAG = "new_recipe";
        } else if (id == R.id.nav_myrecipes) {
            fragment = new MyRecipesFragment();
            TAG = "my_recipes";
        } else if (id == R.id.nav_favorites) {
            fragment = new FavoritesFragment();
            TAG = "favorites";
        } else if (id == R.id.nav_account) {
            fragment = new AccountFragment();
            TAG = "account";
        }

        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.frag_holder, fragment, TAG)
                    .addToBackStack(TAG)
                    .commit();
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // override this with a try/catch to prevent crashing in rare cases (weird java bug)
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

}

