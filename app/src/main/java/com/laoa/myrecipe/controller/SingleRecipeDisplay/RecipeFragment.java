package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.laoa.myrecipe.databinding.FragmentRecipeBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.UUID;


public class RecipeFragment extends Fragment {


    public static String RECIPE_CATEGORY = "category";
    public static String RECIPE_UUID = "uuid";

    private PageAdapterRecipe mPageAdapterRecipe;
    private ViewPager2 mViewPager2;

    private FragmentRecipeBinding mViewBinder;

    private RecipeManager mRecipeManager;
    private Recipe mRecipe;
    private String mCategory;
    private String mUUID;

    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewBinder = FragmentRecipeBinding.inflate(inflater, container, false);
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        View view = mViewBinder.getRoot();
        mCategory = RecipeFragmentArgs.fromBundle(getArguments()).getGetCategory();
        mUUID = RecipeFragmentArgs.fromBundle(getArguments()).getGetUUID();

        mRecipe = mRecipeManager.getRecipe(mCategory, UUID.fromString(mUUID));
        mViewPager2 = mViewBinder.viewpagerFragmentRecipe;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPageAdapterRecipe = new PageAdapterRecipe(getChildFragmentManager(), getLifecycle(), mRecipe);
        mViewPager2.setAdapter(mPageAdapterRecipe);
    }

    public static class RecipeIngredientsFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    public static class RecipeStepsFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);

        }

    }



}
    class PageAdapterRecipe extends FragmentStateAdapter {

    private Recipe mRecipe;

    public PageAdapterRecipe(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Recipe recipe) {
        super(fragmentManager, lifecycle);
        mRecipe = recipe;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            RecipeStartFragment startFragment = new RecipeStartFragment();
            prepareArgs(startFragment);
            return startFragment;

        }
        else if (position == 1) {
            RecipeFragment.RecipeIngredientsFragment ingredientsFragment = new RecipeFragment.RecipeIngredientsFragment();
            prepareArgs(ingredientsFragment);
            return ingredientsFragment;

        }
        else if (position == 2) {
            RecipeFragment.RecipeStepsFragment stepsFragment = new RecipeFragment.RecipeStepsFragment();
            prepareArgs(stepsFragment);
            return stepsFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    private void prepareArgs(Fragment fragment) {
        Bundle args = new Bundle();
        args.putString(RecipeFragment.RECIPE_CATEGORY, mRecipe.getTypeOfFood());
        args.putString(RecipeFragment.RECIPE_UUID, mRecipe.getUuid().toString());
        fragment.setArguments(args);
    }

}




