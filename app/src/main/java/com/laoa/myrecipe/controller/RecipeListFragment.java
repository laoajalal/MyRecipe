package com.laoa.myrecipe.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeListBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;


public class RecipeListFragment extends Fragment {
    private RecipeManager mRecipeManager;
    private RecyclerView mRecyclerViewListOfRecipes;
    private FragmentRecipeListBinding viewBinder;
    private RecipeListAdapter mRecipeListAdapter;

    private String mCategory;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(R.menu.recipe_list_toolbar, inflater);
        inflater.inflate(R.menu.recipe_list_toolbar, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        mCategory = RecipeListFragmentArgs.fromBundle(getArguments()).getGetCategory();
        viewBinder = FragmentRecipeListBinding.inflate(inflater, container, false);
        View view = viewBinder.getRoot();

        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(mCategory);

        mRecyclerViewListOfRecipes = viewBinder.recipeList;
        mRecyclerViewListOfRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(mCategory);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void updateUI(String category) {

        List<Recipe> recipes = mRecipeManager.getTypeOfRecipies(category);

        if (mRecipeListAdapter == null) {
            mRecipeListAdapter = new RecipeListAdapter(recipes);
            mRecyclerViewListOfRecipes.setAdapter(mRecipeListAdapter);
        } else {
            mRecyclerViewListOfRecipes.setAdapter(mRecipeListAdapter);
            mRecipeListAdapter.updateItem();
        }

    }

    private class RecipeListHolder extends RecyclerView.ViewHolder {

        private ViewFlipper mViewFlipper;
        private TextView mTitle;
        private TextView mDescription;

        private CardView mCardView;
        private ScrollView mScrollView;
        private GestureDetector mGestureDetector;

        private Recipe mRecipe;


        public RecipeListHolder(LayoutInflater inflater, ViewGroup parent, int viewID) {
            super(inflater.inflate(viewID, parent, false));
            mViewFlipper = itemView.findViewById(R.id.single_food_viewFlipper);
            mTitle = itemView.findViewById(R.id.single_food_name);
            mDescription = itemView.findViewById(R.id.single_food_description);
            mScrollView = itemView.findViewById(R.id.single_food_scrollview);

            mCardView = itemView.findViewById(R.id.cardView);

            mViewFlipper.setInAnimation(getActivity(),android.R.anim.fade_in);
            mViewFlipper.setOutAnimation(getActivity(), android.R.anim.fade_out);


            mGestureDetector = new GestureDetector(getActivity(), new CustomGestureDetector());
            mViewFlipper.setOnTouchListener((view, motionEvent) ->{
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            });

            mScrollView.setOnTouchListener((view, motionEvent) -> {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                view.onTouchEvent(motionEvent);
                return true;
            });

            mCardView.setOnClickListener(view -> {
                RecipeListFragmentDirections.ActionRecipeListFragmentToRecipeFragment destination = RecipeListFragmentDirections.actionRecipeListFragmentToRecipeFragment();
                destination.setGetCategory(mRecipe.getTypeOfFood());
                destination.setGetUUID(mRecipe.getUuid().toString());
                Navigation.findNavController(view).navigate(destination);
            });

        }

        public void bind(Recipe recipe) {
            mRecipe = recipe;
            setImages(recipe);
            setDescription(recipe);
            setTitle(recipe);
        }

        private void setImages(Recipe recipe) {
            if (recipe.getFoodImagePaths().size()>0) {
                mViewFlipper.setBackground(null);
                for (String path: recipe.getFoodImagePaths()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ImageView image = new ImageView(getActivity());
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                    image.setImageBitmap(bitmap);
                    mViewFlipper.addView(image);
                }
            }
            else
                mViewFlipper.setBackgroundResource(R.drawable.image_icon);
        }

        private void setDescription(Recipe recipe) {
            mDescription.setText(recipe.getDescription());
        }

        private void setTitle(Recipe recipe) {
            mTitle.setText(recipe.getRecipeName());
        }


        class CustomGestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() > e2.getX())
                {
                    mViewFlipper.showNext();
                }
                if (e1.getX() < e2.getX()) {
                    mViewFlipper.showPrevious();

                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        }
    }



    private class RecipeListAdapter extends RecyclerView.Adapter<RecipeListHolder> {

        private List<Recipe> mRecipeList;

        public RecipeListAdapter(List<Recipe> recipes) {
            mRecipeList = recipes;
        }

        @NonNull
        @Override
        public RecipeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            int viewID = R.layout.single_food_display;
            return new RecipeListHolder(layoutInflater,parent,viewID);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeListHolder holder, int position) {
            Recipe recipe = mRecipeList.get(position);
            holder.bind(recipe);
        }

        @Override
        public int getItemCount() {
            return mRecipeList.size();
        }

        public void updateItem() {
            notifyDataSetChanged();
        }
    }

}