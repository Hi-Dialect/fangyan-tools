package com.example.fangyan;

import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

public class DataAccess {
    private static final String TAG = "DataAccess";
    private AppCompatActivity appCompatActivity;
    private SharedPreferences preferences;

    public DataAccess(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.preferences = appCompatActivity.getSharedPreferences("data", appCompatActivity.MODE_PRIVATE);
    }

    @JavascriptInterface
    public void saveStringData(String name, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    @JavascriptInterface
    public void saveIntegerData(String name, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    @JavascriptInterface
    public String fetchStringData(String name) {
        String value = preferences.getString(name, "");
        return value;
    }

    @JavascriptInterface
    public int fetchIntegerData(String name) {
        int value = preferences.getInt(name, -1);
        return value;
    }
}