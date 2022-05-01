package com.laoa.myrecipe.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;
import java.util.Map;

/**
 * When application starts, this fragment is the first to appear. It shows
 * the list of categories and thumbnails of the recipe images of that category.
 * Alongside the list, a floating action button will allow the user to add a new recipe to
 * his collection. At the top bar, the user navigate to his favorite recipes.
 * */
public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter mRecipeAdapter;
    private FloatingActionButton addLabelButton;
    private RecipeManager mRecipeManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_main_toolbar, menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.app_bar_favourite_inbox:
                onFavoritePressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onFavoritePressed() {
        NavHostFragment.findNavController(this).navigate(R.id.action_mainFragment_to_favoriteRecipeFragment);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecipeManager.setState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.list_of_labeled_recipe);
        addLabelButton = view.findViewById(R.id.add_label_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initRecipesFromDB();
        recipeHasBeenAddedObserver();

        UpdateUI();
        addLabelButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_recipeAddFragment);
        }
        );

        return view;
    }

    private void recipeHasBeenAddedObserver() {
        final Observer<Boolean> addedEvent = aBoolean -> {
            if (aBoolean) {
                UpdateUI();
                mRecipeManager.setAddedEvent(false);
            }

        };
        mRecipeManager.getAddedEvent().observe(getViewLifecycleOwner(), addedEvent);
    }

    /**
     * This should only be called when the app starts/resets.
     * When called, the data from the database is loaded to the RecipeManager
     * which handles the data accordingly. After updating the RecipeManager, the UI is updated.
     * Uses background thread to update the data to avoid UI-lag.
     * */
    private void initRecipesFromDB() {
        mRecipeManager.loadDB().observe(getViewLifecycleOwner(), recipes -> {
            if (!mRecipeManager.hasLoadedDB()) {
                new Thread(() -> {
                    for (Recipe recipe: recipes)
                    {
                        mRecipeManager.addRecipeNoUpdate(recipe);
                    }
                }).start();

                UpdateUI();
                mRecipeManager.setHasLoadedDB(true);
            }
        });
    }

    private void UpdateUI() {

        Map<String, List<Recipe>> recipes = mRecipeManager.getRecipiesCategory();
        if (mRecipeAdapter == null) {
            mRecipeAdapter = new RecipeRecyclerAdapter(recipes);
            recyclerView.setAdapter(mRecipeAdapter);
        } else {
            recyclerView.setAdapter(mRecipeAdapter);
            mRecipeAdapter.updateItem();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(getString(R.string.app_name));
    }


    /**
     * Holder class that contains the recipes of each category.
     * If the category contains 3 or more recipes, the first 3 recipes will
     * be displayed as a thumbnail.
     * */
    private class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        private TextView mTitleTextView;
        private ImageView mLeftTextView;
        private ImageView mMiddleTextView;
        private ImageView mRightTextView;


        private String typeOfFood;
        private List<Recipe> mRecipes;

        private void UpdateImageIfAvailable(List<Recipe> recipes) {
            int counter = 0;
            for (Recipe recipe: recipes) {
                if (counter == 0)
                    UpdateImage(recipe, mLeftTextView);
                else if (counter == 1)
                    UpdateImage(recipe, mMiddleTextView);
                else if (counter == 2)
                    UpdateImage(recipe, mRightTextView);
                counter++;
            }

        }

        private void UpdateImage(Recipe recipe, ImageView imageView) {
                if (recipe.getFoodImagePaths().size() > 0) {
                    Glide.with(getActivity())
                            .load(recipe.getFoodImagePaths().get(0))
                            .centerCrop()
                            .into(imageView);
                }
        }

        public void bind(List<Recipe> recipe, String category) {
            typeOfFood = category;
            mTitleTextView.setText(typeOfFood);

            if (recipe != null) {
                if (recipe.size() > 0) {
                    mRecipes = recipe;
                    mLeftTextView.getLayoutParams().width = itemView.getWidth()/3;
                    mMiddleTextView.getLayoutParams().width = itemView.getWidth()/3;
                    mRightTextView.getLayoutParams().width = itemView.getWidth()/3;
                    mLeftTextView.requestLayout();
                    mMiddleTextView.requestLayout();
                    mRightTextView.requestLayout();
                    UpdateImageIfAvailable(recipe);
                }

            }
        }

        public RecipeHolder(LayoutInflater inflater, ViewGroup parent, int viewID) {
            super(inflater.inflate(viewID, parent, false));

            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.foodLabel);
            mLeftTextView = itemView.findViewById(R.id.food_1);
            mMiddleTextView = itemView.findViewById(R.id.food_2);
            mRightTextView = itemView.findViewById(R.id.food_3);

        }

        @Override
        public void onClick(View view) {
            MainFragmentDirections.ActionMainFragmentToRecipeListFragment destination = MainFragmentDirections.actionMainFragmentToRecipeListFragment();
            destination.setGetCategory(typeOfFood);
            Navigation.findNavController(view).navigate(destination);
        }

    }

    public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeHolder> {

        private Map<String, List<Recipe>> mRecipes;

        public RecipeRecyclerAdapter(Map<String, List<Recipe>> recipeList) {
            mRecipes = recipeList;
        }

        @NonNull
        @Override
        public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            int viewID = R.layout.food_card_display;
            return new RecipeHolder(layoutInflater,parent,viewID);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {

            holder.bind(mRecipes.get(mRecipeManager.getCategory(position)), mRecipeManager.getCategory(position));
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }

        public void updateItem() {
            notifyDataSetChanged();
        }

    }

}




