package za.ac.richfield.smartpantrymanager;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private List<RecipeIngredient> ingredients;
    private String instructions;

    public Recipe(int id, String name, List<RecipeIngredient> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
}