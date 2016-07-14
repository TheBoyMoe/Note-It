package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.LoadInfoItemTask;
import com.example.demoapp.ui.fragment.PhotoNoteFragment;

public class PhotoNoteActivity extends AppCompatActivity
        implements PhotoNoteFragment.Contract{


    @SuppressWarnings("unchecked")
    public static void launch(Activity activity, View layout, long id, String previewPath) {
        Intent intent = new Intent(activity, PhotoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_PREVIEW_PATH, previewPath);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(layout.findViewById(R.id.item_thumbnail), activity.getString(R.string.thumbnail_transition))
                );
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        PhotoNoteFragment fragment = (PhotoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String previewPath = getIntent().getStringExtra(Constants.ITEM_PREVIEW_PATH);
            fragment = PhotoNoteFragment.newInstance(id, previewPath);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void displayPhotoInfo(long id) {
        // query the database to retrieve most upto date info
        new LoadInfoItemTask(PhotoNoteActivity.this).execute(id);
    }

    @Override
    public void delete(long id) {
        Utils.deleteItemFromDevice(this, id);
    }


}
