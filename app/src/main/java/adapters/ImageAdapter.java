package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bebo.comeandtaste.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import models.RecipesModel;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterviewHolder> {
  private   List<RecipesModel> recipesModelList;

    private Context context;
     private final ImageClickHandler imageClickHandler;
    public interface ImageClickHandler{
        public void onImageClick(RecipesModel recipesModel);
    }

    public ImageAdapter(List<RecipesModel> recipesModelList, Context context , ImageClickHandler imageClickHandler) {
        this.recipesModelList = recipesModelList;
        this.context = context;
        this.imageClickHandler = imageClickHandler;
    }

    @NonNull
    @Override
    public ImageAdapterviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_adapter, viewGroup, false);
        return new ImageAdapterviewHolder(view);
    }
    public void updateData(List<RecipesModel> recipes) {

        recipesModelList.addAll(recipes);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterviewHolder imageAdapterviewHolder, int i) {
        RecipesModel recipesModel = recipesModelList.get(i);
        Picasso.get().load(recipesModel.getImgUrl()).into(imageAdapterviewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return recipesModelList.size();
    }

    public class ImageAdapterviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.image_iv)
    ImageView imageView;
    public ImageAdapterviewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(this);
    }

        @Override
        public void onClick(View view) {
        int position = getAdapterPosition();
        RecipesModel recipesModel = recipesModelList.get(position);
        imageClickHandler.onImageClick(recipesModel);

        }
    }
}
