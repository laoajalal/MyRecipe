package com.laoa.myrecipe.recipeDB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.laoa.myrecipe.models.Recipe;

import java.util.List;

/**
 * A repository class used to make it convenient for the RecipeManager
 * to have a clean way of loading the recipes from the database, but also
 * provide operations such as adding, removing and updating the recipes inside
 * the database.
 * All operations are performed inside a background thread to avoid
 * UI-lags
 * */
public class RecipeRepository {

    private RecipeDao mRecipeDao;
    private RecipeDB database;

    public RecipeRepository(Application application) {
        database = RecipeDB.getDataBase(application);
        mRecipeDao = database.recipeDao();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeDao.loadAllRecipies();
    }

    public void insert (Recipe recipe) {
        new insertRecipeAsync(mRecipeDao).execute(recipe);
    }

    public void remove(Recipe recipe) {
        new removeRecipeAsync(mRecipeDao).execute(recipe);
    }

    public void update(Recipe recipe) {
        new updateRecipeAsync(mRecipeDao).execute(recipe);
    }

    private static class updateRecipeAsync extends AsyncTask<Recipe, Void, Void> {
        private RecipeDao mRecipeDaoAsync;

        public updateRecipeAsync(RecipeDao dao) {
            mRecipeDaoAsync = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            mRecipeDaoAsync.update(recipes);
            return null;
        }
    }

    private static class insertRecipeAsync extends AsyncTask<Recipe, Void, Void> {
        private RecipeDao mRecipeDaoAsync;

        public insertRecipeAsync(RecipeDao dao) {
            mRecipeDaoAsync = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            recipes[0].setId(0);
            mRecipeDaoAsync.insertRecipe(recipes);
            return null;
        }
    }


    private static class removeRecipeAsync extends AsyncTask<Recipe, Void, Void> {
        private RecipeDao mRecipeDaoAsync;

        public removeRecipeAsync(RecipeDao dao) {
            mRecipeDaoAsync = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            mRecipeDaoAsync.delete(recipes);
            return null;
        }
    }

}
