package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.laoa.myrecipe.R;
import com.laoa.myrecipe.controller.SingleRecipeDisplay.RecipeFragment;
import com.laoa.myrecipe.databinding.FragmentRecipeOverviewBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;
import com.laoa.myrecipe.utils.OnSwipeListener;

import java.util.UUID;

public class RecipeStartFragment extends Fragment {

    private Recipe mRecipe;
    private RecipeManager mRecipeManager;
    private FragmentRecipeOverviewBinding mViewBinder;

    private GestureDetector mOnParentScrollListener;
    private GestureDetector mOnChildScrollListener;

    private String category;
    private String uuid;

    private ViewFlipper mViewFlipper;
    private TextView mRecipeName;
    private TextView mCookTime;
    private TextView mCategory;
    private TextView mDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category = (String) bundle.get(RecipeFragment.RECIPE_CATEGORY);
            uuid = (String) bundle.get(RecipeFragment.RECIPE_UUID);
            System.out.println("In here :Category: "+category + " UUID: "+uuid);
        }

        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        mRecipe = mRecipeManager.getRecipe(category, UUID.fromString(uuid));

        mViewBinder = FragmentRecipeOverviewBinding.inflate(inflater,container, false);
        View view = mViewBinder.getRoot();

        initWidgets();
        updateUI(mRecipe);
        setScrollViewConfiguration();

        return view;
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

    private void initWidgets() {
        mViewFlipper = mViewBinder.fragmentRecipeViewFlipper;
        mRecipeName = mViewBinder.recipeNameTextview;
        mCookTime = mViewBinder.prepTime;
        mCategory = mViewBinder.typeOfFood;
        mDescription = mViewBinder.singleFoodDescription;

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
        mOnParentScrollListener = createMotionDetector(mViewBinder.getRoot());
        mOnChildScrollListener = createMotionDetector(mViewBinder.innerScrollViewTextDescription.getParent());
        mViewBinder.scrollViewOverview.setOnTouchListener((view, motionEvent) -> {

            if (motionEvent !=  null)
                return mOnParentScrollListener.onTouchEvent(motionEvent);
            return false;
        });

        mViewBinder.innerScrollViewTextDescription.setOnTouchListener((view, motionEvent) -> mOnChildScrollListener.onTouchEvent(motionEvent));

    }
}
