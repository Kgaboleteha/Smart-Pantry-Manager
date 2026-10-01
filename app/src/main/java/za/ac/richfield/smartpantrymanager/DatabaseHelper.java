package za.ac.richfield.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry Table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipes Table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe Ingredients Table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_RI_ID = "id";
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QUANTITY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT);";

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL);";

        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COLUMN_RI_NAME + " TEXT NOT NULL, " +
                COLUMN_RI_QUANTITY + " REAL NOT NULL, " +
                COLUMN_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "));";

        db.execSQL(createPantryTable);
        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);

        seedInitialRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==========================================
    // PANTRY CRUD OPERATIONS
    // ==========================================

    // CREATE
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName().trim());
        cv.put(COLUMN_PANTRY_QUANTITY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit().trim());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, cv);
    }

    // READ ALL
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_PANTRY_NAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY));

                itemList.add(new PantryItem(id, name, quantity, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // UPDATE
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName().trim());
        cv.put(COLUMN_PANTRY_QUANTITY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit().trim());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());

        return db.update(TABLE_PANTRY, cv, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    // DELETE
    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // ==========================================
    // RECIPES & SEEDING OPERATIONS
    // ==========================================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
        if (cursor.moveToFirst()) {
            do {
                int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

                List<RecipeIngredient> ingredients = getIngredientsForRecipe(db, recipeId);
                recipes.add(new Recipe(recipeId, name, ingredients, instructions));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS +
                " WHERE " + COLUMN_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT));

                ingredients.add(new RecipeIngredient(name, quantity, unit));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    private void seedInitialRecipes(SQLiteDatabase db) {
        // Recipe 1: Classic Scrambled Eggs
        insertRecipeWithIngredients(db, "Classic Scrambled Eggs",
                "Whisk eggs with a pinch of salt. Melt butter in a non-stick pan over medium-low heat. Pour in eggs and stir gently until soft curds form.",
                new String[]{"egg", "butter", "salt"},
                new double[]{2.0, 1.0, 1.0},
                new String[]{"pcs", "tbsp", "pinch"});

        // Recipe 2: Tomato & Egg Stir-Fry
        insertRecipeWithIngredients(db, "Tomato and Egg Stir-Fry",
                "Scramble eggs lightly and set aside. Sauté sliced tomatoes until soft and juicy. Add back scrambled eggs, season with salt and sugar, stir gently.",
                new String[]{"egg", "tomato", "oil", "salt"},
                new double[]{3.0, 2.0, 1.0, 1.0},
                new String[]{"pcs", "pcs", "tbsp", "pinch"});

        // Recipe 3: Garlic Butter Pasta
        insertRecipeWithIngredients(db, "Garlic Butter Pasta",
                "Boil pasta until al dente. In a pan, gently sauté minced garlic in melted butter. Toss drained pasta into garlic butter with a splash of pasta water.",
                new String[]{"pasta", "garlic", "butter", "salt"},
                new double[]{200.0, 3.0, 2.0, 1.0},
                new String[]{"g", "cloves", "tbsp", "pinch"});

        // Recipe 4: French Toast
        insertRecipeWithIngredients(db, "Simple French Toast",
                "Whisk egg and milk in a bowl. Dip bread slices to coat both sides. Fry in melted butter until golden brown on both sides.",
                new String[]{"bread", "egg", "milk", "butter"},
                new double[]{2.0, 1.0, 50.0, 1.0},
                new String[]{"slices", "pcs", "ml", "tbsp"});

        // Recipe 5: Grilled Cheese Sandwich
        insertRecipeWithIngredients(db, "Crispy Grilled Cheese",
                "Butter outside of bread slices. Place cheese slices between bread. Grill on skillet over medium heat until bread is golden and cheese is melted.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2.0, 2.0, 1.0},
                new String[]{"slices", "slices", "tbsp"});

        // Recipe 6: Egg Fried Rice
        insertRecipeWithIngredients(db, "Quick Egg Fried Rice",
                "Heat oil in a pan, fry onion and garlic until fragrant. Push aside and scramble eggs. Add cooked rice and stir vigorously until heated through.",
                new String[]{"rice", "egg", "onion", "oil"},
                new double[]{250.0, 2.0, 1.0, 1.0},
                new String[]{"g", "pcs", "pcs", "tbsp"});

        // Recipe 7: Tomato Basil Pasta
        insertRecipeWithIngredients(db, "Tomato Pasta",
                "Boil pasta. Sauté chopped onion and garlic in olive oil, add chopped tomatoes and simmer into sauce. Stir pasta directly into hot sauce.",
                new String[]{"pasta", "tomato", "onion", "garlic", "oil"},
                new double[]{200.0, 3.0, 1.0, 2.0, 1.0},
                new String[]{"g", "pcs", "pcs", "cloves", "tbsp"});

        // Recipe 8: Simple Pancakes
        insertRecipeWithIngredients(db, "Fluffy Pantry Pancakes",
                "Whisk flour, sugar, milk, and egg into a smooth batter. Pour 1/4 cup ladles onto a buttered hot pan. Flip when bubbles appear.",
                new String[]{"flour", "milk", "egg", "sugar", "butter"},
                new double[]{150.0, 200.0, 1.0, 2.0, 1.0},
                new String[]{"g", "ml", "pcs", "tbsp", "tbsp"});

        // Recipe 9: Mashed Potatoes
        insertRecipeWithIngredients(db, "Creamy Mashed Potatoes",
                "Boil peeled potatoes in salted water until fork tender. Drain and mash thoroughly with butter, warm milk, and salt until smooth.",
                new String[]{"potato", "butter", "milk", "salt"},
                new double[]{4.0, 2.0, 60.0, 1.0},
                new String[]{"pcs", "tbsp", "ml", "tsp"});

        // Recipe 10: Onion Omelette
        insertRecipeWithIngredients(db, "Golden Onion Omelette",
                "Finely slice onion and sauté in butter until translucent. Pour whisked eggs over onions and cook on low heat until set.",
                new String[]{"egg", "onion", "butter", "salt"},
                new double[]{2.0, 1.0, 1.0, 1.0},
                new String[]{"pcs", "pcs", "tbsp", "pinch"});

        // Recipe 11: Cheesy Garlic Bread
        insertRecipeWithIngredients(db, "Cheesy Garlic Toast",
                "Mix softened butter with minced garlic. Spread generously on bread slices, top with cheese, and bake/toast until bubbly.",
                new String[]{"bread", "garlic", "butter", "cheese"},
                new double[]{4.0, 2.0, 2.0, 50.0},
                new String[]{"slices", "cloves", "tbsp", "g"});

        // Recipe 12: Potato Hash
        insertRecipeWithIngredients(db, "Crispy Skillet Potato Hash",
                "Dice potatoes and onions small. Sauté in oil over medium-high heat until crispy and golden brown. Season with salt.",
                new String[]{"potato", "onion", "oil", "salt"},
                new double[]{3.0, 1.0, 2.0, 1.0},
                new String[]{"pcs", "pcs", "tbsp", "tsp"});

        // Recipe 13: Banana Pancakes
        insertRecipeWithIngredients(db, "Two-Ingredient Banana Pancakes",
                "Mash ripe banana with a fork until smooth. Whisk thoroughly with eggs. Fry spoonfuls in butter over medium-low heat.",
                new String[]{"banana", "egg", "butter"},
                new double[]{1.0, 2.0, 1.0},
                new String[]{"pcs", "pcs", "tbsp"});

        // Recipe 14: Creamy Tomato Soup
        insertRecipeWithIngredients(db, "Comforting Tomato Soup",
                "Simmer chopped tomatoes and onions in butter until soft. Puree with warm milk and season with salt.",
                new String[]{"tomato", "onion", "butter", "milk", "salt"},
                new double[]{4.0, 1.0, 1.0, 100.0, 1.0},
                new String[]{"pcs", "pcs", "tbsp", "ml", "tsp"});

        // Recipe 15: Garlic Fried Rice
        insertRecipeWithIngredients(db, "Sinangag Garlic Rice",
                "Fry plenty of minced garlic in oil until golden and aromatic. Add leftover cooked rice and salt, tossing continuously.",
                new String[]{"rice", "garlic", "oil", "salt"},
                new double[]{250.0, 4.0, 2.0, 1.0},
                new String[]{"g", "cloves", "tbsp", "tsp"});

        // Recipe 16: Banana Milk Smoothie
        insertRecipeWithIngredients(db, "Simple Banana Smoothie",
                "Peel banana and blend with cold milk and a spoonful of sugar until frothy and smooth.",
                new String[]{"banana", "milk", "sugar"},
                new double[]{2.0, 250.0, 1.0},
                new String[]{"pcs", "ml", "tbsp"});
    }

    private void insertRecipeWithIngredients(SQLiteDatabase db, String name, String instructions,
                                             String[] ingredientNames, double[] quantities, String[] units) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_NAME, name);
        recipeValues.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (int i = 0; i < ingredientNames.length; i++) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(COLUMN_RI_RECIPE_ID, recipeId);
            ingValues.put(COLUMN_RI_NAME, ingredientNames[i]);
            ingValues.put(COLUMN_RI_QUANTITY, quantities[i]);
            ingValues.put(COLUMN_RI_UNIT, units[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }
}