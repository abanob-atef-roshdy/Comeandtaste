package bebo.comeandtaste;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import models.IngredientsAppWidget;

public class IngredientsService extends IntentService {
    public IngredientsService(String name) {
        super(name);
    }

    public IngredientsService() {
        super("a");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String action = intent.getAction();
            if(action.equals("getIngredients")){

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String widgetText = sharedPreferences.getString("s","s");
                IngredientsAppWidget.setMwidgetText(widgetText);

            }
        }

    }
}
