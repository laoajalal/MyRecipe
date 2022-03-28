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
import com.laoa.myrecipe.databinding.FragmentRecipeOverviewBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

public class RecipeIngredientFragment extends Fragment {

    private String category;
    private String uuid;

    private ListView mListView;
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
        mRecipe = mRecipeManager.getRecipe(category, UUID.fromString(uuid));
        mIngredients = mRecipe.getIngredients();

        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.fragment_recipe_ingredients_item, R.id.single_ingredient_textview, mIngredients);
        mViewBinder = FragmentRecipeIngredientsBinding.inflate(inflater,container, false);

        mListView = mViewBinder.listItemIngredients;
        mListView.setAdapter(arrayAdapter);

        View view = mViewBinder.getRoot();

        Observer<UUID> hasRecipeBeenModified = new Observer<UUID>() {
            @Override
            public void onChanged(UUID uuid) {
                if (mRecipe.getUuid().compareTo(uuid) == 0)
                {
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), hasRecipeBeenModified);

        return view;
    }
}
