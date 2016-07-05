package com.example.demoapp.ui.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

import timber.log.Timber;

public class AudioNoteFragment extends ContractFragment<AudioNoteFragment.Contract> implements
        View.OnClickListener,
        MediaPlayer.OnCompletionListener{

    public interface Contract {
        void updateAudioNote(long id, String title, String description);
        void quit();
    }

    private View mView;
    private EditText mEditTitle;
    private EditText mEditDescription;
    private ImageButton mPlay;
    private ImageButton mPause;
    private ImageButton mStop;

    private long mId;
    private String mTitle;
    private String mDescription;
    private String mFilePath;
    private MediaPlayer mPlayer;

    public AudioNoteFragment(){}

    public static AudioNoteFragment newInstance(long id, String title, String description, String filePath) {
        AudioNoteFragment fragment = new AudioNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
        args.putString(Constants.ITEM_FILE_PATH, filePath);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_audio_note, container, false);

        // add toolbar
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // retrieve the title and description and propagate up to the hosting activity
                    String title = mEditTitle.getText().toString();
                    String description = mEditDescription.getText().toString();
                    // update/quit
                    if (mTitle.equals(title) && mDescription.equals(description)) {
                        getContract().quit();
                    } else {
                        if (!mTitle.equals(title)) {
                            mTitle = title;
                        }
                        if (!mDescription.equals(description)) {
                            mDescription = description;
                        }
                        getContract().updateAudioNote(mId, mTitle, mDescription);
                    }
                }
            });
        }

        mEditTitle = (EditText) mView.findViewById(R.id.audio_note_title);
        mEditDescription = (EditText) mView.findViewById(R.id.audio_note_description);
        playerControlsSetup();

        if(getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitle = getArguments().getString(Constants.ITEM_TITLE);
            mDescription = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            if (!mTitle.isEmpty())
                mEditTitle.setText(mTitle);
            if (!mDescription.isEmpty())
                mEditDescription.setText(mDescription);
        }

        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
        }

        mPlayerSetup();

        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
    }


    /** MediaPlayer implementation  */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_play:
                play();
                break;
            case R.id.action_pause:
                pause();
                break;
            case R.id.action_stop:
                stop();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStop.isEnabled()) {
            stop();
        }
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    private void mPlayerSetup() {
        try {
            mPlayer = MediaPlayer.create(getActivity(), Uri.parse(mFilePath));
            mPlayer.setOnCompletionListener(this);
            mPlay.setEnabled(true);
        } catch (Exception e) {
            Timber.e("%s Error playing audio file: %s", Constants.LOG_TAG, e.getMessage());
            Utils.showSnackbar(mView, getString(R.string.error_playing_audio));
        }
        mPause.setEnabled(false);
        mStop.setEnabled(false);
    }

    private void play() {
        mPlayer.start();
        mPlay.setEnabled(false);
        mPause.setEnabled(true);
        mStop.setEnabled(true);
    }

    private void pause() {
        mPlayer.pause();
        mPlay.setEnabled(true);
        mPause.setEnabled(false);
        mStop.setEnabled(true);
    }

    private void stop() {
        mPlayer.stop();
        mStop.setEnabled(false);
        mPause.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlay.setEnabled(true);
        } catch(Exception e) {
            Timber.e("%s Error playing audio file: %s", Constants.LOG_TAG, e.getMessage());
            Utils.showSnackbar(mView, getString(R.string.error_playing_audio));
        }
    }

    private void playerControlsSetup() {
        mPlay = (ImageButton) mView.findViewById(R.id.action_play);
        mPause = (ImageButton) mView.findViewById(R.id.action_pause);
        mStop = (ImageButton) mView.findViewById(R.id.action_stop);

        mPlay.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }


}
