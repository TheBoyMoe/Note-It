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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.demoapp.R;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class VideoNoteFragment extends ContractFragment<VideoNoteFragment.Contract>
        implements View.OnClickListener, View.OnLongClickListener, TextWatcher{

    public interface Contract {
        void saveVideoNote();
        void updateVideoNote();
        void playVideo();
        void selectVideo();
        void quit();
    }

    private EditText mEditText;
    private ImageView mImageView;
    private String mTitle;
    private String mFilePath;
    private String mMimeType;

    public VideoNoteFragment() {}

    public static VideoNoteFragment newInstance() {
        return new VideoNoteFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_note, container, false);

        // add toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO save/update/quit
                }
            });
        }

        mEditText = (EditText) view.findViewById(R.id.video_note_title);
        mEditText.addTextChangedListener(this);
        mImageView = (ImageView) view.findViewById(R.id.video_note_thumbnail);
        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.wrapper);
        wrapper.setOnClickListener(this);
        wrapper.setOnLongClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        getContract().playVideo();
    }

    @Override
    public boolean onLongClick(View v) {
        getContract().selectVideo();
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

    public void updateFragmentUI(String title, String filePath) {
        mTitle = title;
        mFilePath = filePath;
        mEditText.setText(mTitle);
        mImageView.setImageBitmap(Utils.generateBitmap(mFilePath));
    }

}
