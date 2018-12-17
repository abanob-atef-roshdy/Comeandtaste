package bebo.comeandtaste;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import models.RecipesModel;

public class MainViewModel extends AndroidViewModel {
    LiveData<List<RecipesModel>> mRecipes;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDataBase mDb = AppDataBase.getsInstance(this.getApplication());
       mRecipes = mDb.recipeDao().loadAllRecipes();
    }

    public LiveData<List<RecipesModel>> getmRecipes() {
        return mRecipes;
    }
}
