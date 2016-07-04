package com.example.demoapp.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.thread.InsertItemThread;
import com.example.demoapp.ui.fragment.MainActivityFragment;
import com.example.demoapp.ui.fragment.ModelFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Contract, View.OnClickListener{

    private static final String MODEL_FRAGMENT = "model_fragment";
    private FloatingActionsMenu mBtnTrigger;
    private CoordinatorLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // cache a reference to a fragment
        MainActivityFragment recyclerFragment =
            (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (recyclerFragment == null) {
            recyclerFragment = MainActivityFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, recyclerFragment)
                    .commit();
        }

        // cache a reference to the model fragment
        Fragment modelFragment = getSupportFragmentManager().findFragmentByTag(MODEL_FRAGMENT);
        if (modelFragment == null) {
            modelFragment = ModelFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(modelFragment, MODEL_FRAGMENT)
                    .commit();
        }

        // button setup
        mBtnTrigger = (FloatingActionsMenu) findViewById(R.id.button_trigger);

        FloatingActionButton textNoteBtn = (FloatingActionButton) findViewById(R.id.action_text_note);
        if(textNoteBtn != null) {
            textNoteBtn.setOnClickListener(this);
            textNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_text_btn), R.color.half_black));
        }

        FloatingActionButton videoNoteBtn = (FloatingActionButton) findViewById(R.id.action_video_note);
        if (videoNoteBtn != null) {
            videoNoteBtn.setOnClickListener(this);
            videoNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_video_btn), R.color.half_black));
        }

        FloatingActionButton audioNoteBtn = (FloatingActionButton) findViewById(R.id.action_audio_note);
        if (audioNoteBtn != null) {
            audioNoteBtn.setOnClickListener(this);
            audioNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_audio_btn), R.color.half_black));
        }

    }

    // contract methods
    @Override
    public void deleteItemTask(long itemId) {
        // TODO
        // new DeleteItemThread(itemId).start();
    }

    @Override
    public void onNoteItemClick(long id, String title, String description) {
        // launch activity displaying text note
        TextNoteActivity.launch(MainActivity.this, id, title, description);
    }

    @Override
    public void onVideoItemClick(long id, String title, String filePath, String thumbnailPath, String mimeType) {
        // launch activity displaying video note
        VideoNoteActivity.launch(MainActivity.this, id, title, filePath, thumbnailPath, mimeType);
    }

    @Override
    public void onAudioItemClick(long id, String title, String description, String filePath) {
        // launch activity to display the audio note
        AudioNoteActivity.launch(MainActivity.this, id, title, description, filePath);
    }

    @Override
    public void onItemLongClick(long itemId) {
        // TODO ?? delete item or delete multiple(via cab)
        Utils.showToast(this, "Item clicked on: " + itemId);
    }

    // handle fab button clicks
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_text_note:
                // launch text note activity
                TextNoteActivity.launch(MainActivity.this);
                break;
            case R.id.action_video_note:
                // TODO launch video recording - start activity for result
                VideoNoteActivity.launch(MainActivity.this);
                break;
            case R.id.action_audio_note:
                // launch audio recording
                if(Utils.hasMicrophone(MainActivity.this)) {
                    // record audio using Android Sound Recorder app
//                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                    if(intent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(intent, Constants.AUDIO_REQUEST);
//                    } else {
//                        Utils.showSnackbar(mLayout, "No app found suitable to record audio");
//                    }
                    AudioRecorderActivity.launch(MainActivity.this);
                } else {
                    Utils.showSnackbar(mLayout, "The device does not support recording audio");
                }
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // collapse the btn menu if req'd
        if (mBtnTrigger.isExpanded()) {
            mBtnTrigger.collapse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.AUDIO_REQUEST && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(Constants.ITEM_FILE_PATH);
            String mimeType = data.getStringExtra(Constants.ITEM_MIME_TYPE);

            // insert item into database
            ContentValues values = Utils.setContentValuesAudioNote(
                    Utils.generateCustomId(),
                    Constants.ITEM_AUDIO_NOTE,
                    "", "", filePath, mimeType); // use empty string for title and description
            new InsertItemThread(this, values).start();

        } else if(resultCode == RESULT_CANCELED){
            Utils.showSnackbar(mLayout, "Operation cancelled by user");
        } else {
            Utils.showSnackbar(mLayout, "Error recording audio");
        }
    }


}
