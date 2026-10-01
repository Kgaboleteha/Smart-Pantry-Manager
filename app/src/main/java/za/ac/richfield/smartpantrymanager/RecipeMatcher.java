package za.ac.richfield.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    /**
     * Filters all available recipes against current pantry items.
     * Returns ONLY recipes where every single ingredient is present in sufficient quantity.
     */
    public static List<Recipe> getStrictlySuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> matchedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (isStrictMatch(recipe, pantryItems)) {
                matchedRecipes.add(recipe);
            }
        }
        return matchedRecipes;
    }

    /**
     * Checks if all ingredients for a given recipe are satisfied by current pantry inventory.
     */
    public static boolean isStrictMatch(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            boolean satisfied = false;

            for (PantryItem pantry : pantryItems) {
                if (namesMatch(required.getName(), pantry.getName())) {
                    // Check if user has at least the required quantity
                    if (pantry.getQuantity() >= required.getRequiredQuantity()) {
                        satisfied = true;
                        break;
                    }
                }
            }

            // If even a single ingredient is missing or insufficient, recipe cannot be suggested
            if (!satisfied) {
                return false;
            }
        }
        return true;
    }

    /**
     * Normalizes ingredient names to handle real-world variations:
     * - Trims excess spaces
     * - Lowercases text
     * - Strips common English plural suffixes ("es", "s")
     */
    public static boolean namesMatch(String rawName1, String rawName2) {
        if (rawName1 == null || rawName2 == null) return false;

        String norm1 = normalize(rawName1);
        String norm2 = normalize(rawName2);

        return norm1.equals(norm2);
    }

    public static String normalize(String input) {
        String cleaned = input.trim().toLowerCase(Locale.ROOT);

        // Handle common plural endings for kitchen staples
        if (cleaned.endsWith("ies")) {
            // e.g., berries -> berry
            cleaned = cleaned.substring(0, cleaned.length() - 3) + "y";
        } else if (cleaned.endsWith("es")) {
            // e.g., tomatoes -> tomato, potatoes -> potato
            cleaned = cleaned.substring(0, cleaned.length() - 2);
        } else if (cleaned.endsWith("s") && !cleaned.endsWith("ss")) {
            // e.g., eggs -> egg, onions -> onion, cloves -> clove
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    /**
     * Helper to find missing or insufficient ingredients (useful for debugging and video demonstration).
     */
    public static List<String> getMissingIngredients(Recipe recipe, List<PantryItem> pantryItems) {
        List<String> missing = new ArrayList<>();

        for (RecipeIngredient required : recipe.getIngredients()) {
            boolean satisfied = false;
            for (PantryItem pantry : pantryItems) {
                if (namesMatch(required.getName(), pantry.getName()) &&
                        pantry.getQuantity() >= required.getRequiredQuantity()) {
                    satisfied = true;
                    break;
                }
            }
            if (!satisfied) {
                missing.add(required.getName() + " (" + required.getRequiredQuantity() + " " + required.getUnit() + ")");
            }
        }
        return missing;
    }
}