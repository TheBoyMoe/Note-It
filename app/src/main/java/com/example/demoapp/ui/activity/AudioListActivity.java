package com.example.demoapp.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.demoapp.R;
import com.example.demoapp.common.BaseActivity;
import com.example.demoapp.common.Constants;
import com.example.demoapp.ui.fragment.AudioListFragment;

public class AudioListActivity extends BaseActivity implements AudioListFragment.Contract{

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AudioListActivity.class);
        activity.startActivityForResult(intent, Constants.ITEM_AUDIO_NOTE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display page title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, AudioListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onClick(String title, String filePath, String mimeType) {
        // return the audio tracks title/filePath & mimeType to the calling activity
        final Intent intent = new Intent();
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        intent.putExtra(Constants.ITEM_MIME_TYPE, mimeType);

        new MaterialDialog.Builder(AudioListActivity.this)
                .title(R.string.audio_dialog_title)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        AudioListActivity.this.setResult(RESULT_OK, intent);
                        AudioListActivity.this.finish();
                    }
                })
                .positiveText(R.string.dialog_positive_text)
                .negativeText(R.string.dialog_negative_text)
                .show();


    }


}
