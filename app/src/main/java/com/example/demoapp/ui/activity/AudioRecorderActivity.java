package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.demoapp.R;

public class AudioRecorderActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        MediaRecorder.OnErrorListener,
        MediaRecorder.OnInfoListener{

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AudioRecorderActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle_button);
        if (toggle != null)
            toggle.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }
}
