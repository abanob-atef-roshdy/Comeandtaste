package bebo.comeandtaste;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import adapters.ImageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import models.RecipesModel;

public class MainActivity extends AppCompatActivity {

    List<RecipesModel> recipesModelList;
    @BindView(R.id.main_rec)
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
       // recipesModelList = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        loadRecipesData();
     /*   AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
        MobileAds.initialize(this,"ca-app-pub-3954911785359391~1804332260");
       */

    }
public void loadRecipesData(){

    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://www.food2fork.com/api/search?key=4d9d538f0f75cdaeb40dfc4fb383e75c";
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    try {
                        List<RecipesModel> recipesModelList = new ArrayList<>();
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

                        Context context = getApplicationContext();
                        imageAdapter = new ImageAdapter(recipesModelList,context);
                        recyclerView.setAdapter(imageAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    queue.add(stringRequest);

}


}
