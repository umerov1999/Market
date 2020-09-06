package com.f0x1d.store;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.developer.crashx.config.CrashConfig;
import com.f0x1d.store.db.Database;
import com.f0x1d.store.settings.Settings;

public class App extends Application {
    private static App instance;
    private Database database;

    public static SharedPreferences getDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(instance);
    }

    @NonNull
    public static App getInstance() {
        if (instance == null) {
            throw new IllegalStateException("App instance is null!!! WTF???");
        }

        return instance;
    }

    public void onCreate() {
        instance = this;
        AppCompatDelegate.setDefaultNightMode(Settings.get().getNightMode());
        super.onCreate();

        CrashConfig.Builder.create()
                .apply();

        //this.database = Room.databaseBuilder(this, Database.class, "main_database.db").addMigrations(Database.MIGRATION_1_2).allowMainThreadQueries().build();
        database = Room.databaseBuilder(this, Database.class, "main_database.db").allowMainThreadQueries().build();
    }

    public Database getDatabase() {
        return database;
    }
}
