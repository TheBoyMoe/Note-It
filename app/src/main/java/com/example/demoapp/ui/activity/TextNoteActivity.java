package com.example.demoapp.ui.activity;

import android.app.Activity;
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

    @Override
    public void saveTextNote(String title, String description) {
        // save note to database
        Timber.i("%s, title: %s, description: %s", Constants.LOG_TAG, title, description);
        new InsertItemThread(Utils.generateCustomId(), title, description).start();
        finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, TextNoteFragment.newInstance())
                    .commit();
        }
    }


    // insert item into database via a bkgd thread
    class InsertItemThread extends Thread {
        private long mItemId;
        private String mTitle;
        private String mDescription;

        public InsertItemThread(long itemId, String title, String description) {
            super();
            mItemId = itemId;
            mTitle = title;
            mDescription = description;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(TextNoteActivity.this).
                        insertTaskItem(TextNoteActivity.this, mItemId, mTitle, mDescription);
            } catch (Exception e) {
                Timber.e("%s: error adding item to dbase, %s", Constants.LOG_TAG, e.getMessage());
            }
            // query the dbase so as to trigger an update of the ui
            Utils.queryAllItems(TextNoteActivity.this);
        }
    }

}
