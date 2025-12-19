package model;

public class Ingredient {
    private String name;
    private String amount;

    public Ingredient() {}

    public Ingredient(String name, String amount) {
        this.setName(name);
        this.setAmount(amount);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null)
            throw new RuntimeException("null name");
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        if(amount == null)
            throw new RuntimeException("null amount");
        this.amount = amount;
    }

    @Override
    public String toString() {
        if (amount == null || amount.isEmpty()) {
            return name;
        }
        return name + " (" + amount + ")";
    }
}