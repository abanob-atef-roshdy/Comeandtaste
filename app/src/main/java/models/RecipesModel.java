package models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "recipes_table")
public class RecipesModel {
    @ColumnInfo(name = "img_db")
    private String imgUrl;
    @ColumnInfo(name = "title_db")
    private String title;
    @ColumnInfo(name = "rank_db")
    private String rank;
    @ColumnInfo(name = "source_db")
    private String sourceUrl ;
    @ColumnInfo(name = "recipe_Id")
    private String recipeId;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_db")
    public int id;
    @Ignore
    public RecipesModel(String imgUrl, String title, String rank, String recipeId, String sourceUrl) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.rank = rank;
        this.recipeId = recipeId;
        this.sourceUrl = sourceUrl;
    }

    public RecipesModel( int id,String imgUrl, String title, String rank, String sourceUrl, String recipeId) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.title = title;
        this.rank = rank;
        this.sourceUrl = sourceUrl;
        this.recipeId = recipeId;

    }

  public   RecipesModel() {
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
