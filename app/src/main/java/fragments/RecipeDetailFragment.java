package fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bebo.comeandtaste.AppDataBase;
import bebo.comeandtaste.AppExecutor;
import bebo.comeandtaste.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import models.RecipesModel;

public class RecipeDetailFragment extends Fragment {
    @BindView(R.id.title_tv)
    TextView mTitle;
    @BindView(R.id.image_iv1)
    ImageView mImageView;
    @BindView(R.id.rank_tv)
    TextView mRank;
    @BindView(R.id.ingredients_tv)
    TextView mIngredients;
    @BindView(R.id.button)
    Button mSourceLauncher;
    @BindView(R.id.share_fab)
    FloatingActionButton fabButton;
    RecipesModel recipesModel;
    @BindView(R.id.star_img)
    ImageView mImg;
    AppDataBase mDb;
    @BindView(R.id.adView)
    AdView mAdView;
    String url;
    String id1;
    String idStar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        populatUi();
        getIngredients();
        mDb = AppDataBase.getsInstance(getContext());
        starColor();
        mSourceLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSource();
            }
        });
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeOrDelete();
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
        return view;
    }

    public void populatUi() {
        if (recipesModel != null) {
            Picasso.get().load(recipesModel.getImgUrl()).into(mImageView);
            mTitle.setText(recipesModel.getTitle());
            mRank.setText(recipesModel.getRank() + "/100");

        }
    }

    public void setRcipeModel(RecipesModel recipesModel) {
        this.recipesModel = recipesModel;
    }

    public void getIngredients() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        if (recipesModel != null) {
            url = "https://www.food2fork.com/api/get?key=&rId=" + recipesModel.getRecipeId();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject all = new JSONObject(response);
                            JSONObject firstObj = all.getJSONObject("recipe");
                            JSONArray ingredients = firstObj.getJSONArray("ingredients");

                            for (int i = 0; i < ingredients.length(); i++) {
                                //  JSONObject ingredObject = ingredients.get(i);
                                String ingredVar = ingredients.getString(i);
                                mIngredients.append(ingredVar + "\n");

                            }
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("s", mIngredients.getText().toString());
                            editor.commit();

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


    public void goToSource() {
        Uri webpage = Uri.parse(recipesModel.getSourceUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recipesModel.getSourceUrl());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    public void save() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.recipeDao().insert(recipesModel);
            }
        });

        
    }

    public void delete() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.recipeDao().delete(recipesModel.getRecipeId());

            }
        });

    }

    public void storeOrDelete() {
        if (recipesModel != null) {
            id1 = recipesModel.getRecipeId();
        }
        final LiveData<List<RecipesModel>> recipesModelList = mDb.recipeDao().loadRecipeById(id1);
        recipesModelList.observe(this, new Observer<List<RecipesModel>>() {
            @Override
            public void onChanged(@Nullable List<RecipesModel> recipesModels) {

                if (recipesModels.isEmpty()) {
                    save();
                    mImg.setImageResource(R.drawable.ystaricon);
                    Toast.makeText(getActivity(), "movie was successfully saved to the favourites list", Toast.LENGTH_SHORT).show();
                } else {
                    delete();
                    mImg.setImageResource(R.drawable.staricon);
                    Toast.makeText(getActivity(), "movie was successfully deleted from the favourites list", Toast.LENGTH_SHORT).show();
                }
                recipesModelList.removeObserver(this);
            }
        });

    }

    public void starColor() {
        if (recipesModel != null) {
            idStar = recipesModel.getRecipeId();
        }
        final LiveData<List<RecipesModel>> recipesModelList = mDb.recipeDao().loadRecipeById(idStar);
        recipesModelList.observe(this, new Observer<List<RecipesModel>>() {
            @Override
            public void onChanged(@Nullable List<RecipesModel> recipesModels) {

                if (recipesModels.isEmpty()) {

                    mImg.setImageResource(R.drawable.staricon);
                } else {

                    mImg.setImageResource(R.drawable.ystaricon);
                }
                recipesModelList.removeObserver(this);
            }

        });

    }
}
