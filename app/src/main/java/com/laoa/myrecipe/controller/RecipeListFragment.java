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
 * A class that displays the recipes of a specific category.
 * */
public class RecipeListFragment extends ListTemplateFragment {

    private String mCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void updateUI(String category) {
        List<Recipe> recipes = mRecipeManager.getTypeOfRecipies(category);

        if (mRecipeListAdapter == null) {
            mRecipeListAdapter = new ListTemplateFragment.RecipeListAdapter(recipes);
            mRecyclerViewListOfRecipes.setAdapter(mRecipeListAdapter);
        } else {
            mRecyclerViewListOfRecipes.setAdapter(mRecipeListAdapter);
            mRecipeListAdapter.updateItem();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        mCategory = RecipeListFragmentArgs.fromBundle(getArguments()).getGetCategory();
        viewBinder = FragmentRecipeListBinding.inflate(inflater, container, false);
        View view = viewBinder.getRoot();
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        setTitleActionbar(mCategory);
        recipeHasModifiedListener();

        mRecyclerViewListOfRecipes = viewBinder.recipeList;
        mRecyclerViewListOfRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(mCategory);

        return view;
    }

    private void recipeHasModifiedListener() {
        Observer<UUID> recipeModified = uuid -> {
            if (mRecipeManager.getRecipe(uuid) !=null)
                updateUI(mRecipeManager.getRecipe(uuid).getTypeOfFood());
        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), recipeModified);
    }

    @Override
    public void setTitleActionbar(String category) {
        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(category);
    }

    @Override
    public NavDirections createNavigationDestination(String arg1, String arg2) {
        RecipeListFragmentDirections.ActionRecipeListFragmentToRecipeFragment destination = RecipeListFragmentDirections.actionRecipeListFragmentToRecipeFragment();
        destination.setGetCategory(arg1);
        destination.setGetUUID(arg2);
        return destination;
    }
}