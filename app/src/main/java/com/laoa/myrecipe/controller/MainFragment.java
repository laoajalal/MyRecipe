package com.laoa.myrecipe.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter mRecipeAdapter;
    private FloatingActionButton addLabelButton;
    private RecipeManager mRecipeManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        recyclerView = view.findViewById(R.id.list_of_labeled_recipe);
        addLabelButton = view.findViewById(R.id.add_label_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        final Observer<List<String>> categories = new Observer<List<String>>() {

            @Override
            public void onChanged(List<String> strings) {
                for (String temp: strings) {
                    System.out.println("Cathegories are: " + temp);
                }
            }
        };

        final Observer<Boolean> addedEvent = aBoolean -> {
            if (aBoolean) {
                UpdateUI();
                mRecipeManager.setAddedEvent(false);
            }

        };
        mRecipeManager.getAddedEvent().observe(getActivity(), addedEvent);
        mRecipeManager.getCategoriesMutableLiveData().observe(getActivity(),categories);

        UpdateUI();
        //TODO: add necessary action
        addLabelButton.setOnClickListener(view1 -> {
            //mRecipeManager.getFirstRecipeOut();
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_recipeAddFragment);
        }
        );

        return view;
    }
    private void UpdateUI() {

        Map<String, List<Recipe>> recipes = mRecipeManager.getRecipiesCategory();

        if (mRecipeAdapter == null) {
            System.out.println("was null");
            mRecipeAdapter = new RecipeRecyclerAdapter(recipes);
            recyclerView.setAdapter(mRecipeAdapter);
        } else {
            recyclerView.setAdapter(mRecipeAdapter);
            mRecipeAdapter.updateItem();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("OnPause in MainFragment Called");
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateUI();
        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(getString(R.string.app_name));
        System.out.println("OnResume in MainFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy in MainFragment");
        //mRecipeManager.deleteAllDB(); //TODO: remove, only for testing purpose
    }

    private class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {


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
                    Bitmap bitmap = BitmapFactory.decodeFile(recipe.getFoodImagePaths().get(0));
                    imageView.setImageBitmap(bitmap);
                }

        }

        public void bind(List<Recipe> recipe) {
            if (recipe.size() >0) {
                mRecipes = recipe;
                typeOfFood = recipe.get(0).getTypeOfFood();
                mTitleTextView.setText(typeOfFood);

                mLeftTextView.getLayoutParams().width = itemView.getWidth()/3;
                mMiddleTextView.getLayoutParams().width = itemView.getWidth()/3;
                mRightTextView.getLayoutParams().width = itemView.getWidth()/3;
                mLeftTextView.requestLayout();
                mMiddleTextView.requestLayout();
                mRightTextView.requestLayout();

                UpdateImageIfAvailable(recipe);
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

        //TODO: transfer the type of food.
        @Override
        public void onClick(View view) {
            MainFragmentDirections.ActionMainFragmentToRecipeListFragment destination = MainFragmentDirections.actionMainFragmentToRecipeListFragment();
            destination.setGetCategory(typeOfFood);
            Navigation.findNavController(view).navigate(destination);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction())
            {
            }
            return true;
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
            System.out.println("Cathegory:" + mRecipeManager.getCategory(position));
            List<Recipe> recipes = mRecipes.get(mRecipeManager.getCategory(position));
            holder.bind(recipes);
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




