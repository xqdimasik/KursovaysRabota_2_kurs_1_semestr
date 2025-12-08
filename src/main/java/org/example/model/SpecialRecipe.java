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

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        String base = super.toString();
        return base.replace("\n",
                "ПРАЗДНИЧНЫЙ РЕЦЕПТ\n" +
                        "Для праздника: " + holiday + "\n" +
                        "\n");
    }
}