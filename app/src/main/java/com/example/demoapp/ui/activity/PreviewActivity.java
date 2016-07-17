package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.ui.fragment.PreviewFragment;

public class PreviewActivity extends AppCompatActivity
        implements PreviewFragment.Contract{

    private CoordinatorLayout mLayout;

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
        setContentView(R.layout.activity_note_layout);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

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
    public void playVideo(String filePath, long id) {

    }

    @Override
    public void save(String title, String description) {

    }

    @Override
    public void update(long id, String title, String description) {

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public void quit() {

    }



}
