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

public class MainActivity extends AppCompatActivity {
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
