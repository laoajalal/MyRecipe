package com.laoa.myrecipe.controller;

import android.content.Context;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeListBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.List;

/**
 * This class was originally a normal class (RecipeListFragment).
 * However, when the feature of adding recipes to a favorites category,
 * I decided to make the RecipeListFragment more abstract and thus created
 * this class that both RecipeListFragment and FavoriteRecipeFragment extends from.
 * */
public abstract class ListTemplateFragment extends Fragment {

    public RecipeManager mRecipeManager;
    public RecyclerView mRecyclerViewListOfRecipes;
    public FragmentRecipeListBinding viewBinder;
    public ListTemplateFragment.RecipeListAdapter mRecipeListAdapter;

    private String mCategory;


    /**
     * Three methods that the subclasses must implement to specify what the
     * RecyclerView should display, what destination to jump to, and the top bar title.
     * */
    public abstract void updateUI(String category);
    public abstract void setTitleActionbar(String category);
    public abstract NavDirections createNavigationDestination(String arg1, String arg2);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_list_toolbar, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }



    /**
     * Should be forced to be overwritten by the subclasses but
     * for now, the subclasses implicitly overwrites this method.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        mCategory = RecipeListFragmentArgs.fromBundle(getArguments()).getGetCategory();
        viewBinder = FragmentRecipeListBinding.inflate(inflater, container, false);
        View view = viewBinder.getRoot();

        setTitleActionbar(mCategory);

        mRecyclerViewListOfRecipes = viewBinder.recipeList;
        mRecyclerViewListOfRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI(mCategory);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * Class that creates the components that is displayed inside the RecyclerView.
     * */
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


            mGestureDetector = new GestureDetector(getActivity(), new ListTemplateFragment.RecipeListHolder.CustomGestureDetector());
            mViewFlipper.setOnTouchListener((view, motionEvent) ->{
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            });

            mScrollView.setOnTouchListener((view, motionEvent) -> {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                view.onTouchEvent(motionEvent);
                return true;
            });
            // When user clicks on the cardView, the fragment will navigate to display the current clicked recipe.
            mCardView.setOnClickListener(view -> {
                NavDirections destination = createNavigationDestination(mRecipe.getTypeOfFood(), mRecipe.getUuid().toString());
                Navigation.findNavController(view).navigate(destination);
            });

        }

        public void bind(Recipe recipe) {
            mRecipe = recipe;
            setImages(recipe);
            setDescription(recipe);
            setTitle(recipe);
        }

        /**
         * Sets the images to the ViewFlipper. If there is no image,
         * An image icon will be used to show that theres is no image set.
         * */
        private void setImages(Recipe recipe) {
            if (recipe.getFoodImagePaths().size() > 0) {
                mViewFlipper.setBackground(null);
                for (String path: recipe.getFoodImagePaths()) {
                    ImageView image = new ImageView(getActivity());
                    Glide.with(getActivity())
                            .load(path)
                            .centerCrop()
                            .into(image);
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

        /**
         * Class used to detect which way the user has slide the finger on. If the direction is right,
         * the next image is presented inside the viewFlipper. If the direction is left, the previous
         * image will display.
         * */
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


    /**
     * Adapter class that contains the recipes of the specified category.
     * */
    protected class RecipeListAdapter extends RecyclerView.Adapter<ListTemplateFragment.RecipeListHolder> {

        private List<Recipe> mRecipeList;

        public RecipeListAdapter(List<Recipe> recipes) {
            mRecipeList = recipes;
        }

        @NonNull
        @Override
        public ListTemplateFragment.RecipeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            int viewID = R.layout.single_food_display;
            return new ListTemplateFragment.RecipeListHolder(layoutInflater,parent,viewID);
        }

        @Override
        public void onBindViewHolder(@NonNull ListTemplateFragment.RecipeListHolder holder, int position) {
            Recipe recipe = mRecipeList.get(position);
            holder.bind(recipe);
        }

        @Override
        public int getItemCount() {
            if (mRecipeList != null)
                return mRecipeList.size();
            else
                return 0;
        }

        public void updateItem() {
            notifyDataSetChanged();
        }
    }
}
