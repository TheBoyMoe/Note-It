package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class AudioNoteFragment extends ContractFragment<AudioNoteFragment.Contract>
        implements View.OnClickListener, View.OnLongClickListener, TextWatcher{



    public interface Contract {
        void saveAudioNote(String title, String filePath, String mimeType);
        void updateAudioNote(long id, String title, String filePath, String mimeType);
        void playAudio(String filePath, String mimeType);
        void selectAudio();
        void quit();
    }

    private EditText mEditText;

    private long mId;
    private String mTitle;
    private String mFilePath;
    private String mMimeType;

    public AudioNoteFragment(){}

    public static AudioNoteFragment newInstance() {
        return new AudioNoteFragment();
    }

    public static AudioNoteFragment newInstance(long id, String title, String filePath, String mimeType) {
        AudioNoteFragment fragment = new AudioNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_FILE_PATH, filePath);
        args.putString(Constants.ITEM_MIME_TYPE, mimeType);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_note, container,false);

        // add toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // save/update/quit
                    if (mTitle == null || mFilePath == null) {
                        getContract().quit();
                    } else {
                        if (mId > 0) {
                            getContract().updateAudioNote(mId, mTitle, mFilePath, mMimeType);
                        } else {
                            getContract().saveAudioNote(mTitle, mFilePath, mMimeType);
                        }
                    }
                }
            });
        }

        mEditText = (EditText) view.findViewById(R.id.audio_note_title);
        mEditText.addTextChangedListener(this);
        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.wrapper);
        wrapper.setOnClickListener(this);
        wrapper.setOnLongClickListener(this);

        if(getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitle = getArguments().getString(Constants.ITEM_TITLE);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mMimeType = getArguments().getString(Constants.ITEM_MIME_TYPE);
            mEditText.setText(mTitle);
        }

        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mMimeType = savedInstanceState.getString(Constants.ITEM_MIME_TYPE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        getContract().playAudio(mFilePath, mMimeType);
    }

    @Override
    public boolean onLongClick(View v) {
        getContract().selectAudio();
        return true;
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        mTitle = text.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // no-op
    }

    @Override
    public void afterTextChanged(Editable s) {
        // no-op
    }

    public void updateFragmentUI(String title, String filePath, String mimeType) {
        if (mTitle == null) mTitle = title;
        mFilePath = filePath;
        mMimeType = mimeType;
        mEditText.setText(mTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
        outState.putString(Constants.ITEM_MIME_TYPE, mMimeType);
    }


}
