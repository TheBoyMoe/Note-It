package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class VideoNoteFragment extends ContractFragment<VideoNoteFragment.Contract>
        implements View.OnClickListener{

    public interface Contract {
        void playVideo(String filePath, String mimeType);
        void displayPhotoInfo(long id);
        void delete(long id);
    }

    private long mId;
    private String mFilePath;
    private String mPreviewPath;
    private String mMimeType;

    public VideoNoteFragment() {}

    public static VideoNoteFragment newInstance(long id, String filePath, String previewPath, String mimeType) {

        VideoNoteFragment fragment = new VideoNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_FILE_PATH, filePath);
        args.putString(Constants.ITEM_PREVIEW_PATH, previewPath);
        args.putString(Constants.ITEM_MIME_TYPE, mimeType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_note, container, false);

        // add toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
        }

        ImageView preview = (ImageView) view.findViewById(R.id.video_note_preview);
        FloatingActionButton infobtn = (FloatingActionButton) view.findViewById(R.id.action_info_btn);
        infobtn.setIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_edit));

        preview.setOnClickListener(this); // play video
        infobtn.setOnClickListener(this); // display video details

        if(getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mPreviewPath = getArguments().getString(Constants.ITEM_PREVIEW_PATH);
            mMimeType = getArguments().getString(Constants.ITEM_MIME_TYPE);
        }

        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mPreviewPath = savedInstanceState.getString(Constants.ITEM_PREVIEW_PATH);
            mMimeType = savedInstanceState.getString(Constants.ITEM_MIME_TYPE);
        }

        Utils.loadPreviewWithPicasso(getActivity(), mPreviewPath, preview);

        return view;
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
                super.getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_info_btn:
                getContract().displayPhotoInfo(mId);
                break;
            case R.id.video_note_preview:
                getContract().playVideo(mFilePath, mMimeType);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
        outState.putString(Constants.ITEM_PREVIEW_PATH, mPreviewPath);
        outState.putString(Constants.ITEM_MIME_TYPE, mMimeType);
    }

}
