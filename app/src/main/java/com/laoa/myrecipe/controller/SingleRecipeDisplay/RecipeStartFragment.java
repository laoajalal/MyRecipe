package com.laoa.myrecipe.controller.SingleRecipeDisplay;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeOverviewBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;
import com.laoa.myrecipe.utils.OnSwipeListener;

import java.util.UUID;


/**
 * This class is the first of the fragment inside the Pager from RecipeFragment.
 * This Fragment displays the food pictures, recipe name, the recipes category,
 * description and cooking time.
 * */
public class RecipeStartFragment extends Fragment {

    public static final String UUID_ARG = "get_uid";
    public static final String CATEGORY_ARG = "get_category";


    private Recipe mRecipe;
    private RecipeManager mRecipeManager;
    private FragmentRecipeOverviewBinding mViewBinder;

    private GestureDetector mOnTouchParentScrollListener;
    private GestureDetector mOnTouchChildScrollListener;

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

        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        mViewBinder = FragmentRecipeOverviewBinding.inflate(inflater,container, false);
        View view = mViewBinder.getRoot();
        initWidgets();
        setChangeImageListeners();
        setScrollViewConfiguration();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category = (String) bundle.get(RecipeFragment.RECIPE_CATEGORY);
            uuid = (String) bundle.get(RecipeFragment.RECIPE_UUID);
        }
        mRecipe = mRecipeManager.getRecipe(category, UUID.fromString(uuid));
        updateUI(mRecipe);

        recipeModifiedListener();
    }

    /**
     * If the recipe has been changed in any fields, update the UI,
     * */
    private void recipeModifiedListener() {
        Observer<UUID> recipeModifiedObserver = uuid -> {
            if (mRecipe!=null) {
                if (mRecipe.getUuid().compareTo(uuid) == 0)
                {
                    mRecipe = mRecipeManager.getRecipe(uuid);
                    mViewFlipper.removeAllViews();
                    updateUI(mRecipe);
                }
            }

        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), recipeModifiedObserver);
    }

    private void updateUI(Recipe recipe) {
        if (recipe!=null) {
            setRecipeName(recipe);
            setCookTime(recipe);
            setCategory(recipe);
            setDescription(recipe);
            setImages(recipe);
        }
    }

    /**
     * Sets the images to the ViewFlipper. If there is no image,
     * An image icon will be used to show that theres is no image set.
     * */
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

    /**
     * Because we use pagers and scrollviews, there will be conflicts in which
     * container and view that should be focused when touched upon. This
     * method handles the case when the user wants to swipe right/left between
     * fragments and scroll horizontally inside the current fragment.
     * */
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
