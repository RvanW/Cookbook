package nl.mprog.robbert.cookbook;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Robbert van Waardhuizen (10543147) on 10-1-2016.
 * Project for University of Amsterdam
 */
public class ParseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Recipe.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}