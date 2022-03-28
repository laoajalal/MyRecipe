package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeOverviewBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;
import com.laoa.myrecipe.utils.OnSwipeListener;

import java.util.UUID;

public class RecipeStartFragment extends Fragment {

    private Recipe mRecipe;
    private RecipeManager mRecipeManager;
    private FragmentRecipeOverviewBinding mViewBinder;

    private GestureDetector mOnTouchParentScrollListener;
    private GestureDetector mOnTouchChildScrollListener;
    private GestureDetector mOnTouchViewFlipper;

    private String category;
    private String uuid;

    private ViewFlipper mViewFlipper;
    private TextView mRecipeName;
    private TextView mCookTime;
    private TextView mCategory;
    private TextView mDescription;

    private ImageView mLeftArrow;
    private ImageView mRightArrow;

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


        mViewBinder = FragmentRecipeOverviewBinding.inflate(inflater,container, false);
        View view = mViewBinder.getRoot();
        initWidgets();
        updateUI(mRecipe);
        setChangeImageListeners();
        setScrollViewConfiguration();


        Observer<UUID> recipeModifiedObserver = new Observer<UUID>() {
            @Override
            public void onChanged(UUID uuid) {
                if (mRecipe.getUuid().compareTo(uuid) == 0)
                {
                    mRecipe = mRecipeManager.getRecipe(uuid);
                    mViewFlipper.removeAllViews();
                    updateUI(mRecipe);
                }
            }
        };

        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), recipeModifiedObserver);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    private void updateUI(Recipe recipe) {
        setRecipeName(recipe);
        setCookTime(recipe);
        setCategory(recipe);
        setDescription(recipe);
        setImages(recipe);
    }

    private void setImages(Recipe recipe) {
        if (recipe.getFoodImagePaths().size()>0) {
            mViewFlipper.setBackground(null);
            for (String path: recipe.getFoodImagePaths()) {
                ImageView image = new ImageView(requireActivity());
                Glide.with(requireActivity())
                        .load(path)
                        .centerCrop()
                        .into(image);
                mViewFlipper.addView(image);
            }
        }
        else
            mViewFlipper.setBackgroundResource(R.drawable.image_icon);
    }

    private void setRecipeName(Recipe recipe) {
        mRecipeName.setText(recipe.getRecipeName());
    }

    private void setCookTime(Recipe recipe) {
        int cookTime = recipe.getCookTime();
        mCookTime.setText(Integer.toString(cookTime));
    }

    private void setCategory(Recipe recipe) {
        mCategory.setText(recipe.getTypeOfFood());
    }

    private void setDescription(Recipe recipe) {
        mDescription.setText(recipe.getDescription());
    }


    private void setChangeImageListeners() {

        mLeftArrow.setOnClickListener(view -> {
            mViewFlipper.showPrevious();
        });

        mRightArrow.setOnClickListener(view -> {
            mViewFlipper.showNext();
        });

    }

    private void initWidgets() {
        /**
         * Init viewflipper with animation for in and out swiping
         * */
        mViewFlipper = mViewBinder.fragmentRecipeViewFlipper;
        mViewFlipper.setInAnimation(getActivity(),android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(getActivity(), android.R.anim.fade_out);

        mRecipeName = mViewBinder.recipeNameTextview;
        mCookTime = mViewBinder.prepTime;
        mCategory = mViewBinder.typeOfFood;
        mDescription = mViewBinder.singleFoodDescription;

        mLeftArrow = mViewBinder.leftArrowChangePicture;
        mRightArrow = mViewBinder.rightArrowChangePicture;

    }

    private GestureDetector createMotionDetector(ViewParent scroller) {

        GestureDetector gestureDetector = new GestureDetector(getActivity(), new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up )
                {
                    scroller.requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                else if (direction == Direction.down)
                {
                    scroller.requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                else if (direction == Direction.left) {
                    scroller.requestDisallowInterceptTouchEvent(false);
                    return true;
                }
                else if (direction == Direction.right) {
                    scroller.requestDisallowInterceptTouchEvent(false);
                    return true;
                }

                return super.onSwipe(direction);
            }
        });

        return gestureDetector;
    }

    private void setScrollViewConfiguration() {
        mOnTouchParentScrollListener = createMotionDetector(mViewBinder.getRoot());
        mOnTouchChildScrollListener = createMotionDetector(mViewBinder.innerScrollViewTextDescription.getParent());
        mViewBinder.scrollViewOverview.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent !=  null)
                return mOnTouchParentScrollListener.onTouchEvent(motionEvent);
            return false;
        });

        mViewBinder.innerScrollViewTextDescription.setOnTouchListener((view, motionEvent) -> mOnTouchChildScrollListener.onTouchEvent(motionEvent));

    }
}
