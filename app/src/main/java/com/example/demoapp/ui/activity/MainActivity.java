package com.example.demoapp.ui.activity;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import java.util.ArrayList;

import timber.log.Timber;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * References:
 * [1] Busy coder's guide to Android development - chapter on runtime permissions support
 */
public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Contract, View.OnClickListener{

    private static final String IS_FIRST_TIME_IN = "is_first_time_in";
    private static final String FILE_PATH = "file_path_uri";
    private static final String MODEL_FRAGMENT = "model_fragment";
    private static final int RESULT_PERMS_INITIAL = 101;
    private static final int RESULT_PERMISSION_TAKE_PICTURE = 102;
    private static final int RESULT_PERMISSION_RECORD_VIDEO = 103;
    private static final int RESULT_PERMISSION_RECORD_AUDIO = 104;

    private FloatingActionsMenu mBtnTrigger;
    private CoordinatorLayout mLayout;
    private String mFullSizePath;
    private SharedPreferences mPrefs;

    // string array of the req'd permissions
    private static final String[] ALL_REQUIRED_PERMS = {
            WRITE_EXTERNAL_STORAGE,
            RECORD_AUDIO
    };
    private static final String[] PERMS_REQUIRED_SAVE_MEDIA = {
            WRITE_EXTERNAL_STORAGE
    };


    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FIXME check denying WRITE_EXTERNAL_STORAGE before loading adapter -
        // stops reading audio/video/photos from disk

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
        actionButtonSetup();

        if (isFirstTimeIn()) {
            // seek WRITE_EXTERNAL_STORAGE first time app is run
            // CAMERA permission not required since we're relying on 3rd party app
//            ActivityCompat.requestPermissions(this, PERMS_REQUIRED_SAVE_MEDIA, RESULT_PERMS_INITIAL);

            // check that we have both WRITE_EXTERNAL_STORAGE and RECORD_AUDIO permissions
            ActivityCompat.requestPermissions(this, ALL_REQUIRED_PERMS, RESULT_PERMS_INITIAL);
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
                    recordVideo();
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording video");
                }
                break;
            case R.id.action_audio_btn:
                // launch audio recording
                if(Utils.hasMicrophone(MainActivity.this)) {
                    recordAudio();
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording audio");
                }
                break;
            case R.id.action_photo_btn:
                if (Utils.hasCamera(MainActivity.this)) {
                    // launch 3rd party photo app
                    takePicture();
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support taking photos");
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

                // generate filePath
                Uri videoUri = data.getData();
                Timber.i("%s: videoUri: %s", Constants.LOG_TAG, videoUri);
                String str = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    str = "/media";
                } else {
                    str = "/storage";
                }
                int position = videoUri.toString().indexOf(str);
                String filePath = videoUri.toString().substring(position); // FIXME
                Timber.i("%s: filePath: %s", Constants.LOG_TAG, filePath);


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
                String thumbnailPath = Utils.generatePreviewImage(mFullSizePath, 300, 300);
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // TODO
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // method called back as a result of requestPermissions()
        boolean permissionNotGiven = false;

        if (requestCode == RESULT_PERMISSION_TAKE_PICTURE) {
            if (canWriteToExternalStorage()) {
                launchCameraApp();
            } else if (!shouldShowWriteToStorageRational()) {
                permissionNotGiven = true;
            }
        }
        else if (requestCode == RESULT_PERMISSION_RECORD_VIDEO) {
            if (canRecordMedia()) {
                launchVideoApp();
            } else if (!shouldShowRecordMediaRational()) {
                permissionNotGiven = true;
            }
        }
        else if (requestCode == RESULT_PERMISSION_RECORD_AUDIO) {
            if (canRecordMedia()) {
                launchAudioApp();
            } else if (!shouldShowRecordMediaRational()) {
                permissionNotGiven = true;
            }
        }

        if (permissionNotGiven) {
            // show message to user
           Utils.showSnackbar(mLayout, getString(R.string.permission_not_given_message));
        }

    }



    // HELPER METHODS
    private void actionButtonSetup() {
        FloatingActionButton textNoteBtn = (FloatingActionButton) findViewById(R.id.action_text_btn);
        FloatingActionButton videoNoteBtn = (FloatingActionButton) findViewById(R.id.action_video_btn);
        FloatingActionButton audioNoteBtn = (FloatingActionButton) findViewById(R.id.action_audio_btn);
        FloatingActionButton photoNoteBtn = (FloatingActionButton) findViewById(R.id.action_photo_btn);

        setUpActionButton(textNoteBtn, R.drawable.action_text_btn);
        setUpActionButton(videoNoteBtn, R.drawable.action_video_btn);
        setUpActionButton(audioNoteBtn, R.drawable.action_audio_btn);
        setUpActionButton(photoNoteBtn, R.drawable.action_photo_btn);
    }

    private void setUpActionButton(FloatingActionButton actionButton, int buttonIcon) {
        actionButton.setOnClickListener(this);
        actionButton.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, buttonIcon), R.color.colorButtonIcon));
    }

    private boolean isFirstTimeIn() {
        // default to true first time
        boolean result = mPrefs.getBoolean(IS_FIRST_TIME_IN, true);
        if (result) {
            mPrefs.edit().putBoolean(IS_FIRST_TIME_IN, false).apply();
        }
        return result;
    }

    // check if the req'd permission has been granted
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean canWriteToExternalStorage() {
        // check the req'd permission is held
        return hasPermission(WRITE_EXTERNAL_STORAGE);
    }
    private boolean canRecordMedia() {
        // requires WRITE_EXTERNAL_STORAGE & RECORD_AUDIO permissions
        return canWriteToExternalStorage() && hasPermission(RECORD_AUDIO);
    }

    private void takePicture() {
        if (canWriteToExternalStorage()) {
            // permission given, take the picture
            launchCameraApp();
        } else if (!shouldShowWriteToStorageRational()){
            // permission not given, inform user permission req'd to execute feature
            showRationalMessage(getString(R.string.required_permission_feature));
        } else {
            // otherwise request permission
            ActivityCompat.requestPermissions(this,
                    permissionsHeld(PERMS_REQUIRED_SAVE_MEDIA), RESULT_PERMISSION_TAKE_PICTURE);
        }
    }

    private void recordVideo() {
        if (canRecordMedia()) {
            launchVideoApp();
        } else if (!shouldShowRecordMediaRational()) { // CHANGED
            showRationalMessage(getString(R.string.required_permission_feature));
        } else {
            ActivityCompat.requestPermissions(this,
                    permissionsHeld(ALL_REQUIRED_PERMS), RESULT_PERMISSION_RECORD_VIDEO);
        }

    }

    private void recordAudio() {
        if (canRecordMedia()) {
            launchAudioApp();
        } else if (!shouldShowRecordMediaRational()) { // CHANGED
            showRationalMessage(getString(R.string.required_permission_feature));
        } else {
            ActivityCompat.requestPermissions(this,
                    permissionsHeld(ALL_REQUIRED_PERMS), RESULT_PERMISSION_RECORD_AUDIO);
        }
    }


    // determine which permissions have not been given
    private String[] permissionsHeld(String[] permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                result.add(permission);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private boolean shouldShowWriteToStorageRational() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE);
    }

    private boolean shouldShowRecordMediaRational() {
        return shouldShowWriteToStorageRational() ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, RECORD_AUDIO);
    }

    private void showRationalMessage(String message) {
        //  add action to the snackbar allowing user to amend permissions in app's settings
        Utils.showSnackbar(mLayout, message);
        Snackbar snackbar = Snackbar
                .make(mLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // launch app settings screen
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });

        snackbar.show();
    }

    private void launchCameraApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri filePathUri = Utils.generateMediaFileUri(Constants.ITEM_TYPE_PHOTO);
        if (filePathUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePathUri);
            mFullSizePath = filePathUri.toString().substring(7); // FIXME change to '/storage'
            if (Utils.isAppInstalled(this, intent)) {
                startActivityForResult(intent, Constants.PHOTO_REQUEST_CODE);
            } else {
                Utils.showSnackbar(mLayout, "No app found suitable to capture photos");
            }
        }
    }

    private void launchVideoApp() {
        Uri fileUri = Utils.generateMediaFileUri(Constants.ITEM_TYPE_VIDEO);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Constants.VIDEO_REQUEST_CODE);
        } else {
            Utils.showSnackbar(mLayout, "No app found suitable to record video");
        }
    }

    private void launchAudioApp() {
        AudioRecorderActivity.launch(MainActivity.this);
    }


}
