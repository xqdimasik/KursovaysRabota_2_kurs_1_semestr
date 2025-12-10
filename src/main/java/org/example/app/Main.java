package org.example.app;

import org.example.model.*;
import org.example.service.RecipeService;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final RecipeService service = new RecipeService();
    private static final String JSON_FILE = "recipes.json";
    private static final String EXPORT_DIR = "exported_recipes/";

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String PURPLE = "\u001B[35m";

    public static void main(String[] args) {
        java.io.File exportDir = new java.io.File(EXPORT_DIR);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Выберите пункт (0-6): ", 0, 6);

            switch (choice) {
                case 1:
                    addRecipe();
                    break;
                case 2:
                    editRecipe();
                    break;
                case 3:
                    removeRecipe();
                    break;
                case 4:
                    listRecipes();
                    break;
                case 5:
                    searchByTitle();
                    break;
                case 6:
                    searchByIngredient();
                    break;
                case 0:
                    running = exitProgram();
                    break;
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println(YELLOW + "            КАТАЛОГ РЕЦЕПТОВ          " + RESET);

        System.out.println(BLUE + "\nГЛАВНОЕ МЕНЮ" + RESET);
        System.out.println("1. Добавить рецепт");
        System.out.println("2. Редактировать рецепт");
        System.out.println("3. Удалить рецепт");
        System.out.println("4. Показать все рецепты");
        System.out.println("5. Поиск по названию");
        System.out.println("6. Поиск по ингредиенту");
        System.out.println("0. Выход");
    }

    private static int readInt(String prompt, int min, int max) {
        int num;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                num = Integer.parseInt(line);
                if (num >= min && num <= max) {
                    return num;
                }
                System.out.println(RED + "Ошибка! Число должно быть от " + min + " до " + max + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ошибка! Введите целое число." + RESET);
            }
        }
    }

    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println(RED + "Ошибка! Поле не может быть пустым." + RESET);
        }
    }

    private static String readIngredientName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(RED + "Ошибка! Название не может быть пустым." + RESET);
        }
    }

    private static void addRecipe() {
        System.out.println(YELLOW + "\nДОБАВЛЕНИЕ РЕЦЕПТА" + RESET);

        System.out.println("\nХотите создать праздничный рецепт? (да/нет): ");
        String isSpecial = scanner.nextLine().trim().toLowerCase();
        boolean special = isSpecial.equals("да") || isSpecial.equals("yes") || isSpecial.equals("д");

        String title = readString("Название рецепта: ");

        System.out.println("\nКатегории:");
        for (Category cat : Category.values()) {
            System.out.println("  • " + cat.name() + " - " + cat.getDisplayName());
        }
        String catInput = readString("\nВыберите категорию (например: DESSERT или Десерт): ");
        Category category = Category.fromString(catInput);

        System.out.println("Инструкция приготовления (можно несколько строк, 'готово' для окончания):");
        StringBuilder instructionBuilder = new StringBuilder();

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("готово")) {
                if (instructionBuilder.length() == 0) {
                    System.out.println(RED + "Инструкция не может быть пустой!" + RESET);
                    continue;
                }
                break;
            }
            if (instructionBuilder.length() > 0) {
                instructionBuilder.append("\n");
            }
            instructionBuilder.append(line);
        }

        Recipe recipe;
        String holiday = "";

        if (special) {
            holiday = readString("Для какого праздника этот рецепт?: ");
            recipe = new SpecialRecipe(title, category, instructionBuilder.toString(), holiday);
        } else {
            recipe = new Recipe(title, category, instructionBuilder.toString());
        }

        System.out.println(GREEN + "\nДОБАВЛЕНИЕ ИНГРЕДИЕНТОВ" + RESET);
        System.out.println("(введите 'готово' чтобы закончить)");

        int ingredientCount = 0;
        while (true) {
            System.out.println("\nИнгредиент #" + (ingredientCount + 1));
            String ingName = readIngredientName("Название ингредиента: ");
            if (ingName.equalsIgnoreCase("готово")) {
                if (ingredientCount == 0) {
                    System.out.println(RED + "Добавьте хотя бы один ингредиент!" + RESET);
                    continue;
                }
                break;
            }
            String amount = readString("Количество (например: '200 г', '2 шт', '1 ст.л.'): ");
            recipe.addIngredient(new Ingredient(ingName, amount));
            ingredientCount++;
            System.out.println(GREEN + "Добавлено: " + ingName + " (" + amount + ")" + RESET);
        }

        service.addRecipe(recipe);
        if (special) {
            System.out.println(YELLOW + "\nПраздничный рецепт '" + title + "' для " + holiday + " успешно добавлен!" + RESET);
        } else {
            System.out.println(GREEN + "\nРецепт '" + title + "' успешно добавлен!" + RESET);
        }
    }

    private static void listRecipes() {
        List<Recipe> all = service.getAll();
        if (all.isEmpty()) {
            System.out.println(YELLOW + "\nКаталог рецептов пуст." + RESET);
            return;
        }

        System.out.println(CYAN + "\nВСЕ РЕЦЕПТЫ (" + all.size() + ")" + RESET);
        for (int i = 0; i < all.size(); i++) {
            Recipe r = all.get(i);
            System.out.println(GREEN + "[" + (i + 1) + "] " + RESET +
                    r.getTitle() + " (" + r.getCategory().getDisplayName() + ")");
        }

        if (!all.isEmpty()) {
            System.out.print("\nПоказать детали рецепта? (введите номер или 'нет'): ");
            String input = scanner.nextLine().trim();
            if (!input.equalsIgnoreCase("нет")) {
                try {
                    int idx = Integer.parseInt(input);
                    // Проверка от 1 до количества рецептов
                    if (idx >= 1 && idx <= all.size()) {
                        System.out.println("\n" + all.get(idx - 1));
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private static void removeRecipe() {
        listRecipes();
        List<Recipe> all = service.getAll();
        if (all.isEmpty()) return;

        System.out.print("\nВведите номер рецепта для удаления: ");
        String input = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(input);
            if (idx >= 1 && idx <= all.size()) {
                String title = all.get(idx - 1).getTitle();
                System.out.print("Вы уверены, что хотите удалить рецепт '" + title + "'? (да/нет): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("да") || confirm.equals("yes") || confirm.equals("д")) {
                    service.removeRecipe(idx - 1);
                } else {
                    System.out.println("Удаление отменено.");
                }
            } else {
                System.out.println(RED + "Неверный номер рецепта!" + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Введите число!" + RESET);
        }
    }

    private static void editRecipe() {
        listRecipes();
        List<Recipe> all = service.getAll();
        if (all.isEmpty()) return;

        int idx = readInt("\nВведите номер рецепта для редактирования: ", 1, all.size());
        Recipe r = all.get(idx - 1);

        System.out.println(YELLOW + "\nРЕДАКТИРОВАНИЕ: " + r.getTitle() + "" + RESET);

        String newTitle = readString("Новое название (Enter - оставить '" + r.getTitle() + "'): ");
        if (!newTitle.isEmpty()) r.setTitle(newTitle);

        String catInput = readString("Новая категория (Enter - оставить '" + r.getCategory().getDisplayName() + "'): ");
        if (!catInput.isEmpty()) r.setCategory(Category.fromString(catInput));

        System.out.println("Текущая инструкция:\n" + r.getInstruction());
        String newInst = readString("\nНовая инструкция (Enter - оставить без изменений): ");
        if (!newInst.isEmpty()) r.setInstruction(newInst);

        if (r instanceof SpecialRecipe) {
            SpecialRecipe sr = (SpecialRecipe) r;
            String currentHoliday = sr.getHoliday();
            String newHoliday = readString("Новый праздник (Enter - оставить '" + currentHoliday + "'): ");
            if (!newHoliday.isEmpty()) {
                sr.setHoliday(newHoliday);
            }
        }

        System.out.println("\nТЕКУЩИЕ ИНГРЕДИЕНТЫ:");
        List<Ingredient> ingredients = r.getIngredients();
        if (ingredients.isEmpty()) {
            System.out.println("   (нет ингредиентов)");
        } else {
            for (int i = 0; i < ingredients.size(); i++) {
                System.out.println("   " + i + ": " + ingredients.get(i));
            }
        }

        boolean editing = true;
        while (editing) {
            System.out.println("\n1. Добавить ингредиент");
            System.out.println("2. Редактировать ингредиент");
            System.out.println("3. Удалить ингредиент");
            System.out.println("0. Закончить редактирование ингредиентов");

            int choice = readInt("Выберите действие: ", 0, 3);
            switch (choice) {
                case 1:
                    String ingName = readIngredientName("Название ингредиента: ");
                    String amount = readString("Количество: ");
                    r.addIngredient(new Ingredient(ingName, amount));
                    System.out.println(GREEN + "Ингредиент добавлен" + RESET);
                    break;
                case 2:
                    if (!ingredients.isEmpty()) {
                        int ingIdx = readInt("Номер ингредиента для редактирования: ", 0, ingredients.size() - 1);
                        String newName = readIngredientName("Новое название: ");
                        String newAmount = readString("Новая мерка: ");
                        r.editIngredient(ingIdx, newName, newAmount);
                        System.out.println(GREEN + "Ингредиент изменён" + RESET);
                    } else {
                        System.out.println(RED + "Нет ингредиентов для редактирования!" + RESET);
                    }
                    break;
                case 3:
                    if (!ingredients.isEmpty()) {
                        int ingIdx = readInt("Номер ингредиента для удаления: ", 0, ingredients.size() - 1);
                        r.removeIngredient(ingIdx);
                        System.out.println(RED + "Ингредиент удалён" + RESET);
                    } else {
                        System.out.println(RED + "Нет ингредиентов для удаления!" + RESET);
                    }
                    break;
                case 0:
                    editing = false;
                    break;
            }
        }

        service.updateRecipe(idx - 1, r);
        System.out.println(GREEN + "\nРецепт успешно обновлён!" + RESET);
    }

    private static void searchByTitle() {
        String query = readString("Введите название для поиска: ");
        List<Recipe> result = service.searchByTitle(query);
        if (result.isEmpty()) {
            System.out.println(RED + "\nРецепты не найдены." + RESET);
        } else {
            System.out.println(GREEN + "\nНАЙДЕНО " + result.size() + " РЕЦЕПТОВ" + RESET);
            for (Recipe r : result) {
                System.out.println(r);
            }
        }
    }

    private static void searchByIngredient() {
        String query = readString("Введите ингредиент для поиска: ");
        List<Recipe> result = service.searchByIngredient(query);
        if (result.isEmpty()) {
            System.out.println(RED + "\nРецепты не найдены." + RESET);
        } else {
            System.out.println(GREEN + "\nНАЙДЕНО " + result.size() + " РЕЦЕПТОВ" + RESET);
            for (Recipe r : result) {
                System.out.println(r);
            }
        }
    }

    private static boolean exitProgram() {
        System.out.println(GREEN + "   Все данные сохранены в recipes.json");
        return false;
    }
}