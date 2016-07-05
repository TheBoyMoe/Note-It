package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.UpdateItemThread;
import com.example.demoapp.ui.fragment.VideoNoteFragment;

import java.io.File;

import timber.log.Timber;

public class VideoNoteActivity extends AppCompatActivity
        implements VideoNoteFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VideoNoteActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, long id, String title,
                 String description, String filePath, String thumbnailPath, String mimeType) {

        Intent intent = new Intent(activity, VideoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_THUMBNAIL_PATH, thumbnailPath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);


        VideoNoteFragment fragment = (VideoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
            String description = getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
            String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
            String thumbnailPath = getIntent().getStringExtra(Constants.ITEM_THUMBNAIL_PATH);
            String mimeType = getIntent().getStringExtra(Constants.ITEM_MIME_TYPE);
            fragment = VideoNoteFragment.newInstance(id, title, description, filePath, thumbnailPath, mimeType);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void updateVideoNote(long id, String title, String description) {
        // only changes to the title or description are saved
        Timber.i("%s: update audio note", Constants.LOG_TAG);
        ContentValues values = Utils.updateContentValues(id, title,  description);
        new UpdateItemThread(this, values).start();
        finish();
    }


    @Override
    public void playVideo(String filePath, String mimeType) {
        // play video onClick
        if(filePath != null && mimeType != null) {
            Uri video = Uri.fromFile(new File(filePath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(video, mimeType);
            startActivity(intent);
        } else {
            Utils.showToast(this, "Error, video file not found");
        }
    }

    @Override
    public void quit() {
        finish();
    }


}
