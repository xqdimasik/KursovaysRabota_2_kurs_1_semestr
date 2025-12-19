package interfaces;

import java.util.List;
import model.Recipe;

public interface Searchable {
    List<Recipe> searchByTitle(String query);
    List<Recipe> searchByIngredient(String query);
}