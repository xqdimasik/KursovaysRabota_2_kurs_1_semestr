package model;

public enum Category {
    SOUP("Суп"),
    SALAD("Салат"),
    MEAT("Мясо"),
    DESSERT("Десерт"),
    DRINK("Напиток"),
    OTHER("Другое");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String s) {
        if (s == null) return OTHER;
        String upper = s.trim().toUpperCase();
        try {
            return Category.valueOf(upper);
        } catch (Exception e) {
            for (Category cat : values()) {
                if (cat.displayName.equalsIgnoreCase(s.trim())) {
                    return cat;
                }
            }
            return OTHER;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}