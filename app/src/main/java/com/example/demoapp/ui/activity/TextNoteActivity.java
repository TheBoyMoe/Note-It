package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
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
import com.example.demoapp.thread.InsertItemThread;
import com.example.demoapp.thread.UpdateItemThread;
import com.example.demoapp.ui.fragment.TextNoteFragment;

public class TextNoteActivity extends AppCompatActivity
        implements TextNoteFragment.Contract{


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, TextNoteActivity.class);
        activity.startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public static void launch(Activity activity, View layout, long id, String title, String description) {
        Intent intent = new Intent(activity, TextNoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, id);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(layout.findViewById(R.id.item_title), activity.getString(R.string.title_transition)),
                        new Pair<View, String>(layout.findViewById(R.id.item_description), activity.getString(R.string.description_transition))
                );
        activity.startActivity(intent, options.toBundle());
    }

    @Override
    public void saveTextNote(String title, String description) {
        // save note to database
        ContentValues values = Utils.setContentValuesTextNote(Utils.generateCustomId(), Constants.ITEM_TYPE_TEXT, title, description);
        new InsertItemThread(this, values).start();
        finish();
    }

    @Override
    public void updateTextNote(long id, String title, String description) {
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
    public void delete(final long id) {
        Utils.deleteItemFromDevice(this, id);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_layout);

        // retrieve values from intent
        long id = getIntent().getLongExtra(Constants.ITEM_ID, 0);
        TextNoteFragment fragment = null;
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            if (id > 0) {
                String title = getIntent().getStringExtra(Constants.ITEM_TITLE);
                String description = getIntent().getStringExtra(Constants.ITEM_DESCRIPTION);
                fragment = TextNoteFragment.newInstance(id, title, description);
            } else {
                fragment = TextNoteFragment.newInstance();
            }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
        }
    }



}
