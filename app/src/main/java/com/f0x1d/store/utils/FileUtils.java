package com.f0x1d.store.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.f0x1d.store.App;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static void openImage(String str, int i, Fragment fragment) {
        Intent intent;
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.setType(str);
        intent2.addCategory("android.intent.category.OPENABLE");
        Intent intent3 = new Intent("com.f0x1d.store.main.PICK_DATA");
        intent3.putExtra("CONTENT_TYPE", str);
        intent3.addCategory("android.intent.category.DEFAULT");
        if (fragment.requireContext().getPackageManager().resolveActivity(intent3, 0) != null) {
            intent = Intent.createChooser(intent3, "Open file");
            intent.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{intent2});
        } else {
            intent = Intent.createChooser(intent2, "Open file");
        }
        try {
            fragment.startActivityForResult(intent, i);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(fragment.requireContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1048576];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    public static String getFileName(Uri uri) {
        String str = null;
        if (uri.getScheme().equals("content")) {
            Cursor query = App.getInstance().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        str = query.getString(query.getColumnIndex("_display_name"));
                    }
                } catch (Throwable th) {
                    query.close();
                    throw th;
                }
            }
            query.close();
        }
        if (str != null) {
            return str;
        }
        String path = uri.getPath();
        int lastIndexOf = path.lastIndexOf(47);
        return lastIndexOf != -1 ? path.substring(lastIndexOf + 1) : path;
    }
}
