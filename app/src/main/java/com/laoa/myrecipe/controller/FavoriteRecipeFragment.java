package com.laoa.myrecipe.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.databinding.FragmentRecipeListBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;
import java.util.UUID;


/**
 * This class extends from ListTemplateFragment. It is almost identical to RecipeListFragment
 * But instead only displays the recipes that the user has marked as their favorites.
 * */
public class FavoriteRecipeFragment extends ListTemplateFragment {

    @Override
    public void updateUI(String category) {
        List<Recipe> favorites = mRecipeManager.getFavoriteRecipes();
        mRecipeListAdapter = new ListTemplateFragment.RecipeListAdapter(favorites);
        mRecyclerViewListOfRecipes.setAdapter(mRecipeListAdapter);
    }

    /**
     * Initializes the view components and attains a list of Recipes that are marked as favorites.
     * When a recipe has been modified, the observer is triggered and updates the UI accordingly.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        viewBinder = FragmentRecipeListBinding.inflate(inflater, container, false);
        View view = viewBinder.getRoot();

        setTitleActionbar("Favorites");

        Observer<UUID> recipeModified = uuid ->{
                updateUI(null);
        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), recipeModified);

        mRecyclerViewListOfRecipes = viewBinder.recipeList;
        mRecyclerViewListOfRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(null);

        return view;
    }

    @Override
    public void setTitleActionbar(String categories) {
        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(categories);
    }

    /**
     * Returns the destination when the user clicks on a recipe inside the recyclerview.
     * The destination carries meta data with information regarding what recipe to display in
     * the RecipeFragment class.
     * */
    @Override
    public NavDirections createNavigationDestination(String arg1, String arg2) {
        FavoriteRecipeFragmentDirections.ActionFavoriteRecipeFragmentToRecipeFragment destination = FavoriteRecipeFragmentDirections.actionFavoriteRecipeFragmentToRecipeFragment();
        destination.setGetCategory(arg1);
        destination.setGetUUID(arg2);
        return destination;
    }
}
