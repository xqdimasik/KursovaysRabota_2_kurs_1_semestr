package org.example.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.Recipe;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonRecipeStorage {

    public static void saveToJson(String filename, List<Recipe> recipes) {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File file = new File(filename);
            mapper.writeValue(file, recipes);
        } catch (IOException e) {
        }
    }

    public static List<Recipe> loadFromJson(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(filename);

            if (!file.exists()) {
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Recipe.class)
            );

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}