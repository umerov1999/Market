package com.f0x1d.store.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.f0x1d.store.App;

public class Settings {
    private static Settings instance;
    private final Context app;

    public Settings(Context context) {
        app = context.getApplicationContext();
    }

    @NonNull
    public static Settings get() {
        if (instance == null) {
            instance = new Settings(App.getInstance());
            return instance;
        }
        return instance;
    }

    public boolean isDarkModeEnabled(Context context) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public void switchNightMode(@AppCompatDelegate.NightMode int key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        preferences.edit().putString("night_switch", String.valueOf(key)).apply();
    }

    public int getLowCount() {
        return PreferenceManager.getDefaultSharedPreferences(app)
                .getInt("low_packet", 0);
    }

    public void setLowCount(int vl) {
        PreferenceManager.getDefaultSharedPreferences(app).edit()
                .putInt("low_packet", vl).apply();
    }

    public int getMediumCount() {
        return PreferenceManager.getDefaultSharedPreferences(app)
                .getInt("medium_packet", 0);
    }

    public void setMediumCount(int vl) {
        PreferenceManager.getDefaultSharedPreferences(app).edit()
                .putInt("medium_packet", vl).apply();
    }

    public int getHeightCount() {
        return PreferenceManager.getDefaultSharedPreferences(app)
                .getInt("height_packet", 0);
    }

    public void setHeightCount(int vl) {
        PreferenceManager.getDefaultSharedPreferences(app).edit()
                .putInt("height_packet", vl).apply();
    }

    @AppCompatDelegate.NightMode
    public int getNightMode() {
        String mode = PreferenceManager.getDefaultSharedPreferences(app)
                .getString("night_switch", String.valueOf(NightMode.ENABLE));
        return Integer.parseInt(mode);
    }
}