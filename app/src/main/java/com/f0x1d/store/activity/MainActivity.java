package com.f0x1d.store.activity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.f0x1d.store.R;
import com.f0x1d.store.fragment.ListFragment;
import com.f0x1d.store.settings.Settings;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private boolean toggle1 = false;
    private boolean toggle2 = false;
    private boolean toggle3 = false;

    public void onCreate(Bundle bundle) {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
        if (Build.VERSION.SDK_INT < 27) {
            getWindow().setNavigationBarColor(ViewCompat.MEASURED_STATE_MASK);
        }
        if (Build.VERSION.SDK_INT < 23) {
            getWindow().setStatusBarColor(-7829368);
        }
        getDelegate().applyDayNight();
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        MaterialButton low = findViewById(R.id.low);
        MaterialButton medium = findViewById(R.id.medium);
        MaterialButton height = findViewById(R.id.height);

        low.setText(getString(R.string.low, Settings.get().getLowCount()));
        medium.setText(getString(R.string.medium, Settings.get().getMediumCount()));
        height.setText(getString(R.string.height, Settings.get().getHeightCount()));

        low.setOnLongClickListener(v -> {
            toggle1 = !toggle1;
            return true;
        });

        medium.setOnLongClickListener(v -> {
            toggle2 = !toggle2;
            return true;
        });

        height.setOnLongClickListener(v -> {
            toggle3 = !toggle3;
            return true;
        });

        low.setOnClickListener(v -> {
            int count = Settings.get().getLowCount();
            if (!toggle1) {
                count--;
            } else {
                count++;
            }
            if (count < 0) {
                count = 0;
            }
            low.setText(getString(R.string.low, count));
            Settings.get().setLowCount(count);
        });

        medium.setOnClickListener(v -> {
            int count = Settings.get().getMediumCount();
            if (!toggle2) {
                count--;
            } else {
                count++;
            }
            if (count < 0) {
                count = 0;
            }
            medium.setText(getString(R.string.medium, count));
            Settings.get().setMediumCount(count);
        });

        height.setOnClickListener(v -> {
            int count = Settings.get().getHeightCount();
            if (!toggle3) {
                count--;
            } else {
                count++;
            }
            if (count < 0) {
                count = 0;
            }
            height.setText(getString(R.string.height, count));
            Settings.get().setHeightCount(count);
        });

        replaceFragment(ListFragment.newInstance(-1, getString(R.string.root)), "main_list", false, null);
    }

    public void replaceFragment(Fragment fragment, String str, boolean z, String str2) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
        beginTransaction.replace(R.id.main_container, fragment, str);
        if (z) {
            beginTransaction.addToBackStack(str2);
        }
        beginTransaction.commit();
    }

    public void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
