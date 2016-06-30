package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.ui.fragment.VideoNoteFragment;

import timber.log.Timber;

public class VideoNoteActivity extends AppCompatActivity
        implements VideoNoteFragment.Contract{

    private VideoNoteFragment mFragment;
    private String mTitle;
    private String mFilePath;
    private String mMimeType;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VideoNoteActivity.class);
        activity.startActivity(intent);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        mFragment = (VideoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = VideoNoteFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ITEM_VIDEO && resultCode == RESULT_OK) {
            String title = data.getStringExtra(Constants.ITEM_TITLE);
            mFilePath = data.getStringExtra(Constants.ITEM_FILE_PATH);
            mMimeType = data.getStringExtra(Constants.ITEM_MIME_TYPE);
            Timber.i("%s: title: %s, filePath: %s, mimeType: %s", Constants.LOG_TAG,
                    title, mFilePath, mMimeType);

            // update fragment UI and display title and thumbnail
            if (mFragment != null) {
                mFragment.updateFragmentUI(title, mFilePath);
            }
        }
    }

    // impl  contract methods
    @Override
    public void saveVideoNote() {

    }

    @Override
    public void updateVideoNote() {

    }

    @Override
    public void playVideo() {
        // TODO
        Utils.showToast(this, "click to play");
    }

    @Override
    public void selectVideo() {
        // check if there are any videos on the device
        CursorLoader loader = new CursorLoader(
                this,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Video.Media.TITLE);
        Cursor cursor = loader.loadInBackground();
        if (cursor.getCount() > 0)
            // launch VideoListActivity using startActivityForResult
            VideoListActivity.launch(this);
        else
            Utils.showToast(this, "No videos found on device");
    }

    @Override
    public void quit() {
        finish();
    }
}
