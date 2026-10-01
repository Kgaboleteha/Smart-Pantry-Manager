package za.ac.richfield.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.tv_detail_recipe_name);
        TextView tvIngredients = findViewById(R.id.tv_detail_ingredients);
        TextView tvInstructions = findViewById(R.id.tv_detail_instructions);

        // Receive parameters passed via Intent
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeIngredients = getIntent().getStringExtra("RECIPE_INGREDIENTS");
        String recipeInstructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");

        if (recipeName != null) tvName.setText(recipeName);
        if (recipeIngredients != null) tvIngredients.setText(recipeIngredients);
        if (recipeInstructions != null) tvInstructions.setText(recipeInstructions);
    }
}