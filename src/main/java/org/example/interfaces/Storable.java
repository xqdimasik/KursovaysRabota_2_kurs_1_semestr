package interfaces;

import java.util.List;
import model.Recipe;

public interface Storable {
    void saveToFile(String filename, List<Recipe> recipes);
    List<Recipe> loadFromFile(String filename);
}