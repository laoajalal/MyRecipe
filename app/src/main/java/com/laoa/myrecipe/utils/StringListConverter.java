package com.laoa.myrecipe.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter {

    @TypeConverter
    public String fromListToString(List<String> list) {
        Type type = new TypeToken<List<String>>() {}.getType();
        return new Gson().toJson(list, type);
    }

    @TypeConverter
    public List<String> fromJsonToList(String json) {
        Type type = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

}
