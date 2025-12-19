package data;

import interfaces.Storable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import model.Recipe;
import model.SpecialRecipe;
import model.Category;
import model.Ingredient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonRecipeStorage implements Storable {

    @Override
    public void saveToFile(String filename, List<Recipe> recipes) {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File file = new File(filename);
            mapper.writeValue(file, recipes);
            System.out.println("Данные сохранены в файл: " + filename);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    @Override
    public List<Recipe> loadFromFile(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(filename);

            if (!file.exists()) {
                System.out.println("Файл " + filename + " не найден. Будет создан новый.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                System.out.println("Файл " + filename + " пустой.");
                return new ArrayList<>();
            }

            JsonNode rootNode = mapper.readTree(file);
            List<Recipe> recipes = new ArrayList<>();

            for (JsonNode recipeNode : rootNode) {
                String title = recipeNode.get("title").asText();
                String categoryStr = recipeNode.get("category").asText();
                Category category = Category.fromString(categoryStr);
                String instruction = recipeNode.get("instruction").asText();

                JsonNode ingredientsNode = recipeNode.get("ingredients");
                List<Ingredient> ingredients = new ArrayList<>();
                if (ingredientsNode != null && ingredientsNode.isArray()) {
                    for (JsonNode ingNode : ingredientsNode) {
                        String name = ingNode.get("name").asText();
                        String amount = ingNode.get("amount").asText();
                        ingredients.add(new Ingredient(name, amount));
                    }
                }

                Recipe recipe;
                JsonNode holidayNode = recipeNode.get("holiday");
                if (holidayNode != null) {
                    String holiday = holidayNode.asText();
                    SpecialRecipe specialRecipe = new SpecialRecipe(title, category, instruction, holiday);
                    specialRecipe.setIngredients(ingredients);
                    recipe = specialRecipe;
                } else {
                    recipe = new Recipe(title, category, instruction);
                    recipe.setIngredients(ingredients);
                }

                recipes.add(recipe);
            }

            System.out.println("Загружено " + recipes.size() + " рецептов из файла: " + filename);
            return recipes;

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке из файла: " + e.getMessage());
            System.out.println("Создаю новый список рецептов...");
            return new ArrayList<>();
        }
    }
}