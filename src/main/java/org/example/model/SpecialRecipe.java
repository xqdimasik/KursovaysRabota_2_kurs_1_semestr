package org.example.model;

public class SpecialRecipe extends Recipe {
    private String holiday;

    public SpecialRecipe() {
        super();
    }

    public SpecialRecipe(String title, Category category, String instruction, String holiday) {
        super(title, category, instruction);
        this.holiday = holiday;
    }

    @Override
    public String getRecipeType() {
        return "Праздничный рецепт";
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("").append(getTitle()).append("\n");
        sb.append("Категория: ").append(getCategory().getDisplayName()).append("\n");
        sb.append("Праздник: ").append(holiday).append("\n");
        sb.append("\nИнгредиенты:\n");

        if (getIngredients() != null && !getIngredients().isEmpty()) {
            for (Ingredient ing : getIngredients()) {
                sb.append("   • ").append(ing).append("\n");
            }
        } else {
            sb.append("   (нет ингредиентов)\n");
        }

        sb.append("\nИнструкция:\n");
        sb.append("   ").append(getInstruction().replace("\n", "\n   ")).append("\n");

        return sb.toString();
    }
}