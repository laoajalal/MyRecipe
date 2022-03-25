package com.laoa.myrecipe.recipeDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.laoa.myrecipe.models.Recipe;
import com.laoa.myrecipe.utils.StringListConverter;

@Database(entities = {Recipe.class}, version = 2, exportSchema = true)
@TypeConverters(StringListConverter.class)
public abstract class RecipeDB extends RoomDatabase {

    private static RecipeDB Instance;

    public abstract RecipeDao recipeDao();

    public static RecipeDB getDataBase(Context context) {
        if (Instance == null) {
            synchronized (RecipeDB.class) {
                Instance = Room.databaseBuilder(context.getApplicationContext(), RecipeDB.class, "recipeDatabase")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
                return Instance;
            }
        }
        return Instance;
    }

}
