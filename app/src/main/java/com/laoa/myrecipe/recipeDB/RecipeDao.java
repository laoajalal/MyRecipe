package com.laoa.myrecipe.recipeDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.laoa.myrecipe.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe_table ORDER BY id ASC")
    List<Recipe> loadAllRecipies();

    @Insert
    void insertRecipe(Recipe... recipe);

    @Delete
    void delete(Recipe... recipe);

    @Query("DELETE FROM recipe_table")
    void delete();

}
