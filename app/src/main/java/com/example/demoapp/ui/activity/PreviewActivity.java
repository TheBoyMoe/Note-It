package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.UpdateItemThread;
import com.example.demoapp.ui.fragment.PreviewFragment;

import java.io.File;

public class PreviewActivity extends AppCompatActivity
        implements PreviewFragment.Contract{

    private CoordinatorLayout mLayout;
    private FloatingActionButton mFab;
    private String mFilePath;
    private String mMimeType;

    public static void launch(Activity activity, long id, String title,
              String description, String filePath, String previewPath, String mimeType) {

        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_PREVIEW_PATH, previewPath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout );
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        //mFab = (FloatingActionButton) findViewById(R.id.fab);

        PreviewFragment fragment =
            (PreviewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = PreviewFragment.newInstance(
                    getIntent().getLongExtra(Constants.ITEM_ID, 0),
                    getIntent().getStringExtra(Constants.ITEM_TITLE),
                    getIntent().getStringExtra(Constants.ITEM_DESCRIPTION),
                    getIntent().getStringExtra(Constants.ITEM_FILE_PATH),
                    getIntent().getStringExtra(Constants.ITEM_PREVIEW_PATH),
                    getIntent().getStringExtra(Constants.ITEM_MIME_TYPE)
            );

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


    // impl contract methods
    @Override
    public void playVideo(long id, String filePath, String mimeType ) {
//        mMimeType = mimeType;
//        mFilePath = filePath;
//        if (mMimeType.equals(Constants.VIDEO_MIMETYPE)) {
//            mFab.setVisibility(View.VISIBLE);
//            mFab.setOnClickListener(this);
//        }

        if(filePath != null && mimeType != null) {
            Uri video = Uri.fromFile(new File(filePath));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(video, mimeType);
            if (Utils.isAppInstalled(this, intent)) {
                startActivity(intent);
            } else {
                Utils.showSnackbar(mLayout, "No suitable app found to play video");
            }
        } else {
            Utils.showSnackbar(mLayout, "Error, video file not found");
        }
    }

    @Override
    public void update(long id, String title, String description) {
        // update note in the database
        ContentValues values = Utils.updateContentValues(id, title, description);
        new UpdateItemThread(this, values).start();
        finish();
    }

    @Override
    public void delete(long id) {
        Utils.deleteItemFromDevice(this, id);
    }

    @Override
    public void quit() {
        finish();
    }


}
