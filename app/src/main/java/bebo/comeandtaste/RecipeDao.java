package bebo.comeandtaste;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import models.RecipesModel;
@Dao
public interface RecipeDao {
    @Query("Select * From RECIPES_TABLE")
   LiveData< List<RecipesModel>> loadAllRecipes();

    @Query("Select * From RECIPES_TABLE WHERE recipe_Id = :id")
   LiveData< List<RecipesModel>> loadRecipeById(String id);
    @Insert
    void insert(RecipesModel recipesModel);
    @Query("DELETE  From RECIPES_TABLE WHERE recipe_Id = :fav")
    void delete(String fav);




}
