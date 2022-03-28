package com.laoa.myrecipe.models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laoa.myrecipe.recipeDB.RecipeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecipeManager extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private List<Recipe> listOfRecipes;
    private Map<String, List<Recipe>> mapOfTypeOfRecipes;

    private LiveData<List<Recipe>> mListLiveDataRecipes;
    private MutableLiveData<List<String>> categories;
    private MutableLiveData<Boolean> addedEvent;
    private MutableLiveData<UUID> recipeModified;

    private MutableLiveData<Boolean> removedRecipe;

    public RecipeManager(Application application) {
        super(application);
        mRecipeRepository = new RecipeRepository(application);
        listOfRecipes = new ArrayList<>();
        mapOfTypeOfRecipes = new HashMap<>();
        categories = new MutableLiveData<>();
        categories.setValue(new ArrayList<>());
        mListLiveDataRecipes = new MutableLiveData<>();
        addedEvent = new MutableLiveData<>();
        recipeModified = new MutableLiveData<>();
        removedRecipe = new MutableLiveData<>();
    }



    public void getFirstRecipeOut() {
        List<Recipe> temp = mRecipeRepository.getAllRecipes() != null? mRecipeRepository.getAllRecipes() : null;
        System.out.println("size of DB" + temp.size());
        for (Recipe recipe: temp) {
            System.out.println("Loading from DB"+ recipe.getRecipeName());
        }

    }

    public void setAddedEvent(Boolean add) {
        addedEvent.setValue(add);
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

    public void setCategory(String category) {
        categories.getValue().add(category);
        categories.setValue(categories.getValue());
        mapOfTypeOfRecipes.putIfAbsent(category, new ArrayList<>());
    }

    public void deleteAllDB() {
        if (mRecipeRepository!=null)
            mRecipeRepository.deleteAll();
    }

    public void modifiedRecipe(Recipe recipe, String oldCategory) {

        // No change in hashmap, only notify the listeners that uses particular recipe
        // if recipe has changed otherwise.
        if (recipe.getTypeOfFood().equals(oldCategory)) {
            recipeModified.setValue(recipe.getUuid());
            return;
        }
        // Recipe has changed category (and maybe other fields). Has to update Hashmap.
        else {
            for (String category: mapOfTypeOfRecipes.keySet()) {
                if (category.equals(oldCategory))
                    mapOfTypeOfRecipes.get(category).remove(recipe);
            }
            mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
            recipeModified.setValue(recipe.getUuid());
        }

    }

    public MutableLiveData<Boolean> getRemovedRecipe() {
        return removedRecipe;
    }

    public boolean removeRecipe(Recipe recipe) {
        if (mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).remove(recipe)) {
            removedRecipe.setValue(true);
            return true;
        }
        return false;
    }

    public void addRecipe(Recipe recipe) {

        if (mapOfTypeOfRecipes.containsKey(recipe.getTypeOfFood())) {
            mapOfTypeOfRecipes.get(recipe.getTypeOfFood()).add(recipe);
        }
        else {
            List<Recipe> recepies = new ArrayList<>();
            recepies.add(recipe);
            mapOfTypeOfRecipes.putIfAbsent(recipe.getTypeOfFood(), recepies);
            categories.getValue().add(recipe.getTypeOfFood());
            categories.setValue(categories.getValue());
            mRecipeRepository.insert(recipe);
        }
    }

    public String getCategory(int position) {
        if (position < categories.getValue().size())
            return categories.getValue().get(position);
        return null;
    }

    public List<String> getCategories() {
        return categories.getValue();
    }

    public void addCategory(String label) {
        if (!mapOfTypeOfRecipes.containsKey(label))
            mapOfTypeOfRecipes.putIfAbsent(label, new ArrayList<>());
    }


    public List<Recipe> getListOfRecipes() {
        return listOfRecipes;
    }

    //TODO: if data changes, does not responde in map.
    public List<Recipe> getTypeOfRecipies(String typeOfFood) {
        List<Recipe> specificTypeOfRecipes = new ArrayList<>();

        if (mapOfTypeOfRecipes.containsKey(typeOfFood)) {
            return mapOfTypeOfRecipes.get(typeOfFood);
        }

        return null;
    }

    public Map<String, List<Recipe>> getRecipiesCategory() {
        return mapOfTypeOfRecipes;
    }


    /**
     *
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
     * Slower then the other alternative with (string,uuid) as alternative
     * However, is safe to use if the category of the recipe has been modified
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

}
