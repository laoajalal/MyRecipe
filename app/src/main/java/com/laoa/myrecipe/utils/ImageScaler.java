package com.laoa.myrecipe.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;

//import android.media.ExifInterface;


/**
 * Utility class provided in lecture slides, used to properly scale image asynchronous.
 * */
public class ImageScaler extends AsyncTask<File, Void, Bitmap> {

    private ImageView view;
    private int height;
    private int width;

    private Observer mObserver;

    private boolean hasExecutedBackground = false;

    public ImageScaler(ImageView view, int width, int height) {
        this.view=view;
        this.width = width;
        this.height=height;
    }


    public void setListener(Observer observer) {
        mObserver = observer;
    }
    public void removeListener() {
        mObserver = null;
    }

    protected void onPreExecute() {
        hasExecutedBackground = false;
    }

    private int getExifRotation(File imageFile) {
        if (imageFile == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile);
            ;
            // We only recognize a subset of orientation tag values
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    protected Bitmap doInBackground(File... file) {
        //Läs in bilden som nu bör finnas där vi sa att den skulle placeras
        Bitmap bm= BitmapFactory.decodeFile(file[0].getAbsolutePath());
        Bitmap rotatedBitmap;
        int orientation = getExifRotation(file[0]);
        Matrix matrix = new Matrix();
        if(orientation!=0) {
            matrix.postRotate(orientation);
            rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            bm = null;
        }
        else {
            rotatedBitmap=bm;
        }

        //Skala om bilden så att den passar i imageviewn. Observera att getWidth och  getHeight
        //ej kommer ge korrekta värden förrän från onResume
        if (rotatedBitmap != null)
            return Bitmap.createScaledBitmap(rotatedBitmap,  width, height,true);
        else
            return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null)
        {
            view = null;
        }
        else
            view.setImageBitmap(bitmap);
        mObserver.update(view);
    }

    public interface Observer {
        public void update(ImageView photo);
    }


    }
