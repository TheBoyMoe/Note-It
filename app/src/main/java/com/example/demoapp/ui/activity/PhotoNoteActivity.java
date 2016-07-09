package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;
import com.example.demoapp.ui.fragment.PhotoNoteFragment;

import timber.log.Timber;

public class PhotoNoteActivity extends AppCompatActivity
        implements PhotoNoteFragment.Contract{


    public static void launch(Activity activity, long id, String title, String description, String filePath, String mimeType) {
        Intent intent = new Intent(activity, PhotoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        PhotoNoteFragment fragment = (PhotoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
            String description = getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
            String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
            String mimeType = getIntent().getStringExtra(Constants.ITEM_MIME_TYPE);
            fragment = PhotoNoteFragment.newInstance(id, title, description, filePath, mimeType);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void displayPhotoInfo(long id) {
        // query the database to retrieve most upto date info
        new LoadItemTask().execute(id);
    }

    @Override
    public void delete(long id) {
        Utils.deleteItemFromDevice(this, id);
    }

    public class LoadItemTask extends AsyncTask<Long, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Long... params) {
            Timber.i("%s: Execute database query");
            Long itemId = params[0];
            return DatabaseHelper.getInstance(PhotoNoteActivity.this).loadItem(PhotoNoteActivity.this, itemId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (cursor != null) {
                // query the database and launch the info activity
                cursor.moveToFirst();
                long id = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));
                String title = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
                Timber.i("%s: title: %s, description: %s", Constants.LOG_TAG, title, description);
                InfoNoteActivity.launch(PhotoNoteActivity.this, id, title, description);
                cursor.close();
            }
        }

    }


//    @Override
//    public void displayPhoto(String filePath, String mimeType) {
//        if (filePath != null && mimeType != null) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse("file://" + filePath), mimeType);
//            if (Utils.isAppInstalled(this, intent)) {
//                startActivity(intent);
//            } else {
//                Utils.showSnackbar(mLayout, "No suitable app found to display image");
//            }
//        } else {
//            Utils.showSnackbar(mLayout, "Error, file not found");
//        }
//    }


}
