package com.laoa.myrecipe.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.laoa.myrecipe.recipeDB.RecipeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The main class for handling the logic behind managing the recipes in the
 * app. This class provides a way for the controllers to update their UI when
 * different changes happen to the recipes, e.g. recipes removed or edited.
 * This class also has the job of updating the database accordingly when
 * different operations occur, e.g. adding new recipes.
 *
 * extends from AndroidViewModel which enables the Repository to be initialized.
 * */




public class RecipeManager extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private Map<String, List<Recipe>> mapOfTypeOfRecipes;

    private MutableLiveData<List<String>> categories;
    private MutableLiveData<Boolean> addedEvent;

    private MutableLiveData<UUID> recipeModified;

    private MutableLiveData<Boolean> removedRecipe;
    private boolean hasLoadedDB = false;

    private SavedStateHandle mSavedStateHandle;

    public RecipeManager(Application application, SavedStateHandle savedStateHandle) {
            super(application);
            mSavedStateHandle = savedStateHandle;
            mRecipeRepository = new RecipeRepository(application);
            mapOfTypeOfRecipes = new HashMap<>();
            categories = new MutableLiveData<>();
            categories.setValue(new ArrayList<>());
            addedEvent = new MutableLiveData<>();
            recipeModified = new MutableLiveData<>();
            removedRecipe = new MutableLiveData<>();
            getState(savedStateHandle);

        }

    /**
     * Retrieves the state when a configuration of different sort occurred, e.g. rotate phone.
     * */
    private void getState(SavedStateHandle savedStateHandle) {
        if (savedStateHandle!=null) {
            if (savedStateHandle.get("recipes") != null) {
                for (Recipe recipe: (ArrayList<Recipe>) savedStateHandle.get("recipes")) {
                    addRecipeNoUpdate(recipe);
                }
            }

            if (savedStateHandle.get("added_event") != null)
                addedEvent.setValue(savedStateHandle.get("added_event"));
            if (savedStateHandle.get("modified_recipe") != null)
                recipeModified.setValue(savedStateHandle.get("modified_recipe"));
            if (savedStateHandle.get("removed_recipe") != null)
                removedRecipe.setValue(savedStateHandle.get("removed_recipe"));
            if (savedStateHandle.get("has_loaded") != null)
                hasLoadedDB = savedStateHandle.get("has_loaded");
            if (savedStateHandle.get("categories") != null) {
                categories.setValue(savedStateHandle.get("categories"));
            }
        }
    }

    /**
     * Sets the state before a configuration occurs. This should be called inside the fragment/activity's
     * onSaveInstanceState.
     * */
    public void setState() {
        if (mSavedStateHandle != null) {
            ArrayList<Recipe> temp = new ArrayList<>();
            ArrayList<String> categoriesHolder = new ArrayList<>();
            for (List<Recipe> recipes : mapOfTypeOfRecipes.values()) {
                for (Recipe recipe: recipes) {
                    temp.add(recipe);
                }
            }
            for (String category: categories.getValue()) {
                categoriesHolder.add(category);
            }

            mSavedStateHandle.set("recipes", temp);
            mSavedStateHandle.set("added_event", addedEvent.getValue());
            mSavedStateHandle.set("modified_recipe", recipeModified.getValue());
            mSavedStateHandle.set("removed_recipe", removedRecipe.getValue());
            mSavedStateHandle.set("has_loaded", hasLoadedDB);
            mSavedStateHandle.set("categories", categoriesHolder);
        }

    }



    public LiveData<List<Recipe>> loadDB() {
        return mRecipeRepository.getAllRecipes();
    }

    public void setAddedEvent(Boolean add) {
        addedEvent.postValue(add);
    }

    private void setHasModifiedRecipe(UUID id) {
        recipeModified.setValue(id);
    }

    public MutableLiveData<UUID> getRecipeModified() {
        return recipeModified;
    }

    public MutableLiveData<Boolean> getAddedEvent() {return addedEvent;}
    public MutableLiveData<List<String>> getCategoriesMutableLiveData() {
        return categories;
    }

    public void addCategory(String label) {
        if (!mapOfTypeOfRecipes.containsKey(label))
            mapOfTypeOfRecipes.putIfAbsent(label, new ArrayList<>());
    }

    /**
     * When a new category is added, update the categories list and
     * notify listeners. Also updates the Map and creates an new entry
     * of the new category.
     * */
    public void setCategory(String category) {
        categories.getValue().add(category);
        categories.setValue(categories.getValue());
        addCategory(category);
    }

    /**
     * When an already created recipe is modified, this method is called.
     * */
    public void modifiedRecipe(Recipe recipe, String oldCategory) {

        // No change in hashmap, only notify the listeners that uses particular recipe
        // if recipe has changed otherwise.
        if (recipe.getTypeOfFood().equals(oldCategory)) {
            recipeModified.setValue(recipe.getUuid());
        }
        // Recipe has changed category (and maybe other fields). Has to update Hashmap.
        else {
            for (String category: mapOfTypeOfRecipes.keySet()) {
                if (category.equals(oldCategory))
                    mapOfTypeOfRecipes.get(category).remove(recipe);
            }
            // The case when the changed category already exists
            if (mapOfTypeOfRecipes.get(recipe.getTypeOfFood()) != null) {
                mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
            }
            // If the category does not exist, create the category and add the recipe.
            else {
                setCategory(recipe.getTypeOfFood());
                mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
            }
            recipeModified.setValue(recipe.getUuid());
        }
        //Notify database about recipe changes.
        mRecipeRepository.update(recipe);
    }

    /**
     * Returns a list of all recipes that is marked as favorites.
     * */
    public List<Recipe> getFavoriteRecipes() {
        List<Recipe> favoriteRecipes = new ArrayList<>();
        for (List<Recipe> recipes: mapOfTypeOfRecipes.values()) {
            for (Recipe recipe : recipes) {
                if (recipe.isFavourite())
                    favoriteRecipes.add(recipe);
            }
        }
        return favoriteRecipes;
    }

    public MutableLiveData<Boolean> getRemovedRecipe() {
        return removedRecipe;
    }

    /**
     * @param recipe
     * Removes the specified recipe and also removes from the database.
     * */
    public boolean removeRecipe(Recipe recipe) {
        if (mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).remove(recipe)) {
            removedRecipe.setValue(true);
            mRecipeRepository.remove(recipe);
            return true;
        }
        return false;
    }

    /**
     * This should only be called when app starts.
     * Uploads the recipes from the database to the Map so that each recipe appears
     * In the right category.
     * */
    public void addRecipeNoUpdate(Recipe recipe) {
        if (mapOfTypeOfRecipes.containsKey(recipe.getTypeOfFood())) {
            boolean alreadyExist = mapOfTypeOfRecipes.
                    get(recipe.getTypeOfFood()).
                    stream().
                    anyMatch(recipe1 -> recipe1.getUuid().compareTo(recipe.getUuid()) == 0);
            if (!alreadyExist) {
                mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
            }
        }
        else {
            List<Recipe> recipes = new ArrayList<>();
            recipes.add(recipe);
            mapOfTypeOfRecipes.putIfAbsent(recipe.getTypeOfFood(), recipes);
            if (!categories.getValue().contains(recipe.getTypeOfFood())) {
                categories.getValue().add(recipe.getTypeOfFood());
                //categories.postValue(categories.getValue());
            }
        }
        setAddedEvent(true);
    }

    /**
     * @param recipe
     * Adds a new recipe to the map and also adds the recipe to the database.
     * */
    public void addRecipe(Recipe recipe) {

        if (mapOfTypeOfRecipes.containsKey(recipe.getTypeOfFood())) {
            boolean alreadyExist = mapOfTypeOfRecipes.
                    get(recipe.getTypeOfFood()).
                    stream().
                    anyMatch(recipe1 -> recipe1.getUuid().compareTo(recipe.getUuid()) == 0);

            if (!alreadyExist) {
                mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
                mRecipeRepository.insert(recipe);
            }
        }
        else {
            List<Recipe> recepies = new ArrayList<>();
            recepies.add(recipe);
            mapOfTypeOfRecipes.putIfAbsent(recipe.getTypeOfFood(), recepies);

            if (!categories.getValue().contains(recipe.getTypeOfFood())) {
                categories.getValue().add(recipe.getTypeOfFood());
                categories.postValue(categories.getValue());
            }
            mRecipeRepository.insert(recipe);
        }
    }

    /**
     * @param position
     * Get category at specified position,
     * @return Returns the category name at specified position, otherwise null.
     * */
    public String getCategory(int position) {
        if (position < categories.getValue().size())
            return categories.getValue().get(position);
        return null;
    }

    public List<String> getCategories() {
        return categories.getValue();
    }

    public List<Recipe> getTypeOfRecipies(String typeOfFood) {

        if (mapOfTypeOfRecipes.containsKey(typeOfFood)) {
            return mapOfTypeOfRecipes.get(typeOfFood);
        }
        return null;
    }

    public Map<String, List<Recipe>> getRecipiesCategory() {
        return mapOfTypeOfRecipes;
    }


    /**
     * @param category
     * @param id
     *  A faster way of attaining a specific recipe.
     *  Should only be used when knowing for certain that
     *  the category has not been altered.
     * */
    public Recipe getRecipe(String category, UUID id) {
        if (mapOfTypeOfRecipes.containsKey(category)) {
            for (Recipe recipe: mapOfTypeOfRecipes.get(category)) {
                if (recipe.getUuid().compareTo(id) == 0)
                    return recipe;
            }
        }
        return null;
    }

    /**
     * Slower then the other alternative with (string,uuid) as parameter
     * However, is safe to use even if the category of the recipe has been modified
     * */
    public Recipe getRecipe(UUID id) {
        for (List<Recipe> recipes: mapOfTypeOfRecipes.values()) {
            for (Recipe recipe : recipes) {
                if (recipe.getUuid().compareTo(id) == 0)
                    return recipe;
            }
        }
        return null;
    }

    public boolean hasLoadedDB() {
        return hasLoadedDB;
    }

    public void setHasLoadedDB(boolean hasLoadedDB) {
        this.hasLoadedDB = hasLoadedDB;
    }


}
