package com.laoa.myrecipe.models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The class contains information to model a recipe.
 * This class is an entity inside the Room-database
 * */
@Entity(tableName = "recipe_table")
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "recipe_name")
    private String recipeName;

    @ColumnInfo(name = "prep_time")
    private int preperationTime;

    @ColumnInfo(name = "cook_time")
    private int cookTime;
    @ColumnInfo(name = "type_of_food")
    private String typeOfFood;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_paths")
    private List<String> foodImagePaths;

    @ColumnInfo(name = "ingredients")
    private List<String> ingredients;

    @ColumnInfo(name = "recipe_steps")
    private List<String> recipeSteps;

    private boolean isFavourite;
    private UUID uuid;

    protected Recipe(Parcel in) {
        id = in.readInt();
        recipeName = in.readString();
        preperationTime = in.readInt();
        cookTime = in.readInt();
        typeOfFood = in.readString();
        description = in.readString();
        foodImagePaths = in.createStringArrayList();
        ingredients = in.createStringArrayList();
        recipeSteps = in.createStringArrayList();
        isFavourite = in.readByte() != 0;
        uuid = (UUID) in.readSerializable();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getTypeOfFood() {
        return typeOfFood;
    }

    public void setTypeOfFood(String typeOfFood) {
        this.typeOfFood = typeOfFood;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }



    public void setId(int id) {
        this.id = id;
    }

    public Recipe() {
        foodImagePaths = new ArrayList<>();
        ingredients = new ArrayList<>();
        recipeSteps = new ArrayList<>();
        uuid = UUID.randomUUID();
    }


    @Ignore
    public Recipe(String recipeName, String typeOfFood, String description, int preperationTime, int cookTime) {
        this.recipeName = recipeName;
        this.preperationTime = preperationTime;
        this.cookTime = cookTime;
        this.typeOfFood = typeOfFood;
        this.description = description;
        foodImagePaths = new ArrayList<>();
        ingredients = new ArrayList<>();
        recipeSteps = new ArrayList<>();
        uuid = UUID.randomUUID();

    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }

    public String getRecipeName() { return recipeName; }

    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }

    public int getPreperationTime() {
        return preperationTime;
    }

    public void setPreperationTime(int preperationTime) {
        this.preperationTime = preperationTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getId() {
        return id;
    }

    public List<String> getFoodImagePaths() {
        return foodImagePaths;
    }

    public void setFoodImagePaths(List<String> foodImagePaths) {
        this.foodImagePaths = foodImagePaths;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setRecipeSteps(List<String> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }


    public List<String> getRecipeSteps() {
        return recipeSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(recipeName);
        parcel.writeInt(preperationTime);
        parcel.writeInt(cookTime);
        parcel.writeString(typeOfFood);
        parcel.writeString(description);
        parcel.writeStringList(foodImagePaths);
        parcel.writeStringList(ingredients);
        parcel.writeStringList(recipeSteps);
        parcel.writeByte((byte) (isFavourite ? 1 : 0));
        parcel.writeSerializable(uuid);
    }
}
