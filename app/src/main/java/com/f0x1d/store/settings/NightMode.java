package com.f0x1d.store.settings;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({NightMode.DISABLE, NightMode.ENABLE, NightMode.AUTO})
@Retention(RetentionPolicy.SOURCE)
public @interface NightMode {
    int DISABLE = AppCompatDelegate.MODE_NIGHT_NO;
    int ENABLE = AppCompatDelegate.MODE_NIGHT_YES;
    int AUTO = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
    int FOLLOW_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
}
