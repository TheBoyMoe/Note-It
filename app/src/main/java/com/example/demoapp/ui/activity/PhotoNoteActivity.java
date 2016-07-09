package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.LoadInfoItemTask;
import com.example.demoapp.ui.fragment.PhotoNoteFragment;

public class PhotoNoteActivity extends AppCompatActivity
        implements PhotoNoteFragment.Contract{


    public static void launch(Activity activity, long id, String filePath) {
        Intent intent = new Intent(activity, PhotoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_FILE_PATH, filePath);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        PhotoNoteFragment fragment = (PhotoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String filePath = getIntent().getStringExtra(Constants.ITEM_FILE_PATH);
            fragment = PhotoNoteFragment.newInstance(id, filePath);

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
