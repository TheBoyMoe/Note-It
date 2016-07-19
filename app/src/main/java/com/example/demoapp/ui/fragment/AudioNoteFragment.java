package com.example.demoapp.ui.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

import timber.log.Timber;


/**
 * References
 * [1] http://stackoverflow.com/questions/8956218/android-seekbar-setonseekbarchangelistener
 * [2] http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/
 * [3] http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
 * [4] http://stackoverflow.com/questions/21864890/change-progress-music-when-clicked-on-seekbar-in-android
 * [5] http://united-coders.com/nico-heid/an-android-seekbar-for-your-mediaplayer/
 *
 */
public class AudioNoteFragment extends ContractFragment<AudioNoteFragment.Contract> implements
        View.OnClickListener,
        MediaPlayer.OnCompletionListener{

    public interface Contract {
        void updateAudioNote(long id, String title, String description);
        void quit();
        void delete(long id);
    }

    private static final String STATE_PLAY_BUTTON = "play_button_state";
    private static final String STATE_STOP_BUTTON = "stop_button_state";
    private static final String STATE_IS_PLAYING = "is_playing";
    private static final String STATE_CURRENT_POSITION = "current_position";

    private View mView;
    private EditText mEditTitle;
    private EditText mEditDescription;
    private ImageButton mPlayButton;
    private ImageButton mStopButton;
    private AppCompatSeekBar mProgressBar;

    private long mId;
    private String mTitle;
    private String mDescription;
    private String mFilePath;
    private boolean mIsPlaying;
    private int mCurrentPosition;
    private int mDuration;

    private MediaPlayer mPlayer;
    private Handler mSeekHandler;

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
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION, 0);
            mIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING, false);
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mPlayButton.setEnabled(savedInstanceState.getBoolean(STATE_PLAY_BUTTON));
            mStopButton.setEnabled(savedInstanceState.getBoolean(STATE_STOP_BUTTON));
            mIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING);
            if (mIsPlaying) {
                mPlayButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_pause));
            }
            mProgressBar.setProgress(mCurrentPosition);
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
        outState.putBoolean(STATE_PLAY_BUTTON, mPlayButton.isEnabled());
        outState.putBoolean(STATE_STOP_BUTTON, mStopButton.isEnabled());
        outState.putBoolean(STATE_IS_PLAYING, mIsPlaying);
        outState.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_play:
                if (mIsPlaying) {
                    mCurrentPosition = getCurrentPosition();
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.action_stop:
                stop();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStopButton.isEnabled()) {
            stop();
        }
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    private void mPlayerSetup() {
        try {
            mPlayer = MediaPlayer.create(getActivity(), Uri.parse(mFilePath));
            mPlayer.setOnCompletionListener(this);
            mDuration = mPlayer.getDuration();
            Timber.i("%s: duration: %d", Constants.LOG_TAG, mDuration);
            mPlayButton.setEnabled(true);
        } catch (Exception e) {
            Timber.e("%s Error playing audio file: %s", Constants.LOG_TAG, e.getMessage());
            Utils.showSnackbar(mView, getString(R.string.error_playing_audio));
        }
        mStopButton.setEnabled(false);
    }

    private void play() {
        if (mPlayer != null) {
            mPlayer.start();
            mIsPlaying = true;
            mSeekHandler.postDelayed(onProgressUpdater, 1000);
            mPlayButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_pause));
            mStopButton.setEnabled(true);
        }
    }

    private void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            mIsPlaying = false;
            mSeekHandler.removeCallbacks(onProgressUpdater);
            mPlayButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_play));
            mStopButton.setEnabled(true);
        }
    }

    private void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mIsPlaying = false;
            mSeekHandler.removeCallbacks(onProgressUpdater);
            mPlayButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_audio_play));
            mStopButton.setEnabled(false);
            mCurrentPosition = 0;
            mProgressBar.setProgress(mCurrentPosition);
            try {
                mPlayer.prepare();
                mPlayer.seekTo(0);
                mPlayButton.setEnabled(true);
            } catch (Exception e) {
                Timber.e("%s Error stopping audio file: %s", Constants.LOG_TAG, e.getMessage());
                Utils.showSnackbar(mView, getString(R.string.error_playing_audio));
            }
        }
    }

    private void playerControlsSetup() {
        mSeekHandler = new Handler();

        mPlayButton = (ImageButton) mView.findViewById(R.id.action_play);
        mStopButton = (ImageButton) mView.findViewById(R.id.action_stop);
        mProgressBar = (AppCompatSeekBar) mView.findViewById(R.id.progress_bar);

        mPlayButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);
    }

    private int getCurrentPosition() {
        int currentPosition = 0;
        if (mPlayer != null) {
            currentPosition = mPlayer.getCurrentPosition();
        }
        return currentPosition;
    }

    public Runnable onProgressUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                mCurrentPosition = getCurrentPosition();
                Timber.i("%s: current position: %d, duration: %d", Constants.LOG_TAG, mCurrentPosition, mDuration);
                mProgressBar.setMax(mDuration);
                mProgressBar.setProgress(mCurrentPosition);
                // repeat every second
                mSeekHandler.postDelayed(onProgressUpdater, 1000);
            }
        }
    };


}
