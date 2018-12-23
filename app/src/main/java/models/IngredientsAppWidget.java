package models;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import bebo.comeandtaste.IngredientsService;
import bebo.comeandtaste.MainActivity;
import bebo.comeandtaste.R;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsAppWidget extends AppWidgetProvider {
   static String mwidgetText ;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_app_widget);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_text,pendingIntent1);
        Intent getIngredientsIntent = new Intent(context, IngredientsService.class);
        getIngredientsIntent.setAction("getIngredients");
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, getIngredientsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imgv,pendingIntent);
        views.setTextViewText(R.id.appwidget_text, getMwidgetText());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static String getMwidgetText() {
        return mwidgetText;
    }

    public static void setMwidgetText(String mwidgetText) {
        IngredientsAppWidget.mwidgetText = mwidgetText;
    }
}

