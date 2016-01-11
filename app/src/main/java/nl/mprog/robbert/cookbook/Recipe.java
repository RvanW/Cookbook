package nl.mprog.robbert.cookbook;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Robbert on 10-1-2016.
 */
@ParseClassName("Recipe")
public class Recipe extends ParseObject {

    public Recipe() {
        // A default constructor is required.
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public String getRating() {
        return getString("rating");
    }

    public void setRating(String rating) {
        put("rating", rating);
    }

    public ParseFile getImageFile() {
        return getParseFile("image");
    }

    public void setImageFile(ParseFile file) {
        put("image", file);
    }

    public Boolean getPublic() { return getBoolean("public"); }

    public void setPublic(Boolean bool) {
        put("public", bool);
    }
}