package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;
import com.example.demoapp.ui.fragment.TextNoteFragment;

import timber.log.Timber;

public class TextNoteActivity extends AppCompatActivity
        implements TextNoteFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, TextNoteActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity, long id, String title, String description) {
        Intent intent = new Intent(activity, TextNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        activity.startActivity(intent);
    }

    @Override
    public void saveTextNote(String title, String description) {
        // save note to database
        ContentValues values = Utils.setContentValuesTextNote(Utils.generateCustomId(), title, description);
        new InsertItemThread(values).start();
        finish();
    }

    @Override
    public void updateTextNote(long id, String title, String description) {
        // update note in database
        ContentValues values = Utils.setContentValuesTextNote(id, title, description);
        new UpdateItemThread(values).start();
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

        // retrieve values from intent
        Intent intent = getIntent();
        long id = intent.getLongExtra(Constants.ITEM_ID, 0);
        String title = intent.getStringExtra(Constants.ITEM_TITLE);
        String description = intent.getStringExtra(Constants.ITEM_DESCRIPTION);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            if (id > 0) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, TextNoteFragment.newInstance(id, title, description))
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, TextNoteFragment.newInstance())
                        .commit();
            }
        }
    }


    // insert item into database via a bkgd thread
    class InsertItemThread extends Thread {

        private ContentValues mValues;

        public InsertItemThread(ContentValues values) {
            super();
            mValues = values;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {

                DatabaseHelper.getInstance(TextNoteActivity.this).
                        insertTaskItem(TextNoteActivity.this, mValues);
            } catch (Exception e) {
                Timber.e("%s: error adding item to dbase, %s", Constants.LOG_TAG, e.getMessage());
            }
            // query the dbase so as to trigger an update of the ui
            Utils.queryAllItems(TextNoteActivity.this);
        }
    }

    // update database item via bkgd thread
    class UpdateItemThread extends Thread {

        private ContentValues mValues;

        public UpdateItemThread(ContentValues values) {
            mValues = values;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(TextNoteActivity.this).updateTaskItem(TextNoteActivity.this, mValues);
            } catch (Exception e) {
                Timber.e("%s: error deleting item from the database, %s", Constants.LOG_TAG, e.getMessage());
            }
            // trigger ui update
            Utils.queryAllItems(TextNoteActivity.this);
        }
    }

}
