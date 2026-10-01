package za.ac.richfield.smartpantrymanager;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeMatcherTest {

    @Test
    public void testStrictMatching_SuccessWhenAllIngredientsPresent() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("egg", 2, "pcs"));
        ingredients.add(new RecipeIngredient("butter", 1, "tbsp"));

        Recipe recipe = new Recipe(1, "Scrambled Eggs", ingredients, "Cook eggs in butter.");

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("egg", 3, "pcs", null));
        pantry.add(new PantryItem("butter", 2, "tbsp", null));

        assertTrue(RecipeMatcher.isStrictMatch(recipe, pantry));
    }

    @Test
    public void testStrictMatching_FailsWhenOneIngredientMissing() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("egg", 2, "pcs"));
        ingredients.add(new RecipeIngredient("butter", 1, "tbsp"));
        ingredients.add(new RecipeIngredient("salt", 1, "pinch"));

        Recipe recipe = new Recipe(2, "Salted Eggs", ingredients, "Cook eggs in butter with salt.");

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("egg", 2, "pcs", null));
        pantry.add(new PantryItem("butter", 1, "tbsp", null));
        // salt is missing entirely

        assertFalse(RecipeMatcher.isStrictMatch(recipe, pantry));
    }

    @Test
    public void testStrictMatching_FailsWhenInsufficientQuantity() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("pasta", 200, "g"));

        Recipe recipe = new Recipe(3, "Plain Pasta", ingredients, "Boil pasta.");

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("pasta", 150, "g", null)); // Only 150g available

        assertFalse(RecipeMatcher.isStrictMatch(recipe, pantry));
    }

    @Test
    public void testNormalization_HandlesPluralEndings() {
        assertTrue(RecipeMatcher.namesMatch("tomato", "tomatoes"));
        assertTrue(RecipeMatcher.namesMatch("potatoes", "potato"));
        assertTrue(RecipeMatcher.namesMatch("eggs", "egg"));
        assertTrue(RecipeMatcher.namesMatch("Onion", "onions"));
    }
}