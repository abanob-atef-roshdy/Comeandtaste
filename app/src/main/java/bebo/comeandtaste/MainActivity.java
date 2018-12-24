package bebo.comeandtaste;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.ImageView;
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

import java.io.Serializable;
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
    int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDb = AppDataBase.getsInstance(getApplicationContext());
        recipesModelList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        Context context = getApplicationContext();
        imageAdapter = new ImageAdapter(recipesModelList, context, MainActivity.this);

        recyclerView.setAdapter(imageAdapter);
        if (savedInstanceState != null) {
            flag = savedInstanceState.getInt("fav");
            if (flag == 1) {
                retFromDb();
            } else {
                loadRecipesData(sharedPreferences);
            }
        }


    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void loadRecipesData(SharedPreferences sharedPreferences) {
        recipesModelList.clear();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, retUrl(sharedPreferences),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            // List<RecipesModel> recipesModelList = new ArrayList<>();
                            if (response.equals("{\"count\": 0, \"recipes\": []}")) {
                                Toast.makeText(MainActivity.this, getString(R.string.message), Toast.LENGTH_LONG).show();
                            } else {
                                JSONObject all = new JSONObject(response);
                                JSONArray recipes = all.getJSONArray("recipes");
                                for (int i = 0; i < recipes.length(); i++) {
                                    JSONObject recipe = recipes.getJSONObject(i);
                                    String imgUrl = recipe.getString("image_url");
                                    String title = recipe.getString("title");
                                    String source = recipe.getString("source_url");
                                    String rank = recipe.getString("social_rank");
                                    String recId = recipe.getString("recipe_id");
                                    RecipesModel recipesModel = new RecipesModel(imgUrl, title, rank, recId, source);
                                    recipesModelList.add(recipesModel);

                                }
                            }

                            imageAdapter.updateData(recipesModelList);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sort = sharedPreferences1.getString(getString(R.string.listKey), getString(R.string.listDefValue));
                if (!sort.equals("f")) {
                    Toast.makeText(MainActivity.this, getString(R.string.network_message), Toast.LENGTH_SHORT).show();
                }

            }
        });
        queue.add(stringRequest);

    }

    public String retUrl(SharedPreferences sharedPreferences) {

        String sort = sharedPreferences.getString(getString(R.string.listKey), getString(R.string.listDefValue));
        pageNumber = Integer.parseInt(sharedPreferences.getString(getString(R.string.ed_page_key), getString(R.string.ed_page_defValue)));
        if (sort.equals("f")) {
            flag = 1;
            retFromDb();
            return "";
        } else if (sort.equals("r")) {
            flag = 0;
            mIngredient = sharedPreferences.getString(getString(R.string.edit_key), getString(R.string.edit_defValue));
        } else if (sort.equals("t")) {
            flag = 0;
            mIngredient = "";
        }
        String url = "https://www.food2fork.com/api/search?key=" +
                "666b3d1a587e38dc488df5ca0345f233&sort=" + sort + "&q=" + mIngredient + "&page=" + pageNumber;

        return url;

    }


    @Override
    public void onImageClick(RecipesModel recipesModel) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", Parcels.wrap(recipesModel));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("fav", flag);
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
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

    public void retFromDb() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getmRecipes().observe(this, new Observer<List<RecipesModel>>() {
            @Override
            public void onChanged(@Nullable List<RecipesModel> recipesModels) {
                if (recipesModels.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.favourite_message), Toast.LENGTH_SHORT).show();
                } else {
                    imageAdapter.updateData(recipesModels);
                }

            }
        });


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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(getString(R.string.listKey), getString(R.string.listDefValue));
        if (sort.equals("f")) {
            retFromDb();
        } else {
            loadRecipesData(sharedPreferences);
        }

    }
}
