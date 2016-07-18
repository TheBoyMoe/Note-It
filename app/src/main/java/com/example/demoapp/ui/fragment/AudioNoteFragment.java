package com.example.demoapp.ui.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

import java.util.Random;

import timber.log.Timber;

public class AudioNoteFragment extends ContractFragment<AudioNoteFragment.Contract> implements
        View.OnClickListener,
        MediaPlayer.OnCompletionListener{

    public interface Contract {
        void updateAudioNote(long id, String title, String description);
        void quit();
        void delete(long id);
    }

    private static final String STATE_PLAY_BUTTON = "play_button_state";
    private static final String STATE_PAUSE_BUTTON = "pause_button_state";
    private static final String STATE_STOP_BUTTON = "stop_button_state";
    private static final String STATE_PLAYER = "player_state";
    private static final int PROGRESS_DELAY = 100;
    private static final int MAX_PROGRESS = 100;

    private View mView;
    private EditText mEditTitle;
    private EditText mEditDescription;
    private ImageButton mPlay;
    //private ImageButton mPause;
    private ImageButton mStop;
    private ProgressBar mProgressBar;

    private long mId;
    private String mTitle;
    private String mDescription;
    private String mFilePath;
    private boolean mIsPlaying = false;

    private MediaPlayer mPlayer;
    private Handler mHandler;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // stop onDestroy() being called - maintain audio playback during device rotation
        setRetainInstance(true);
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

        // restore states
        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mPlay.setEnabled(savedInstanceState.getBoolean(STATE_PLAY_BUTTON));
            //mPause.setEnabled(savedInstanceState.getBoolean(STATE_PAUSE_BUTTON));
            mStop.setEnabled(savedInstanceState.getBoolean(STATE_STOP_BUTTON));
            mIsPlaying = savedInstanceState.getBoolean(STATE_PLAYER);
            if (mIsPlaying) {
                mPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_pause));
            }
        } else {
            mPlayerSetup();
        }


        return mView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);

        // save button states
        outState.putBoolean(STATE_PLAY_BUTTON, mPlay.isEnabled());
        //outState.putBoolean(STATE_PAUSE_BUTTON, mPause.isEnabled());
        outState.putBoolean(STATE_STOP_BUTTON, mStop.isEnabled());
        outState.putBoolean(STATE_PLAYER, mIsPlaying);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete_black, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                getContract().delete(mId);
                return true;
            case android.R.id.home:
                getActivity().supportFinishAfterTransition();
                //getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /** MediaPlayer implementation  */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_play:
                if (!mIsPlaying) {
                    play();
                } else {
                    pause();
                }
                break;
            //case R.id.action_pause:
            //    pause();
            //    break;
            case R.id.action_stop:
                stop();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
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
        //mPause.setEnabled(false);
        mStop.setEnabled(false);
    }

    private void play() {
        mIsPlaying = true;
        mPlayer.start();
        mPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_pause));
        //mPlay.setEnabled(false);
        //mPause.setEnabled(true);
        mStop.setEnabled(true);
        //progressBarAnimation();
    }

    private void pause() {
        mIsPlaying = false;
        mPlayer.pause();
        mPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_play));
        //mPlay.setEnabled(true);
        //mPause.setEnabled(false);
        mStop.setEnabled(true);
    }

    private void stop() {
        mIsPlaying = false;
        mPlayer.stop();
        mPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_play));
        mStop.setEnabled(false);
        //mPause.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            mPlay.setEnabled(true);
        } catch(Exception e) {
            Timber.e("%s Error stopping audio file: %s", Constants.LOG_TAG, e.getMessage());
            Utils.showSnackbar(mView, getString(R.string.error_playing_audio));
        }
    }

    private void playerControlsSetup() {
        mHandler = new Handler();

        mPlay = (ImageButton) mView.findViewById(R.id.action_play);
        //mPause = (ImageButton) mView.findViewById(R.id.action_pause);
        mStop = (ImageButton) mView.findViewById(R.id.action_stop);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);

        mPlay.setOnClickListener(this);
        //mPause.setOnClickListener(this);
        mStop.setOnClickListener(this);
    }

    private void progressBarAnimation() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentProgress = 0;
                int total = mPlayer.getDuration();
                mProgressBar.setMax(total);
                while (mPlayer != null && currentProgress < total) {
                    currentProgress = mPlayer.getCurrentPosition();
                    mProgressBar.setProgress(currentProgress);
                }
//                if (currentProgress > 100) {
//                    currentProgress = 100;
//                }

//                if (currentProgress <= MAX_PROGRESS) {
//                    progressBarAnimation();
//                }
            }
        }, PROGRESS_DELAY);
    }

    private int getNewProgress() {
        Random random = new Random();
        return random.nextInt(5);
    }


}
