package nl.mprog.robbert.cookbook;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Robbert on 10-1-2016.
 */
public class ParseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Recipe.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}