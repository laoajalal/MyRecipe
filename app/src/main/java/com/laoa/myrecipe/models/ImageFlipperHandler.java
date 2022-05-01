package com.laoa.myrecipe.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A class used to handle tracking of the images inside the viewFlipper.
 * */
public class ImageFlipperHandler extends ViewModel implements ChangeView{

    private List<File> mImagesInsideViewFlipper;
    private MutableLiveData<File> mCurrentImagePath;
    private SavedStateHandle mSavedStateHandle;

    int currentSize = 0;

    public ImageFlipperHandler(SavedStateHandle savedStateHandle) {

            mImagesInsideViewFlipper = new ArrayList<>();
            mCurrentImagePath = new MutableLiveData<>();
            mSavedStateHandle = savedStateHandle;
            getState(savedStateHandle);
    }


    public void getState(SavedStateHandle savedStateHandle) {
        System.out.println("updating state");
        if (savedStateHandle!=null) {
            if (savedStateHandle.get("paths") != null) {
                for (String path : (ArrayList<String>) savedStateHandle.get("paths")) {
                    mImagesInsideViewFlipper.add(new File(path));
                }
            }
            if (savedStateHandle.get("current_photo") != null)
                mCurrentImagePath.setValue(new File( (String) savedStateHandle.get("current_photo")));
            if (savedStateHandle.get("size") != null)
                currentSize = savedStateHandle.get("size");
            else
                currentSize = 0;
        }

    }

    /**
     * Sets the state before a configuration occurs. This should be called inside the fragment/activity's
     * onSaveInstanceState.
     * */
    public void setState() {
        if (mSavedStateHandle != null) {
            System.out.println("savedstatehandle not null");
            ArrayList<String> temp = new ArrayList();
            for (File path : mImagesInsideViewFlipper) {
                System.out.println("FILE PATH: " + path.toString());
                temp.add(path.toString());
            }
            mSavedStateHandle.set("paths", temp);
            if (mCurrentImagePath.getValue() != null)
                mSavedStateHandle.set("current_photo", mCurrentImagePath.getValue().toString());

            mSavedStateHandle.set("size", currentSize);
        }

    }


    /**
     * Reset the list of paths that should be correlated to the viewFlipper.
     * */
    public void reset() {
        mImagesInsideViewFlipper.clear();
    }


    public List<String> getImagesAbsolutePaths() {
        List<String> absPath = new ArrayList<>();

        for (File imageFile: mImagesInsideViewFlipper) {
            absPath.add(imageFile.getAbsolutePath());
        }
        return absPath;
    }

    public List<File> getFilePathsImages() {
        return mImagesInsideViewFlipper;
    }

    /**
     * Sets the next currentImage
     * */
    @Override
    public void setNext() {
        if (mImagesInsideViewFlipper.size()>0 && mCurrentImagePath != null) {
            int nextPos = mImagesInsideViewFlipper.indexOf(mCurrentImagePath) + 1;
            if (nextPos >= mImagesInsideViewFlipper.size()) {
                nextPos = 0;
            }
            mCurrentImagePath.setValue(mImagesInsideViewFlipper.get(nextPos));
        }
    }

    @Override
    public void setPrevious() {
        if (mImagesInsideViewFlipper.size()>0 && mCurrentImagePath != null) {
            int nextPos = mImagesInsideViewFlipper.indexOf(mCurrentImagePath) - 1;
            if (nextPos < 0) {
                nextPos = mImagesInsideViewFlipper.size() - 1;
            }
            mCurrentImagePath.setValue(mImagesInsideViewFlipper.get(nextPos));
        }

    }

    @Override
    public void removeItemAt(int pos) {
        mImagesInsideViewFlipper.remove(pos);
        currentSize--;
    }

    @Override
    public void addViewPath(File path) {
        if (mImagesInsideViewFlipper != null)
        {
            if (!mImagesInsideViewFlipper.contains(path))
                mImagesInsideViewFlipper.add(path);
            currentSize++;
        }
    }

    @Override
    public int getPathPos(File path) {
        return mImagesInsideViewFlipper.indexOf(path);
    }

    @Override
    public int getPos() {
        return currentSize;
    }

}
