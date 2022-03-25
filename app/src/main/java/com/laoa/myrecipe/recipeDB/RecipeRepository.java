package com.laoa.myrecipe.recipeDB;

import android.app.Application;
import android.os.AsyncTask;

import com.laoa.myrecipe.models.Recipe;

import java.util.List;

public class RecipeRepository {

    private RecipeDao mRecipeDao;
    private List<Recipe> mAllRecipes;
    private RecipeDB database;
    public RecipeRepository(Application application) {
        database = RecipeDB.getDataBase(application);
        mRecipeDao = database.recipeDao();
        mAllRecipes = mRecipeDao.loadAllRecipies();
    }

    public List<Recipe> getAllRecipes() {
        return mAllRecipes;
    }

    public void insert (Recipe recipe) {
        new insertRecipeAsync(mRecipeDao).execute(recipe);
    }

    public void deleteAll() {
        if (mRecipeDao!=null)
            mRecipeDao.delete();
    }


    private static class insertRecipeAsync extends AsyncTask<Recipe, Void, Void> {
        private RecipeDao mRecipeDaoAsync;

        public insertRecipeAsync(RecipeDao dao) {
            mRecipeDaoAsync = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            mRecipeDaoAsync.insertRecipe(recipes);
            return null;
        }
    }

}
