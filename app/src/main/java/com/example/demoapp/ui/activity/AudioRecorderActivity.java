package com.example.demoapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class AudioRecorderActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        MediaRecorder.OnErrorListener,
        MediaRecorder.OnInfoListener{

    private MediaRecorder mRecorder = null;
    private File mAudioFile;


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, AudioRecorderActivity.class);
        activity.startActivityForResult(intent, Constants.AUDIO_REQUEST_CODE);
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
    protected void onResume() {
        super.onResume();
        mRecorder = new MediaRecorder();
        mRecorder.setOnErrorListener(this);
        mRecorder.setOnInfoListener(this);
    }

    @Override
    protected void onPause() {
        mRecorder.release();
        mRecorder = null;
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mAudioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Utils.generateAudioFileName());

            // Timber.i("%s: file path: %s", Constants.LOG_TAG, mAudioFile);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  // FIXME broken in api 23
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(160 * 1024);
            mRecorder.setAudioChannels(2);
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IOException e) {
                Timber.e("%s Error recording audio: %s", Constants.LOG_TAG, e.getMessage());
                Utils.showToast(this, getString(R.string.error_generic));
            }
        } else {
            try {
                mRecorder.stop();
            } catch (Exception e) {
                Timber.e("%s: Error stopping recording: %s", e.getMessage());
                Utils.showToast(this, getString(R.string.error_generic));
            }
            mRecorder.reset();

            // send back file path to MainActivity
            Intent intent =  new Intent();
            intent.putExtra(Constants.ITEM_FILE_PATH, mAudioFile.toString());
            intent.putExtra(Constants.ITEM_MIME_TYPE, Constants.AUDIO_MIMETYPE);
            setResult(RESULT_OK, intent);

            finish();
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        String message = null;

        switch (what) {
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                message = getString(R.string.error_max_duration);
                break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                message = getString(R.string.error_max_file_size);
                break;
            default:
                message = getString(R.string.error_unknown);
        }
        Utils.showToast(this, message);
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        Utils.showToast(this, getString(R.string.error_unknown));
    }



}
