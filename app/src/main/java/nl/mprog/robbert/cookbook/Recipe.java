package nl.mprog.robbert.cookbook;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Robbert van Waardhuizen (10543147) on 10-1-2016.
 * Project for University of Amsterdam
 */
@ParseClassName("Recipe")
class Recipe extends ParseObject implements Serializable {

    private boolean favorite;
    private float avg_rating;

    public Recipe() {
        // A default constructor is required.
    }

    public String getId() {
        return getObjectId();
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

    public Float getAvg_Rating() {
        Number total = getNumber("totalRating");
        Number number_ratings = getNumber("numberOfRatings");
        if (total == null || number_ratings == null ||
                number_ratings.floatValue() == 0) {
            return (float) 0;
        }
        avg_rating = Math.round((total.floatValue() / number_ratings.floatValue())*2)/2f;
        return avg_rating;
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

    public static final Comparator<Recipe> COMPARE_BY_RATING = new Comparator<Recipe>() {
        public int compare(Recipe one, Recipe other) {
            return one.avg_rating < other.avg_rating ? +1 : one.avg_rating > other.avg_rating ? -1 : 0;
        }
    };
}