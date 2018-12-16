package bebo.comeandtaste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import adapters.ImageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import models.RecipesModel;

public class MainActivity extends AppCompatActivity implements ImageAdapter.ImageClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    List<RecipesModel> recipesModelList;
    @BindView(R.id.main_rec)
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    GridLayoutManager gridLayoutManager;
    String mIngredient;
    int pageNumber;
    AppDataBase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDb = AppDataBase.getsInstance(getApplicationContext());
        recipesModelList = new ArrayList<>();
         gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        Context context = getApplicationContext();
        imageAdapter = new ImageAdapter(recipesModelList,context,MainActivity.this);

        recyclerView.setAdapter(imageAdapter);
        loadRecipesData(sharedPreferences);
      //  retFromDb();


     /*   AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
        MobileAds.initialize(this,"ca-app-pub-3954911785359391~1804332260");
       */

    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void loadRecipesData(SharedPreferences sharedPreferences){
    recipesModelList.clear();
    RequestQueue queue = Volley.newRequestQueue(this);

    StringRequest stringRequest = new StringRequest(Request.Method.GET, retUrl(sharedPreferences),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    try {
                        //List<RecipesModel> recipesModelList = new ArrayList<>();
                        JSONObject all = new JSONObject(response);
                        JSONArray recipes = all.getJSONArray("recipes");
                        for(int i = 0;i<recipes.length();i++){
                            JSONObject recipe = recipes.getJSONObject(i);
                            String imgUrl = recipe.getString("image_url");
                            String title = recipe.getString("title");
                            String source = recipe.getString("source_url");
                            String rank = recipe.getString("social_rank");
                            String recId = recipe.getString("recipe_id");
                             RecipesModel recipesModel = new RecipesModel(imgUrl,title,rank,recId,source);
                             recipesModelList.add(recipesModel);

                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    imageAdapter.updateData(recipesModelList);

                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    queue.add(stringRequest);

}
public String retUrl(SharedPreferences sharedPreferences){

    String sort = sharedPreferences.getString(getString(R.string.listKey),getString(R.string.listDefValue));
    pageNumber = Integer.parseInt(sharedPreferences.getString(getString(R.string.ed_page_key),getString(R.string.ed_page_defValue)));
    if(sort.equals("f")){

        retFromDb();
        return "";
    }
  else if(sort.equals("r")) {
        mIngredient = sharedPreferences.getString(getString(R.string.edit_key), getString(R.string.edit_defValue));
    }
    else if(sort.equals("t")) {
        mIngredient = "" ;
    }
    String url = "https://www.food2fork.com/api/search?key=&sort="+sort+"&q="+mIngredient+"&page="+pageNumber;

    return url;

}


    @Override
    public void onImageClick(RecipesModel recipesModel) {
        Intent intent = new Intent(this,RecipeDetailActivity.class);
        intent.putExtra("recipe", Parcels.wrap(recipesModel));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(getString(R.string.listKey), getString(R.string.listDefValue));
        if (sort.equals("f")) {
            retFromDb();
        } else {
            loadRecipesData(sharedPreferences);
        }
    }
    public void retFromDb(){
       List<RecipesModel> recipesModelList = mDb.recipeDao().loadAllRecipes();
      //  imageAdapter = new ImageAdapter(recipesModelList,getApplicationContext(),MainActivity.this);
        imageAdapter.updateData(recipesModelList);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(getString(R.string.listKey), getString(R.string.listDefValue));
        if (sort.equals("f")) {
            retFromDb();
        } else {
            loadRecipesData(sharedPreferences);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //loadRecipesData(sharedPreferences);
    }
}
