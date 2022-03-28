package com.laoa.myrecipe.controller.SingleRecipeDisplay;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.laoa.myrecipe.controller.RecipeAddFragment;
import com.laoa.myrecipe.databinding.RecipeAddBinding;
import com.laoa.myrecipe.models.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Modifying and adding recipes share many similarities in functionalities, thus, RecipeModifyFragment extends from RecipeAddFragment
 * And overwrites methods to assure that the recipe is modified and not added.
 * */
public class RecipeModifyFragment extends RecipeAddFragment {

    private String category;
    private String uuid;
    private Recipe recipe;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        category = RecipeModifyFragmentArgs.fromBundle(getArguments()).getCategory();
        uuid = RecipeModifyFragmentArgs.fromBundle(getArguments()).getUid();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        recipe = mRecipeManager.getRecipe(UUID.fromString(uuid));
        setImages(recipe);
        updateInputFields(recipe);
        return view;

    }

    private void updateInputFields(Recipe recipe) {
        getmRecipeName().setText(recipe.getRecipeName());
        getmDescription().setText(recipe.getDescription());
        for (String step: recipe.getRecipeSteps()) {
            getmSteps().append(step + "\n");
        }
        getmCategory().setSelection(getmSpinnerAdapter().getPosition(recipe.getTypeOfFood()));
        getmCookTime().setText(Integer.toString(recipe.getCookTime()));
        recipe.getIngredients().stream().forEach(ingredient -> getmIngredients().append(ingredient +"\n"));

    }

    private void setImages(Recipe recipe) {
        for (String path: recipe.getFoodImagePaths()) {
            File file = new File(path);
            mImageFlipperHandler.addViewPath(file);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void UpdateRecipeFields(Recipe recipe) {
        recipe.setRecipeName(getEditTextAsString(getmRecipeName()));
        recipe.setCookTime(getCookTime());
        recipe.setTypeOfFood(getTypeOfFood());
        recipe.setIngredients(getEditTextInputAsList(getmIngredients(), recipe.getIngredients()));
        recipe.setRecipeSteps(getEditTextInputAsList(getmSteps(), recipe.getRecipeSteps()));
        recipe.setDescription(getEditTextAsString(getmDescription()));
        System.out.println("Number of photos saved is:" +mImageFlipperHandler.getImagesAbsolutePaths().size());
        setFoodImages(recipe, mImageFlipperHandler.getImagesAbsolutePaths());
        //recipe.setFoodImagePaths(mImageFlipperHandler.getImagesAbsolutePaths());
    }

    private void setFoodImages(Recipe recipe, List<String> newImages) {
        List<String> listOfImagesPath = recipe.getFoodImagePaths();
        listOfImagesPath.clear();
        for (String path: newImages) {
            listOfImagesPath.add(path);
        }
    }

    public List<String> getEditTextInputAsList(EditText component, @NonNull List<String> stringList) {
        Editable text = component.getText();
        stringList.clear();
        if (text.toString().length() > 0) {
            stringList.addAll(Arrays.asList(text.toString().split(System.lineSeparator())));
            return stringList;
        }
        return stringList;
    }


    @Override
    public void onSavedPressed() {
        UpdateRecipeFields(recipe);
        mRecipeManager.modifiedRecipe(recipe, category);
        mImageFlipperHandler.reset();
        NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void setCancelButtonListener() {
        super.setCancelButtonListener();
    }
}
