package com.example.demoapp.common;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.demoapp.event.ModelLoadedEvent;
import com.example.demoapp.model.DatabaseHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    // hide the keyboard on executing search
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarSticky(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
    }

    // Check that a network connection is available
    public  static boolean isClientConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Long generateCustomId() {
        // define an id based on the date time stamp
        Locale locale = new Locale("en_US");
        Locale.setDefault(locale);
        String pattern = "yyyyMMddHHmmssSS"; // pattern used to sort objects
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());

        return Long.valueOf(formatter.format(new Date()));
    }


    public static Bitmap generateBitmap(String path) {
        return ThumbnailUtils.createVideoThumbnail(new File(path).getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
    }

    public static void queryAllItems(Context context) {
        try {
            Cursor results = DatabaseHelper.getInstance(context).loadItems(context);
            EventBus.getDefault().postSticky(new ModelLoadedEvent(results));
        } catch (Exception e) {
            Timber.e("%s: error loading items from dbase, %s", Constants.LOG_TAG, e.getMessage());
        }
    }

    public static ContentValues setContentValues(long id, String title, String description) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.ITEM_ID, id);
        cv.put(Constants.ITEM_TITLE, title);
        cv.put(Constants.ITEM_DESCRIPTION, description);
        return cv;
    }

}
