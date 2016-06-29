package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
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
        // TODO save note to database
        Timber.i("%s, title: %s, description: %s", Constants.LOG_TAG, title, description);
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


}
