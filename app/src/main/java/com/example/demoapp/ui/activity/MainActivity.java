package com.example.demoapp.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.demoapp.R;
import com.example.demoapp.common.Utils;
import com.example.demoapp.ui.fragment.MainActivityFragment;
import com.example.demoapp.ui.fragment.ModelFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.Contract, View.OnClickListener{

    private static final String MODEL_FRAGMENT = "model_fragment";
    private FloatingActionsMenu mBtnTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            textNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_text_note), R.color.half_black));
        }

        FloatingActionButton videoNoteBtn = (FloatingActionButton) findViewById(R.id.action_video_note);
        if (videoNoteBtn != null) {
            videoNoteBtn.setOnClickListener(this);
            videoNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_video_note), R.color.half_black));
        }

        FloatingActionButton audioNoteBtn = (FloatingActionButton) findViewById(R.id.action_audio_note);
        if (audioNoteBtn != null) {
            audioNoteBtn.setOnClickListener(this);
            audioNoteBtn.setIconDrawable(Utils.tintDrawable(ContextCompat.getDrawable(this, R.drawable.action_audio_note), R.color.half_black));
        }

    }


    // contract methods
    @Override
    public void deleteItemTask(long itemId) {
        // TODO
        // new DeleteItemThread(itemId).start();
    }


    @Override
    public void onItemClick(long id, String title, String description) {
        // launch activity displaying text note
        TextNoteActivity.launch(MainActivity.this, id, title, description);
    }

    @Override
    public void onItemClick(long id, String title, String filePath, String mimeType) {
        // launch activity displaying video note
        VideoNoteActivity.launch(MainActivity.this, id, title, filePath, mimeType);
    }

    @Override
    public void onItemLongClick(long itemId) {
        // TODO ?? delete item or delete multiple(via cab)
        Utils.showToast(this, "Item clicked on: " + itemId);
    }


    // handle button clicks
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_text_note:
                // launch text note activity
                TextNoteActivity.launch(MainActivity.this);
                break;
            case R.id.action_video_note:
                VideoNoteActivity.launch(MainActivity.this);
                break;
            case R.id.action_audio_note:
                Toast.makeText(MainActivity.this, "clicked on audio button", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // collapse the btn menu if req'd
        if (mBtnTrigger.isExpanded()) {
            mBtnTrigger.collapse();
        }
    }

}
