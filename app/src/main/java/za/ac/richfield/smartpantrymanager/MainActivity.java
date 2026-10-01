package za.ac.richfield.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemListener, RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecyclerView rvPantry;
    private RecyclerView rvRecipes;
    private PantryAdapter pantryAdapter;
    private RecipeAdapter recipeAdapter;

    private LinearLayout layoutEmptyState;
    private LinearLayout layoutSettings;
    private TextView tvEmptyMessage;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNav;

    private List<PantryItem> pantryList = new ArrayList<>();
    private List<Recipe> allRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI Elements
        rvPantry = findViewById(R.id.rv_pantry);
        rvRecipes = findViewById(R.id.rv_recipes);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        layoutSettings = findViewById(R.id.layout_settings);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        fabAdd = findViewById(R.id.fab_add_pantry_item);
        bottomNav = findViewById(R.id.bottom_navigation);

        setupRecyclerViews();
        setupNavigation();
        loadInitialData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void setupRecyclerViews() {
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        pantryAdapter = new PantryAdapter(pantryList, this);
        rvPantry.setAdapter(pantryAdapter);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), this);
        rvRecipes.setAdapter(recipeAdapter);
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_pantry) {
                showPantryTab();
                return true;
            } else if (itemId == R.id.nav_suggestions) {
                showSuggestionsTab();
                return true;
            } else if (itemId == R.id.nav_settings) {
                showSettingsTab();
                return true;
            }
            return false;
        });
    }

    private void loadInitialData() {
        pantryList = dbHelper.getAllPantryItems();
        allRecipes = dbHelper.getAllRecipes();
        pantryAdapter.updateList(pantryList);
        showPantryTab();
    }

    private void showPantryTab() {
        rvPantry.setVisibility(View.VISIBLE);
        rvRecipes.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.GONE);
        fabAdd.setVisibility(View.VISIBLE);

        pantryList = dbHelper.getAllPantryItems();
        pantryAdapter.updateList(pantryList);

        if (pantryList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("Your pantry is empty.\nTap '+' to add your leftover ingredients!");
        } else {
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showSuggestionsTab() {
        rvPantry.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);

        pantryList = dbHelper.getAllPantryItems();
        List<Recipe> suggestions = RecipeMatcher.getStrictlySuggestedRecipes(allRecipes, pantryList);

        if (suggestions.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText("No recipes match your pantry yet — add more ingredients to cook right away!");
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
            recipeAdapter.updateList(suggestions);
        }
    }

    private void showSettingsTab() {
        rvPantry.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.VISIBLE);
    }

    // ==========================================
    // PANTRY CRUD & DIALOGS
    // ==========================================

    private void showAddEditDialog(final PantryItem existingItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_pantry, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etName = dialogView.findViewById(R.id.et_dialog_name);
        EditText etQuantity = dialogView.findViewById(R.id.et_dialog_quantity);
        EditText etUnit = dialogView.findViewById(R.id.et_dialog_unit);
        EditText etExpiry = dialogView.findViewById(R.id.et_dialog_expiry);

        boolean isEdit = existingItem != null;
        if (isEdit) {
            tvTitle.setText("Edit Pantry Ingredient");
            etName.setText(existingItem.getName());
            etQuantity.setText(String.valueOf(existingItem.getQuantity()));
            etUnit.setText(existingItem.getUnit());
            etExpiry.setText(existingItem.getExpiryDate());
        } else {
            tvTitle.setText("Add Pantry Ingredient");
        }

        builder.setPositiveButton(isEdit ? "Update" : "Add", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Custom listener for positive button to prevent premature dismissal on validation failure
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            // Strict Input Validation (Section 3.1)
            if (name.isEmpty()) {
                etName.setError("Ingredient name is required");
                return;
            }
            if (qtyStr.isEmpty()) {
                etQuantity.setError("Quantity is required");
                return;
            }
            double qty;
            try {
                qty = Double.parseDouble(qtyStr);
                if (qty <= 0) {
                    etQuantity.setError("Quantity must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Enter a valid numerical quantity");
                return;
            }
            if (unit.isEmpty()) {
                etUnit.setError("Unit of measure is required (e.g. pcs, g, ml)");
                return;
            }

            if (isEdit) {
                existingItem.setName(name);
                existingItem.setQuantity(qty);
                existingItem.setUnit(unit);
                existingItem.setExpiryDate(expiry);
                dbHelper.updatePantryItem(existingItem);
                Toast.makeText(this, "Updated " + name, Toast.LENGTH_SHORT).show();
            } else {
                PantryItem newItem = new PantryItem(name, qty, unit, expiry);
                dbHelper.addPantryItem(newItem);
                Toast.makeText(this, "Added " + name + " to pantry", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            showPantryTab();
        });
    }

    @Override
    public void onEditClick(PantryItem item) {
        showAddEditDialog(item);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    Toast.makeText(this, item.getName() + " removed", Toast.LENGTH_SHORT).show();
                    showPantryTab();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ==========================================
    // RECIPE DETAILS SELECTION (INTENT)
    // ==========================================

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE_NAME", recipe.getName());

        StringBuilder ingDetails = new StringBuilder();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            ingDetails.append("• ").append(ri.getName())
                    .append(" : ").append(ri.getRequiredQuantity())
                    .append(" ").append(ri.getUnit()).append("\n");
        }
        intent.putExtra("RECIPE_INGREDIENTS", ingDetails.toString().trim());
        intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());

        startActivity(intent);
    }
}