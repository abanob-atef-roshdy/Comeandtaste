package models;

import org.parceler.Parcel;

@Parcel
public class RecipesModel {
    String imgUrl , title , rank , sourceUrl ;
    String recipeId;

    public RecipesModel(String imgUrl, String title, String rank, String recipeId, String sourceUrl) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.rank = rank;
        this.recipeId = recipeId;
        this.sourceUrl = sourceUrl;
    }

    public RecipesModel() {
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getRank() {
        return rank;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }
}
