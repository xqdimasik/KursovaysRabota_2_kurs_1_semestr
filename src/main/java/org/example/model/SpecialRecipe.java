package model;

public class SpecialRecipe extends Recipe {
    private String holiday;

    public SpecialRecipe() {
        super();
    }

    public SpecialRecipe(String title, Category category, String instruction, String holiday) {
        super(title, category, instruction);
        this.setHoliday(holiday);
    }

    @Override
    public String getPrintableFormat() {
        return "ПРАЗДНИЧНЫЙ РЕЦЕПТ\n" +
                "Для праздника: " + holiday + "\n\n" +
                super.getPrintableFormat();
    }

    @Override
    public String getShortDescription() {
        return "Праздничный: " + getTitle() + " (для " + holiday + ")";
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        if(holiday == null)
            throw new RuntimeException("null holiday");
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Праздничный рецепт: ").append(getTitle()).append("\n");
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