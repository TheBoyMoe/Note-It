package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.ui.fragment.VideoNoteFragment;

import java.io.File;

public class VideoNoteActivity extends AppCompatActivity
        implements VideoNoteFragment.Contract{

    private VideoNoteFragment mFragment;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, VideoNoteActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, long id, String title, String filePath, String thumbnailPath, String mimeType) {
        Intent intent = new Intent(activity, VideoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_THUMBNAIL_PATH, thumbnailPath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);

        mFragment = (VideoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            if (id > 0) {
                String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
                String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
                String thumbnailPath = getIntent().getStringExtra(Constants.ITEM_THUMBNAIL_PATH);
                String mimeType = getIntent().getStringExtra(Constants.ITEM_MIME_TYPE);
                mFragment = VideoNoteFragment.newInstance(id, title, filePath, thumbnailPath, mimeType);
            } else {
                mFragment = VideoNoteFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ITEM_VIDEO_NOTE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(Constants.ITEM_TITLE);
            String filePath = data.getStringExtra(Constants.ITEM_FILE_PATH);
            String mimeType = data.getStringExtra(Constants.ITEM_MIME_TYPE);

            // update fragment UI and display title and thumbnail
            if (mFragment != null) {
                mFragment.updateFragmentUI(title, filePath, mimeType);
            }
        }
    }

    // impl  contract methods
    @Override
    public void saveVideoNote(String title, String filePath, String thumbnailPath, String mimeType) {
        // save to database //FIXME
//        ContentValues values = Utils.setContentValuesVideoNote(
//                Utils.generateCustomId(),
//                Constants.ITEM_VIDEO_NOTE,
//                title, filePath, thumbnailPath, mimeType
//        );
//        new InsertItemThread(this, values).start();
        finish();
    }

    @Override
    public void updateVideoNote(long id, String title, String filePath, String thumbnailPath, String mimeType) {
        // FIXME
//        ContentValues values = Utils.setContentValuesVideoNote(
//            id, Constants.ITEM_VIDEO_NOTE, title, filePath, thumbnailPath, mimeType);
//        new UpdateItemThread(this, values).start();
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
