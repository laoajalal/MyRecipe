package com.laoa.myrecipe.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "recipe_table")
public class Recipe {

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

    public String getTypeOfFood() {
        return typeOfFood;
    }

    public void setTypeOfFood(String typeOfFood) {
        this.typeOfFood = typeOfFood;
    }



    private UUID uuid;

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

    public void addFoodImagePath(String foodImages) {
        this.foodImagePaths.add(foodImages);
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

    public void addIngredients(String ingredients) {
        this.ingredients.add(ingredients);
    }

    public List<String> getRecipeSteps() {
        return recipeSteps;
    }

    public void addRecipeStep(String recipeSteps) {
        this.recipeSteps.add(recipeSteps);
    }

}
