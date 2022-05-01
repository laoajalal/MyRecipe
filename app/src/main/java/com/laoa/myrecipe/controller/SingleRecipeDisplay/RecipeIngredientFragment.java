package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeIngredientsBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;
import java.util.UUID;

/**
 * Class used inside the pager to display the ingredients.
 * */
public class RecipeIngredientFragment extends Fragment {

    private String category;
    private String uuid;

    private ArrayAdapter arrayAdapter;
    private RecipeManager mRecipeManager;
    private FragmentRecipeIngredientsBinding mViewBinder;

    private List<String> mIngredients;
    private Recipe mRecipe;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category = (String) bundle.get(RecipeFragment.RECIPE_CATEGORY);
            uuid = (String) bundle.get(RecipeFragment.RECIPE_UUID);
        }

        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        System.out.println("THE CATEGORY:" + category);
        mRecipe = mRecipeManager.getRecipe(category, UUID.fromString(uuid));
        mIngredients = mRecipe.getIngredients();
        mViewBinder = FragmentRecipeIngredientsBinding.inflate(inflater,container, false);

        configureListViewAdapter(mViewBinder.listItemIngredients);
        View view = mViewBinder.getRoot();
        recipeModifiedListener();

        return view;
    }

    private void configureListViewAdapter(ListView listView) {
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.fragment_recipe_ingredients_item, R.id.single_ingredient_textview, mIngredients);
        listView.setAdapter(arrayAdapter);
    }

    private void recipeModifiedListener() {
        Observer<UUID> hasRecipeBeenModified = uuid -> {
            if (mRecipe.getUuid().compareTo(uuid) == 0)
            {
                arrayAdapter.notifyDataSetChanged();
            }
        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), hasRecipeBeenModified);

    }

}
