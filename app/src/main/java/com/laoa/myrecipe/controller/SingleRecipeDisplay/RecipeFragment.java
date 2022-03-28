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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.laoa.myrecipe.ActionBarTitleSetter;
import com.laoa.myrecipe.R;
import com.laoa.myrecipe.controller.MainFragmentDirections;
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(R.menu.recipe_list_toolbar, inflater);
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

    private void setRecipeToFavorite() {
    }

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
        // Inflate the layout for this fragment
        mViewBinder = FragmentRecipeBinding.inflate(inflater, container, false);
        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);

        View view = mViewBinder.getRoot();
        mCategory = RecipeFragmentArgs.fromBundle(getArguments()).getGetCategory();
        mUUID = RecipeFragmentArgs.fromBundle(getArguments()).getGetUUID();

        ((ActionBarTitleSetter)getActivity()).setTitleActionBar(mCategory);

        mRecipe = mRecipeManager.getRecipe(mCategory, UUID.fromString(mUUID));

        Observer<UUID> modifiedRecipe = new Observer<UUID>() {
            @Override
            public void onChanged(UUID uuid) {
                if (mRecipe.getUuid().compareTo(uuid) == 0)
                {
                    mRecipe = mRecipeManager.getRecipe(uuid);
                    ((ActionBarTitleSetter)requireActivity()).setTitleActionBar(mRecipe.getTypeOfFood());
                }

            }
        };
        mRecipeManager.getRecipeModified().observe(getViewLifecycleOwner(),modifiedRecipe);
        mViewPager2 = mViewBinder.viewpagerFragmentRecipe;


        return view;
    }

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

    }

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







