package bebo.comeandtaste;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import models.RecipesModel;

@Database(entities = {RecipesModel.class},version = 1,exportSchema = false)
public abstract class  AppDataBase extends RoomDatabase {
    private static final Object lock = new Object();
    private static final String dataBaseName = "recipeDataBase";
    private static AppDataBase sInstance;

    public static AppDataBase getsInstance(Context context){

        if(sInstance == null){

            synchronized (lock){

                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,dataBaseName)
                        .allowMainThreadQueries()
                        .build();
            }

        }

        return sInstance;

    }
    public abstract RecipeDao recipeDao();
}
