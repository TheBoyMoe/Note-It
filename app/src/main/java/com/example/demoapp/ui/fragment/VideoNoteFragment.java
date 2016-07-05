package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class VideoNoteFragment extends ContractFragment<VideoNoteFragment.Contract>
        implements View.OnClickListener{

    public interface Contract {
        void updateVideoNote(long id, String title, String description);
        void playVideo(String filePath, String mimeType);
        void quit();
    }

    private EditText mEditTitle;
    private EditText mEditDescription;
    private ImageView mThumbnail;

    private long mId;
    private String mTitle;
    private String mDescription;
    private String mFilePath;
    private String mThumbnailPath;
    private String mMimeType;

    public VideoNoteFragment() {}

    public static VideoNoteFragment newInstance() {
        return new VideoNoteFragment();
    }

    public static VideoNoteFragment newInstance(long id,
                String title, String description, String filePath, String thumbnailPath, String mimeType) {

        VideoNoteFragment fragment = new VideoNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
        args.putString(Constants.ITEM_FILE_PATH, filePath);
        args.putString(Constants.ITEM_THUMBNAIL_PATH, thumbnailPath);
        args.putString(Constants.ITEM_MIME_TYPE, mimeType);
        fragment.setArguments(args);

        return fragment;
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
                    // retrieve the title & description, propagate upto the hosting activity when they change
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
                        getContract().updateVideoNote(mId, mTitle, mDescription);
                    }
                }
            });
        }

        mEditTitle = (EditText) view.findViewById(R.id.video_note_title);
        mEditDescription = (EditText) view.findViewById(R.id.video_note_description);
        mThumbnail = (ImageView) view.findViewById(R.id.video_note_thumbnail);
        mThumbnail.setOnClickListener(this);

        if(getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitle = getArguments().getString(Constants.ITEM_TITLE);
            mDescription = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mThumbnailPath = getArguments().getString(Constants.ITEM_THUMBNAIL_PATH);
            mMimeType = getArguments().getString(Constants.ITEM_MIME_TYPE);

            mEditTitle.setText(mTitle);
            mEditDescription.setText(mDescription);
            Utils.loadLargeThumbnail(getActivity(), mThumbnailPath, mThumbnail);
        }

        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mThumbnailPath = savedInstanceState.getString(Constants.ITEM_THUMBNAIL_PATH);
            mMimeType = savedInstanceState.getString(Constants.ITEM_MIME_TYPE);
            Utils.loadLargeThumbnail(getActivity(), mThumbnailPath, mThumbnail);
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        getContract().playVideo(mFilePath, mMimeType);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
        outState.putString(Constants.ITEM_THUMBNAIL_PATH, mThumbnailPath);
        outState.putString(Constants.ITEM_MIME_TYPE, mMimeType);
    }

}
