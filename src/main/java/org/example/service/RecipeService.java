package org.example.service;

import org.example.data.JsonRecipeStorage;
import org.example.model.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeService {
    private List<Recipe> recipes;
    private static final String JSON_FILE = "recipes.json";

    public RecipeService() {
        recipes = JsonRecipeStorage.loadFromJson(JSON_FILE);

        if (recipes.isEmpty()) {
            System.out.println("Файл рецептов пустой, добавляю примеры...");
            addExampleRecipes();
        }
    }

    private void addExampleRecipes() {
        Recipe borscht = new Recipe("Борщ украинский", Category.SOUP,
                "1. Мясо отварить до готовности.\n" +
                        "2. Свеклу натереть, потушить с томатной пастой.\n" +
                        "3. Добавить картофель, капусту, морковь, лук.\n" +
                        "4. Варить до готовности овощей.\n" +
                        "5. Подавать со сметаной и зеленью.");
        borscht.addIngredient(new Ingredient("Говядина", "500 г"));
        borscht.addIngredient(new Ingredient("Свекла", "2 шт"));
        borscht.addIngredient(new Ingredient("Капуста", "300 г"));
        borscht.addIngredient(new Ingredient("Картофель", "4 шт"));
        recipes.add(borscht);

        Recipe olivie = new Recipe("Салат Оливье", Category.SALAD,
                "1. Отварить овощи и яйца.\n" +
                        "2. Все ингредиенты нарезать кубиками.\n" +
                        "3. Добавить горошек.\n" +
                        "4. Заправить майонезом, посолить.\n" +
                        "5. Украсить зеленью.");
        olivie.addIngredient(new Ingredient("Картофель", "4 шт"));
        olivie.addIngredient(new Ingredient("Морковь", "2 шт"));
        olivie.addIngredient(new Ingredient("Яйца", "4 шт"));
        olivie.addIngredient(new Ingredient("Колбаса", "300 г"));
        recipes.add(olivie);

        Recipe charlotte = new Recipe("Шарлотка с яблоками", Category.DESSERT,
                "1. Взбить яйца с сахаром.\n" +
                        "2. Добавить муку и разрыхлитель.\n" +
                        "3. Яблоки нарезать дольками.\n" +
                        "4. Выложить яблоки в форму, залить тестом.\n" +
                        "5. Выпекать 40 минут при 180°C.");
        charlotte.addIngredient(new Ingredient("Яйца", "4 шт"));
        charlotte.addIngredient(new Ingredient("Сахар", "1 стакан"));
        charlotte.addIngredient(new Ingredient("Мука", "1 стакан"));
        charlotte.addIngredient(new Ingredient("Яблоки", "5 шт"));
        recipes.add(charlotte);

        SpecialRecipe newYearCake = new SpecialRecipe(
                "Новогодний торт",
                Category.DESSERT,
                "1. Приготовить бисквитное тесто.\n" +
                        "2. Выпекать 30 минут при 180°C.\n" +
                        "3. Пропитать кремом.\n" +
                        "4. Украсить новогодней символикой.",
                "Новый год"
        );
        newYearCake.addIngredient(new Ingredient("Мука", "300 г"));
        newYearCake.addIngredient(new Ingredient("Яйца", "4 шт"));
        newYearCake.addIngredient(new Ingredient("Сахар", "200 г"));
        newYearCake.addIngredient(new Ingredient("Сливки", "500 мл"));
        newYearCake.addIngredient(new Ingredient("Красная икра", "для украшения"));
        recipes.add(newYearCake);

        SpecialRecipe easterCake = new SpecialRecipe(
                "Пасхальный кулич",
                Category.DESSERT,
                "1. Приготовить дрожжевое тесто.\n" +
                        "2. Добавить изюм и цукаты.\n" +
                        "3. Выпекать в формах 40 минут.\n" +
                        "4. Покрыть глазурью.",
                "Пасха"
        );
        easterCake.addIngredient(new Ingredient("Мука", "500 г"));
        easterCake.addIngredient(new Ingredient("Дрожжи", "25 г"));
        easterCake.addIngredient(new Ingredient("Изюм", "100 г"));
        easterCake.addIngredient(new Ingredient("Яйца", "3 шт"));
        recipes.add(easterCake);

        saveAllRecipes();
        System.out.println("Добавлено " + recipes.size() + " примеров рецептов.");
    }

    public List<Recipe> getAll() {
        return new ArrayList<>(recipes);
    }

    public void addRecipe(Recipe r) {
        recipes.add(r);
        saveAllRecipes();
        System.out.println("Добавлен рецепт: " + r.getTitle());
    }

    public void removeRecipe(int idx) {
        if (idx >= 0 && idx < recipes.size()) {
            Recipe removed = recipes.remove(idx);
            saveAllRecipes();
            System.out.println("Удален рецепт: " + removed.getTitle());
        }
    }

    public void updateRecipe(int idx, Recipe r) {
        if (idx >= 0 && idx < recipes.size()) {
            recipes.set(idx, r);
            saveAllRecipes();
            System.out.println("Обновлен рецепт: " + r.getTitle());
        }
    }

    private void saveAllRecipes() {
        JsonRecipeStorage.saveToJson(JSON_FILE, recipes);
    }

    public List<Recipe> searchByTitle(String query) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (r.getTitle().toLowerCase().contains(query.toLowerCase())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Recipe> searchByIngredient(String query) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) {
            if (r.getIngredients() != null) {
                for (Ingredient ing : r.getIngredients()) {
                    if (ing.getName().toLowerCase().contains(query.toLowerCase())) {
                        result.add(r);
                        break;
                    }
                }
            }
        }
        return result;
    }
}