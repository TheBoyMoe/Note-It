package com.example.demoapp.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.InsertItemThread;
import com.example.demoapp.ui.fragment.MainActivityFragment;
import com.example.demoapp.ui.fragment.ModelFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Contract, View.OnClickListener{

    private static final String FILE_PATH = "file_path_uri";
    private static final String MODEL_FRAGMENT = "model_fragment";
    private FloatingActionsMenu mBtnTrigger;
    private CoordinatorLayout mLayout;
    private String mFullSizePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // cache a reference to a fragment
        MainActivityFragment recyclerFragment =
            (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (recyclerFragment == null) {
            recyclerFragment = MainActivityFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, recyclerFragment)
                    .commit();
        }

        // cache a reference to the model fragment
        Fragment modelFragment = getSupportFragmentManager().findFragmentByTag(MODEL_FRAGMENT);
        if (modelFragment == null) {
            modelFragment = ModelFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(modelFragment, MODEL_FRAGMENT)
                    .commit();
        }

        if (savedInstanceState != null) {
            mFullSizePath = savedInstanceState.getString(FILE_PATH);
        }

        // button setup
        mBtnTrigger = (FloatingActionsMenu) findViewById(R.id.button_trigger);

        FloatingActionButton textNoteBtn = (FloatingActionButton) findViewById(R.id.action_text_btn);
        if(textNoteBtn != null) {
            textNoteBtn.setOnClickListener(this);
            textNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_text_btn), R.color.half_black));
        }

        FloatingActionButton videoNoteBtn = (FloatingActionButton) findViewById(R.id.action_video_btn);
        if (videoNoteBtn != null) {
            videoNoteBtn.setOnClickListener(this);
            videoNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_video_btn), R.color.half_black));
        }

        FloatingActionButton audioNoteBtn = (FloatingActionButton) findViewById(R.id.action_audio_btn);
        if (audioNoteBtn != null) {
            audioNoteBtn.setOnClickListener(this);
            audioNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_audio_btn), R.color.half_black));
        }

        FloatingActionButton photoNoteBtn = (FloatingActionButton) findViewById(R.id.action_photo_btn);
        if (photoNoteBtn != null) {
            photoNoteBtn.setOnClickListener(this);
            photoNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_photo_btn), R.color.half_black));
        }

    }

    // contract methods
    @Override
    public void onNoteItemClick(long id, String title, String description) {
        // launch activity displaying text note
        TextNoteActivity.launch(MainActivity.this, id, title, description);
    }

    @Override
    public void onVideoItemClick(long id, String filePath, String previewPath, String mimeType) {
        // launch activity displaying video note
        VideoNoteActivity.launch(MainActivity.this, id, filePath, previewPath, mimeType);
    }

    @Override
    public void onPhotoItemClick(long id, String previewPath) {
        // launch activity to display photo
        PhotoNoteActivity.launch(this, id, previewPath);
    }

    @Override
    public void onAudioItemClick(long id, String title, String description, String filePath) {
        // launch activity to display the audio note
        AudioNoteActivity.launch(MainActivity.this, id, title, description, filePath);
    }

    // handle fab button clicks
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_text_btn:
                // launch text note activity
                TextNoteActivity.launch(MainActivity.this);
                break;
            case R.id.action_video_btn:
                // launch 3rd party video recording app
                if (Utils.hasCamera(MainActivity.this)) {
                    Uri fileUri = Utils.generateMediaFileUri(Constants.ITEM_TYPE_VIDEO);
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, Constants.VIDEO_REQUEST_CODE);
                    } else {
                        Utils.showSnackbar(mLayout, "No app found suitable to record video");
                    }
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording video");
                }
                break;
            case R.id.action_audio_btn:
                // launch audio recording
                if(Utils.hasMicrophone(MainActivity.this)) {
                    AudioRecorderActivity.launch(MainActivity.this);
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording audio");
                }
                break;
            case R.id.action_photo_btn:
                if (Utils.hasCamera(MainActivity.this)) {
                    // launch 3rd party photo app
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri filePathUri = Utils.generateMediaFileUri(Constants.ITEM_TYPE_PHOTO);
                    if (filePathUri != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePathUri);
                        mFullSizePath = filePathUri.toString().substring(7);
                        if (Utils.isAppInstalled(this, intent)) {
                            startActivityForResult(intent, Constants.PHOTO_REQUEST_CODE);
                        } else {
                            Utils.showSnackbar(mLayout, "No app found suitable to capture photos");
                        }
                    }
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording video");
                }
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // collapse the btn menu if req'd
        if (mBtnTrigger.isExpanded()) {
            mBtnTrigger.collapse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.AUDIO_REQUEST_CODE) {
                String filePath = data.getStringExtra(Constants.ITEM_FILE_PATH);
                String mimeType = data.getStringExtra(Constants.ITEM_MIME_TYPE);

                // insert item into database
                ContentValues values = Utils.setContentValuesAudioNote(
                        Utils.generateCustomId(),
                        Constants.ITEM_TYPE_AUDIO,
                        "", "", filePath, mimeType); // use empty string for title and description
                new InsertItemThread(this, values).start();
            } else if (requestCode == Constants.VIDEO_REQUEST_CODE) {

                Uri videoUri = data.getData();
                String filePath = videoUri.toString().substring(7);

                // generate thumbnailPath
                String thumbnailPath = Utils.generateImagePathFromVideo(MainActivity.this,
                        filePath, MediaStore.Video.Thumbnails.MINI_KIND);

                // generate full screen preview image
                String previewPath = Utils.generateImagePathFromVideo(MainActivity.this,
                        filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                // insert video item into database
                ContentValues values = Utils.setContentValuesMediaNote(
                        Utils.generateCustomId(),
                        Constants.ITEM_TYPE_VIDEO,
                        "", "", filePath, previewPath, thumbnailPath,
                        Constants.VIDEO_MIMETYPE);
                new InsertItemThread(this, values).start();

            } else  if (requestCode == Constants.PHOTO_REQUEST_CODE) {

                // generate thumbnail
                String thumbnailPath = Utils.generatePreviewImage(mFullSizePath, 200, 200);
                // generate preview maintaining aspect ratio
                String previewPath = Utils.generateScaledPreviewImage(mFullSizePath, 1024, 1024);

                // insert photo item into database
                ContentValues values = Utils.setContentValuesMediaNote(
                        Utils.generateCustomId(),
                        Constants.ITEM_TYPE_PHOTO,
                        "", "", mFullSizePath, previewPath, thumbnailPath, // save references to preview/thumbnail to database
                        Constants.PHOTO_MIMETYPE);
                new InsertItemThread(this, values).start();
            }
        }
        else if(resultCode == RESULT_CANCELED){
            Utils.showSnackbar(mLayout, "Operation cancelled by user");
        } else {
            Utils.showSnackbar(mLayout, "Error executing operation");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILE_PATH, mFullSizePath);
    }
}
