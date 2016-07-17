package com.example.demoapp.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class PreviewFragment extends ContractFragment<PreviewFragment.Contract>
        implements View.OnClickListener{

    private ImageView preview;


    public interface Contract {
        void playVideo(long id, String flePath, String mimeType);
        void update(long id, String title, String description);
        void delete(long id);
        void quit();
    }

    private EditText mTitle;
    private EditText mDescription;

    private long mId;
    private String mFilePath;
    private String mPreviewPath;
    private String mMimeType;


    public PreviewFragment() {}

    public static PreviewFragment newInstance(long id, String title, String description, String filePath, String previewPath, String mimeType) {

        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
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
        View view = inflater.inflate(R.layout.fragment_preview_note, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setUpToolbar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // retrieve title & description and propagate upto hosting activity
                    String title = mTitle.getText() != null ? mTitle.getText().toString() : "";
                    String description = mDescription.getText() != null ? mDescription.getText().toString() : "";
                    if (title.isEmpty()) {
                        getContract().quit();
                    } else  if (mId > 0){
                        updateAndQuit(title, description);
                    }
                }
            });
        }

        ImageView preview = (ImageView) view.findViewById(R.id.preview_image);;
        mTitle = (EditText) view.findViewById(R.id.preview_title);
        mDescription = (EditText) view.findViewById(R.id.preview_description);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (getArguments() != null) {
            // fetch note details from args bundle
            mId = getArguments().getLong(Constants.ITEM_ID, 0);
            String titleText = getArguments().getString(Constants.ITEM_TITLE);
            String descriptionText = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mPreviewPath = getArguments().getString(Constants.ITEM_PREVIEW_PATH);
            mMimeType = getArguments().getString(Constants.ITEM_MIME_TYPE);

            if (titleText != null && !titleText.isEmpty()) {
                mTitle.setText(titleText);
            }
            if (descriptionText != null && !descriptionText.isEmpty()) {
                mDescription.setText(descriptionText);
            }
        }

        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mPreviewPath = savedInstanceState.getString(Constants.ITEM_PREVIEW_PATH);
            mMimeType = savedInstanceState.getString(Constants.ITEM_MIME_TYPE);
        }

        Utils.loadPreviewWithPicasso(getActivity(), mPreviewPath, preview);

        // display fab and onClickListener on VideoNote fragments
        if (mMimeType.equals(Constants.VIDEO_MIMETYPE)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete_white, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                getContract().delete(mId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
        outState.putString(Constants.ITEM_PREVIEW_PATH, mPreviewPath);
        outState.putString(Constants.ITEM_MIME_TYPE, mMimeType);
    }

    private void setUpToolbar(Toolbar toolbar) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_back_white));
    }

    private void updateAndQuit(String title, String description) {
        getContract().update(mId, title, description);
    }

    @Override
    public void onClick(View v) {
        getContract().playVideo(mId, mFilePath, mMimeType);
    }


}
