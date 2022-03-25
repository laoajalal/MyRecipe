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

    public RecipeManager(Application application) {
        super(application);
        listOfRecipes = new ArrayList<>();
        mapOfTypeOfRecipes = new HashMap<>();
        categories = new MutableLiveData<>();
        categories.setValue(new ArrayList<>());
        mListLiveDataRecipes = new MutableLiveData<>();
        mRecipeRepository = new RecipeRepository(application);
        addedEvent = new MutableLiveData<>();

    }

    public void getFirstRecipeOut() {
        List<Recipe> temp = mRecipeRepository.getAllRecipes();

        for (Recipe recipe:temp) {
            if (recipe.getFoodImagePaths().size()>0)
                System.out.println("Loading from DB"+ recipe.getFoodImagePaths().get(0));

        }

    }

    public void setAddedEvent(Boolean add) {
        addedEvent.setValue(add);
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

        for (Recipe recipe:listOfRecipes) {
            if (recipe.getTypeOfFood().equals(typeOfFood)) {
                specificTypeOfRecipes.add(recipe);
            }
        }
        mapOfTypeOfRecipes.put(typeOfFood, specificTypeOfRecipes);
        return specificTypeOfRecipes;
    }

    public Map<String, List<Recipe>> getRecipiesCategory() {
        return mapOfTypeOfRecipes;
    }

    public Recipe getRecipe(String category, UUID id) {
        if (mapOfTypeOfRecipes.containsKey(category)) {
            for (Recipe recipe: mapOfTypeOfRecipes.get(category)) {
                if (recipe.getUuid().compareTo(id) == 0)
                    return recipe;
            }
        }
        return null;
    }

}
