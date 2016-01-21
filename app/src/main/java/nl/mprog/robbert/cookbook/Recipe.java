package nl.mprog.robbert.cookbook;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robbert on 10-1-2016.
 */
@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Serializable {

    private boolean favorite;

    public Recipe() {
        // A default constructor is required.
    }
    public String getId() {
        return getString("objectId");
    }

    public void setId(String id) {
        put("objectId", id);
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
        if (file != null) {
            put("image", file);
        }
    }

    public Boolean getPublic() { return getBoolean("public"); }

    public void setPublic(Boolean bool) {
        put("public", bool);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public List<String> getIngredients(){
        return getList("ingredients");
    }
    public void setIngredients(List<String> ingredientsList){
        if (ingredientsList != null)
            put("ingredients", ingredientsList);
    }
}