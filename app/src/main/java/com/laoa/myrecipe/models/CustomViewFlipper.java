package com.laoa.myrecipe.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Custom ViewFlipper class that adds a way for the ImageFlipperHandler to communicate with the ViewFlipper.
 * This helps when saving,removing or modifying images inside the viewFlipper.
 * */
public class CustomViewFlipper extends ViewFlipper {

        private ChangeView mChangeView;
        private Map<File, View> mapOfPhotos;


        public CustomViewFlipper(Context context, AttributeSet attrs) {
            super(context, attrs);
            mapOfPhotos = new HashMap<>();
        }
        public void setChangeViewListener(ChangeView changeView) {
            mChangeView = changeView;
        }

        @Override
        public void showNext() {
            super.showNext();
            mChangeView.setNext();
        }

        @Override
        public void showPrevious() {
            super.showPrevious();
            mChangeView.setPrevious();
        }



    @Override
    public void removeView(View view) {
        int index = indexOfChild(view);
        mChangeView.removeItemAt(index);
        super.removeView(view);
    }

    public boolean alreadyDisplayed(File path) {
            if (mapOfPhotos.containsKey(path)) {
                return true;
            }
            return false;
    }

    public void addView(View child, File path) {
        int pos = mChangeView.getPathPos(path);
        super.addView(child, pos);
        mChangeView.addViewPath(path);
        mapOfPhotos.put(path, child);
    }

}
