package com.laoa.myrecipe.controller;

import android.app.Dialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.laoa.myrecipe.R;
import com.laoa.myrecipe.databinding.RecipeAddBinding;
import com.laoa.myrecipe.models.CustomViewFlipper;
import com.laoa.myrecipe.models.ImageFlipperHandler;
import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.models.RecipeManager;
import com.laoa.myrecipe.utils.ImageScaler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RecipeAddFragment extends DialogFragment {


    private static boolean IS_ADD_CATEGORY_WINDOW_OPEN = false;
    private static boolean IS_REMOVE_IMAGE_WINDOW_OPEN = false;

    private ActivityResultLauncher<Uri> startActivityCamera;
    private RecipeAddBinding viewBinder;

    private CustomViewFlipper mViewFlipper;

    private Button mCamera;
    private Button mSaveButton;
    private Button mCancelButton;
    private ImageButton mAddnewTypeButton;

    private ImageView mLeftArrow;
    private ImageView mRightArrow;

    private EditText mRecipeName;
    private EditText mCookTime;
    private EditText mIngredients;
    private EditText mSteps;
    private EditText mDescription;

    private Spinner mCategory;
    private ArrayAdapter mSpinnerAdapter;

    private RecipeManager mRecipeManager;
    private ImageFlipperHandler mImageFlipperHandler;

    private File currentFile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            IS_ADD_CATEGORY_WINDOW_OPEN = (boolean) savedInstanceState.get("window_category_add");
            IS_REMOVE_IMAGE_WINDOW_OPEN = (boolean) savedInstanceState.get("window_image_remove");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        viewBinder = RecipeAddBinding.inflate(inflater, container, false);
        View view = viewBinder.getRoot();

        view.post(() -> {
            if(IS_REMOVE_IMAGE_WINDOW_OPEN) {
                popUpWindowRemovePicture();
            }
            if (IS_ADD_CATEGORY_WINDOW_OPEN)
                popUpWindowAddNewCategory();
        });

        mRecipeManager = new ViewModelProvider(requireActivity()).get(RecipeManager.class);
        mImageFlipperHandler = new ViewModelProvider(requireActivity()).get(ImageFlipperHandler.class);

        initWidgets();
        configureSpinnerAdapter(mRecipeManager.getCategories());

        setSaveButtonListener();
        setCancelButtonListener();
        removeImageListener();
        addNewTypeListener();

        Observer<List<String>> categoriesAdded = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                for (String string: strings) {
                    System.out.println(string);
                }
                if (getActivity()!=null)
                    configureSpinnerAdapter(strings);

                }

        };

        mRecipeManager.getCategoriesMutableLiveData().observe(getActivity(),categoriesAdded);

        mViewFlipper.setChangeViewListener(mImageFlipperHandler);

        mLeftArrow.setOnClickListener(view1 -> mViewFlipper.showPrevious());

        mRightArrow.setOnClickListener(view12 -> mViewFlipper.showNext());

        mCamera.setOnClickListener(view13 -> takePicture());



        view.getViewTreeObserver().addOnWindowFocusChangeListener( hasFocus -> {
            for (File path : mImageFlipperHandler.getFilePathsImages() ) {
                if (path != null && path.exists() && hasFocus && !mViewFlipper.alreadyDisplayed(path))
                {
                    displayImage(view.getWidth(), mViewFlipper.getHeight(), path);
                }
            }
        });

        return view;
    }

    public void setSaveButtonListener() {
        mSaveButton.setOnClickListener(view -> {
            mRecipeManager.addRecipe(getSavedRecipe());
            mRecipeManager.setAddedEvent(true);
            mImageFlipperHandler.reset();
            NavHostFragment.findNavController(RecipeAddFragment.this).popBackStack();
        });

    }

    public void setCancelButtonListener() {
        mCancelButton.setOnClickListener(view -> {
            mImageFlipperHandler.reset();
            NavHostFragment.findNavController(RecipeAddFragment.this).popBackStack();
        });
    }


    public Recipe getSavedRecipe() {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(getEditTextAsString(mRecipeName));
        recipe.setCookTime(getCookTime());
        recipe.setTypeOfFood(getTypeOfFood());
        recipe.setIngredients(getEditTextInputAsList(mIngredients));
        recipe.setRecipeSteps(getEditTextInputAsList(mSteps));
        recipe.setDescription(getEditTextAsString(mDescription));
        recipe.setFoodImagePaths(mImageFlipperHandler.getImagesAbsolutePaths());
        return recipe;
    }

    private List<String> getEditTextInputAsList(EditText component) {
        Editable text = component.getText();
        if (text.toString().length() > 0) {
            List<String> ingredients = new ArrayList<>(Arrays.asList(text.toString().split(System.lineSeparator())));
            return ingredients;
        }
        return new ArrayList<>();
    }

    public String getEditTextAsString(EditText input) {
        if (input.getText().toString().length() > 0)
            return input.getText().toString();
        return "";
    }

    public int getCookTime() {
        if (mCookTime.getText().toString().length()>0)
            return Integer.parseInt(mCookTime.getText().toString());
        return 0;
    }

    public String getTypeOfFood() {
        if (mCategory.getSelectedItem() != null)
            return mCategory.getSelectedItem().toString();
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewFlipper = null;
        viewBinder = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void configureSpinnerAdapter(List<String> categories) {
        mSpinnerAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        //mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(mSpinnerAdapter);
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initWidgets() {
        mViewFlipper = viewBinder.viewFlipperFoodImages;
        /**
         * Buttons
         */
        mCamera = viewBinder.cameraButton;
        mSaveButton = viewBinder.saveButton;
        mCancelButton = viewBinder.cancelButton;
        mAddnewTypeButton = viewBinder.addNewType;

        /**
         * Arrows
         */
        mLeftArrow = viewBinder.leftArrowChangePicture;
        mRightArrow = viewBinder.rightArrowChangePicture;

        /**
         * EditTexts
         */
        mRecipeName = viewBinder.recipeNameEditText;
        mCookTime = viewBinder.cookTimeEditText;
        mIngredients = viewBinder.ingredientsEditText;
        mSteps = viewBinder.recipeStepsEditText;
        mDescription = viewBinder.descriptionEditText;

        /**
         * Spinner
         */
        mCategory = viewBinder.spinnerTypeOfFood;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("window_category_add", IS_ADD_CATEGORY_WINDOW_OPEN);
        outState.putBoolean("window_image_remove", IS_REMOVE_IMAGE_WINDOW_OPEN);
    }

    private void takePicture() {

        try {
            currentFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (currentFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.laoa.myrecipe",
                    currentFile
            );
            startActivityCamera.launch(photoURI);
        }
    }

    // TODO: Generate callback
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);

         startActivityCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                 result -> {
                     if (result) {
                        mImageFlipperHandler.addViewPath(currentFile);
                     }
                 });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }


    private void popUpWindowRemovePicture() {
        IS_REMOVE_IMAGE_WINDOW_OPEN = true;
        View popUp = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window_remove, null);
        final PopupWindow popupWindow = new PopupWindow(
                popUp, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);

        Button remove = popUp.findViewById(R.id.remove_button_popUp);
        Button dismiss = popUp.findViewById(R.id.dismiss_popUp);

        dismiss.setOnClickListener(view1 ->{ popupWindow.dismiss(); IS_REMOVE_IMAGE_WINDOW_OPEN=false;});

        remove.setOnClickListener(view12 -> {
            mViewFlipper.removeView(mViewFlipper.getCurrentView());
            popupWindow.dismiss();
            IS_REMOVE_IMAGE_WINDOW_OPEN = false;
        });
    }

    private void popUpWindowAddNewCategory() {
        IS_ADD_CATEGORY_WINDOW_OPEN = true;
        View popUp = LayoutInflater.from(getActivity()).inflate(R.layout.add_new_type, null);
        final PopupWindow popupWindow = new PopupWindow(
                popUp, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(getView(), Gravity.CENTER,0,0);

        Button save = popUp.findViewById(R.id.save_new_category_button);
        Button dismiss = popUp.findViewById(R.id.dismiss_adding_category);

        EditText editText = popUp.findViewById(R.id.add_category_editText);

        dismiss.setOnClickListener(view1 -> {popupWindow.dismiss(); IS_ADD_CATEGORY_WINDOW_OPEN = false;});

        save.setOnClickListener(view12 -> {

            if (editText.getText().toString().trim().equals("")) {
                editText.setError("Please write Category name");
            }
            else {
                mRecipeManager.setCategory(editText.getText().toString());
                IS_ADD_CATEGORY_WINDOW_OPEN = false;
                popupWindow.dismiss();
            }

        });

    }


    private void removeImageListener() {

        mViewFlipper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                popUpWindowRemovePicture();
                return true;
            }
        });
    }

    private void addNewTypeListener() {

        mAddnewTypeButton.setOnClickListener(view -> {
            popUpWindowAddNewCategory();
        });

    }



    private void displayImage(int width, int height, File currentPhotoFile) {

        ImageView photo = new ImageView(getActivity());
        ImageScaler imageScaler = new ImageScaler(photo, width, height);

        imageScaler.setListener(new ImageScaler.Observer() {
            @Override
            public void update(ImageView photo) {
                if (photo!=null) {
                    mViewFlipper.addView(photo, currentPhotoFile);
                    imageScaler.removeListener(); // avoid memory leaks
                }
            }
        });
        imageScaler.execute(currentPhotoFile);
    }


}
