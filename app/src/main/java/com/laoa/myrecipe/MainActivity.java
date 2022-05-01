package com.laoa.myrecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Application is built on the principle of having 1 main activity with multiple fragments.
 * */
public class MainActivity extends AppCompatActivity implements ActionBarTitleSetter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setTitleActionBar(String title) {

        this.setTitle(title);
    }

}

