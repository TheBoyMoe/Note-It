package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.UpdateItemThread;
import com.example.demoapp.ui.fragment.InfoNoteFragment;


public class InfoNoteActivity extends AppCompatActivity
        implements InfoNoteFragment.Contract{


    public static void launch(Activity activity, long id, String title, String description) {
        Intent intent = new Intent(activity, InfoNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        activity.startActivity(intent);
    }

    @Override
    public void updateInfoNote(long id, String title, String description) {
        // update note in the database
        ContentValues values = Utils.updateContentValues(id, title, description);
        new UpdateItemThread(this, values).start();
        finish();
    }

    @Override
    public void quit() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        InfoNoteFragment fragment = (InfoNoteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
            String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
            String description = getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
            fragment = InfoNoteFragment.newInstance(id, title, description);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


}
