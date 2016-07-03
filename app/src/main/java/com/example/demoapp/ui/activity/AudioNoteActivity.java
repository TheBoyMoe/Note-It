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
import com.example.demoapp.ui.fragment.AudioNoteFragment;

public class AudioNoteActivity extends AppCompatActivity implements AudioNoteFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AudioNoteActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, long id, String title, String filePath, String mimeType) {
        Intent intent = new Intent(activity, AudioNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);
        activity.startActivity(intent);
    }

    private AudioNoteFragment mFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
        mFragment = (AudioNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            if (id > 0) {
                String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
                String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
                String mimeType = getIntent().getStringExtra(Constants.ITEM_MIME_TYPE);
                mFragment = AudioNoteFragment.newInstance(id, title, filePath, mimeType);
            } else {
                mFragment = AudioNoteFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ITEM_AUDIO_NOTE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(Constants.ITEM_TITLE);
            String filePath = data.getStringExtra(Constants.ITEM_FILE_PATH);
            String mimeType = data.getStringExtra(Constants.ITEM_MIME_TYPE);

            // update fragment UI and display title and thumbnail
            if (mFragment != null) {
                mFragment.updateFragmentUI(title, filePath, mimeType);
            }
        }
    }

    // impl contract methods
    @Override
    public void saveAudioNote(String title, String filePath, String mimeType) {
        // TODO
    }

    @Override
    public void updateAudioNote(long id, String title, String filePath, String mimeType) {
        // TODO
    }

    @Override
    public void playAudio(String filePath, String mimeType) {
        // TODO
    }

    @Override
    public void selectAudio() {
        // check if there are any videos on the device
        CursorLoader loader = new CursorLoader(
                this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.TITLE);
        Cursor cursor = loader.loadInBackground();

        while(cursor.moveToNext()) {
            // TODO list cursor contents
        }

        if (cursor.getCount() > 0)
            // launch VideoListActivity using startActivityForResult
            AudioListActivity.launch(this);
        else
            Utils.showToast(this, "No audio tracks found on device");
    }

    @Override
    public void quit() {
        finish();
    }
}
