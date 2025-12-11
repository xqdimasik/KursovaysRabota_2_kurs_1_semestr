package example.model;

import example.interfaces.Printable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Printable {
    private String title;
    private Category category;
    private List<Ingredient> ingredients;
    private String instruction;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(String title, Category category, String instruction) {
        this.title = title;
        this.category = category;
        this.instruction = instruction;
        this.ingredients = new ArrayList<>();
    }

    @Override
    public String getPrintableFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("РЕЦЕПТ: ").append(title).append("\n");
        sb.append("Категория: ").append(category.getDisplayName()).append("\n");
        sb.append("\nИнгредиенты:\n");

        if (ingredients != null && !ingredients.isEmpty()) {
            for (Ingredient ing : ingredients) {
                sb.append("   • ").append(ing).append("\n");
            }
        } else {
            sb.append("   (нет ингредиентов)\n");
        }

        sb.append("\nИнструкция:\n");
        sb.append("   ").append(instruction.replace("\n", "\n   "));

        return sb.toString();
    }

    @Override
    public String getShortDescription() {
        String shortInst = instruction.length() > 50 ?
                instruction.substring(0, 47) + "..." : instruction;
        return title + " - " + shortInst;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void addIngredient(Ingredient i) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        ingredients.add(i);
    }

    public void removeIngredient(int idx) {
        if (ingredients != null && idx >= 0 && idx < ingredients.size()) {
            ingredients.remove(idx);
        }
    }

    public void editIngredient(int idx, String newName, String newAmount) {
        if (ingredients != null && idx >= 0 && idx < ingredients.size()) {
            ingredients.get(idx).setName(newName);
            ingredients.get(idx).setAmount(newAmount);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Рецепт: ").append(title).append("\n");
        sb.append("Категория: ").append(category.getDisplayName()).append("\n");
        sb.append("\nИнгредиенты:\n");

        if (ingredients != null && !ingredients.isEmpty()) {
            for (Ingredient ing : ingredients) {
                sb.append("   • ").append(ing).append("\n");
            }
        } else {
            sb.append("   (нет ингредиентов)\n");
        }

        sb.append("\nИнструкция:\n");
        sb.append("   ").append(instruction.replace("\n", "\n   ")).append("\n");

        return sb.toString();
    }
}