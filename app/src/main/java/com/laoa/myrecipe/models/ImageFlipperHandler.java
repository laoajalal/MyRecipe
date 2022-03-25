package com.laoa.myrecipe.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageFlipperHandler extends ViewModel implements ChangeView{

    private List<File> mImagesInsideViewFlipper;
    private MutableLiveData<File> mCurrentImagePath;
    private SavedStateHandle mSavedStateHandle;

    int currentSize = 0;

    public ImageFlipperHandler(SavedStateHandle savedStateHandle) {
        mImagesInsideViewFlipper = new ArrayList<>();
        mCurrentImagePath = new MutableLiveData<>();

        if (savedStateHandle == null) {
            //TODO:FIX APPROPRIATE
        }
        else {
            mSavedStateHandle = savedStateHandle;
            getState(mSavedStateHandle);
        }

    }

    private void getState(SavedStateHandle savedStateHandle) {

        if (savedStateHandle!=null) {
            //mImagesInsideViewFlipper = mSavedStateHandle.get("current_Map");

        }
    }

    public void reset() {
        mImagesInsideViewFlipper.clear();
    }

    /**
     * Sets the state before a configuration occurs. This should be called inside the fragment/activity's
     * onSaveInstanceState.
     * */
    public void setState() {
        if (mSavedStateHandle!=null) {
            mSavedStateHandle.set("current_map", mImagesInsideViewFlipper);
            mSavedStateHandle.set("current_path", mCurrentImagePath);
        }
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

    public MutableLiveData<File> getCurrentFileLiveData() {
        return mCurrentImagePath;
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
