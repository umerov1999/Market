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
        this.app = context.getApplicationContext();
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

    @AppCompatDelegate.NightMode
    public int getNightMode() {
        String mode = PreferenceManager.getDefaultSharedPreferences(app)
                .getString("night_switch", String.valueOf(NightMode.ENABLE));
        return Integer.parseInt(mode);
    }
}