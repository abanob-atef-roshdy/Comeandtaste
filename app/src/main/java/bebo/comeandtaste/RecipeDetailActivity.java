package bebo.comeandtaste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import fragments.RecipeDetailFragment;
import models.RecipesModel;

public class RecipeDetailActivity extends AppCompatActivity {

     RecipesModel mrecipesModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        mrecipesModel = (RecipesModel) Parcels.unwrap(getIntent().getParcelableExtra("recipe"));
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setRcipeModel(mrecipesModel);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.recipe_fragment,recipeDetailFragment).commit();
        }
    }
}
