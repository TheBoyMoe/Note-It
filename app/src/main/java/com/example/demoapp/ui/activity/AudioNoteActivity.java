package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.DeleteItemsThread;
import com.example.demoapp.thread.UpdateItemThread;
import com.example.demoapp.ui.fragment.AudioNoteFragment;

import timber.log.Timber;

public class AudioNoteActivity extends AppCompatActivity implements
        AudioNoteFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AudioNoteActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, long id, String title, String description, String filePath) {
        Intent intent = new Intent(activity, AudioNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        AudioNoteFragment fragment = (AudioNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {

            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
            String description = getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
            String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
            fragment = AudioNoteFragment.newInstance(id, title, description, filePath);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void updateAudioNote(long id, String title, String description) {
        // only changes to the title or description are saved
        Timber.i("%s: update audio note", Constants.LOG_TAG);
        ContentValues values = Utils.updateContentValues(id, title,  description);
        new UpdateItemThread(this, values).start();
        finish();
    }

    @Override
    public void quit() {
        finish();
    }

    @Override
    public void delete(final long id) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.note_deletion_dialog_title))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (id > 0) {
                            String[] args = {String.valueOf(id)};
                            new DeleteItemsThread(AudioNoteActivity.this, args).start();
                        }
                        finish();
                    }
                })
                .positiveText(getString(R.string.dialog_positive_text))
                .negativeText(getString(R.string.dialog_negative_text))
                .show();
    }


}
