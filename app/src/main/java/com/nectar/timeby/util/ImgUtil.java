package com.nectar.timeby.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by finalize on 7/21/15.
 */
public class ImgUtil {
    private static final String TAG = "ImgUtil";
    private static final String ALBUM_NAME = "headimg";

    public static void saveHeadBitmap(Context context, String fileName, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
           Log.w(TAG, e.getMessage());
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
           Log.w(TAG, e.getMessage());
        }
    }

    public static Bitmap readBitmap(Context context, String filename) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(filename);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.getMessage());
        }

        return BitmapFactory.decodeStream(fileInputStream);
    }

    public static String getHeadImgFileName(String userName) {
        return userName + ".jpg";
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
