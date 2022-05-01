package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.FragmentRecipeBinding;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;

import java.util.UUID;


/**
 * When the user has clicked on a recipe inside the recyclerview, this class will be
 * initialized. The class uses a pager to display information about the recipe in a user-friendly
 * way.
 * */
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

    public RecipeFragment() {}

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.single_recipe_toolbar, menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.app_bar_remove_item:
                removeRecipe();
                return true;
            case R.id.app_bar_modify:
                modifyRecipe();
                return true;
            case R.id.app_bar_favourite:
                setRecipeToFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When user presses the heart symbol in the top bar, the recipe object toggles its boolean
     * value to specify if it is regarded as a favorite or not. After toggling, a toast message
     * is displayed to prompt about the changes.
     * */
    private void setRecipeToFavorite() {
        if (!mRecipe.isFavourite()) {
            mRecipe.setFavourite(true);
            Toast.makeText(getActivity(), "Recipe set to favorites", Toast.LENGTH_SHORT).show();
        }
        else {
            mRecipe.setFavourite(false);
            Toast.makeText(getActivity(), "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
        }
        mRecipeManager.modifiedRecipe(mRecipe, mRecipe.getTypeOfFood());
    }

    /**
     * When the user press the modify symbol in the top bar, the fragment navigates to RecipeModifyFragment
     * which enables the user to modify the current recipe.
     * */
    private void modifyRecipe() {
        RecipeFragmentDirections.ActionRecipeFragmentToRecipeModifyFragment destination = RecipeFragmentDirections.actionRecipeFragmentToRecipeModifyFragment();
        destination.setCategory(mCategory);
        destination.setUid(mUUID);
        Navigation.findNavController(mViewBinder.getRoot()).navigate(destination);
    }

    private void removeRecipe() {
        popUpWindowRemoveRecipe();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mViewBinder = FragmentRecipeBinding.inflate(inflater, container, false);
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        View view = mViewBinder.getRoot();
        //Unpack the arguments that the previous fragment packed.
        mCategory = RecipeFragmentArgs.fromBundle(getArguments()).getGetCategory();
        mUUID = RecipeFragmentArgs.fromBundle(getArguments()).getGetUUID();

        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(mCategory);
        // The recipe is attained by using the unpacked data and call the RecipeManager.
        mRecipe = mRecipeManager.getRecipe(mCategory, UUID.fromString(mUUID));

        /**
         * Observer that is called when a recipe has changed. The UUID is compared to the current
         * recipe to check whether the this fragments recipe has been altered or not. If altered,
         * update the current recipe.
         * */
        Observer<UUID> modifiedRecipe = uuid -> {
            if (mRecipe != null) {
                if (mRecipe.getUuid().compareTo(uuid) == 0)
                {
                    mRecipe = mRecipeManager.getRecipe(uuid);
                    ((ActionBarTitleSetter)requireActivity()).setTitleActionBar(mRecipe.getTypeOfFood());
                }
            }

        };

        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(), modifiedRecipe);
        mViewPager2 = mViewBinder.viewpagerFragmentRecipe;

        return view;
    }

    /**
     * Ensures that the correct backstack entry is attained. This is needed because
     * We navigate to a dialog which does not switch the current lifecycle to pause.
     * */
    private void setBackStackListener() {

        NavController navController = NavHostFragment.findNavController(this);

        final NavBackStackEntry navBackStackEntry = navController.getCurrentBackStackEntry();

        final LifecycleEventObserver observer = (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_RESUME)
                    && navBackStackEntry.getSavedStateHandle().contains(RecipeStartFragment.CATEGORY_ARG)
                    && navBackStackEntry.getSavedStateHandle().contains(RecipeStartFragment.UUID_ARG))
            {
                String category = navBackStackEntry.getSavedStateHandle().get(RecipeStartFragment.CATEGORY_ARG);
                String uuid = navBackStackEntry.getSavedStateHandle().get(RecipeStartFragment.UUID_ARG);
                mRecipe = mRecipeManager.getRecipe(category, UUID.fromString(uuid));
                mPageAdapterRecipe = new PageAdapterRecipe(getChildFragmentManager(), getLifecycle(), mRecipe);
                mViewPager2.setAdapter(mPageAdapterRecipe);
            }

        };
        navBackStackEntry.getLifecycle().addObserver(observer);

        getViewLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_DESTROY))
                navBackStackEntry.getLifecycle().removeObserver(observer);
        });

    }

    /**
     * Sets the adapter to the pager and creates the tabs which can be used to switch between the fragments
     * inside the pager.
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPageAdapterRecipe = new PageAdapterRecipe(getChildFragmentManager(), getLifecycle(), mRecipe);
        mViewPager2.setAdapter(mPageAdapterRecipe);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_fragment_recipe);
        new TabLayoutMediator(tabLayout, mViewPager2, (tab, position) -> {
            if (position == 0)
                tab.setText("Overview");
            else if (position == 1)
                tab.setText("Ingredients");
            else if (position == 2)
                tab.setText("Steps");
        }).attach();

        setBackStackListener();
    }

    /**
     * Creates a small pop-up window that gives the option of removing a recipe.
     * This method is called when the remove symbol on the top bar is pressed.
     * */
    private void popUpWindowRemoveRecipe() {
        View popUp = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window_remove, null);
        final PopupWindow popupWindow = new PopupWindow(
                popUp, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);
        TextView label = popUp.findViewById(R.id.label_popUp_window_remove);
        label.setText("Remove Recipe");
        Button remove = popUp.findViewById(R.id.remove_button_popUp);
        Button dismiss = popUp.findViewById(R.id.dismiss_popUp);

        dismiss.setOnClickListener(view1 -> { popupWindow.dismiss();});

        remove.setOnClickListener(view12 -> {
            mRecipeManager.removeRecipe(mRecipe);
            popupWindow.dismiss();
            Navigation.findNavController(mViewBinder.getRoot()).popBackStack();
        });
    }

}
    /**
     * Pager class that creates three different fragments, one start fragment which displays
     * the overview of the recipe. The second fragment shows the ingredients in a list.
     * The third fragment displays the steps for creating the recipe.
     * */
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
            RecipeIngredientFragment ingredientsFragment = new RecipeIngredientFragment();
            prepareArgs(ingredientsFragment);
            return ingredientsFragment;

        }
        else if (position == 2) {
            RecipeStepsFragment stepsFragment = new RecipeStepsFragment();
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







