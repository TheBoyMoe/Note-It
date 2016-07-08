package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoNoteFragment extends ContractFragment<PhotoNoteFragment.Contract>
        implements View.OnClickListener{

    public interface Contract {
        void updatePhotoNote(long id, String title, String description);
        void displayPhoto(String filePath);
        void delete(long id);
        void quit();
    }

    private EditText mTitle;
    private EditText mDescription;

    private long mId;
    private String mTitleText;
    private String mDescriptionText;
    private String mFilePath;


    public PhotoNoteFragment() {}

    public static PhotoNoteFragment newInstance(long id, String title, String description, String filePath) {
        PhotoNoteFragment fragment = new PhotoNoteFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photo_note, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO do stuff impl delete and quit methods

                }
            });
        }

        mTitle = (EditText) view.findViewById(R.id.photo_note_title);
        mDescription = (EditText) view.findViewById(R.id.photo_note_description);
        ImageView image = (ImageView) view.findViewById(R.id.photo_note_image);
        image.setOnClickListener(this);
        // ?? display large scale image

        if (getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitleText = getArguments().getString(Constants.ITEM_TITLE);
            mDescriptionText = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mTitle.setText(mTitleText);
            mDescription.setText(mTitleText);
        }

        // retrieve saved state
        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
        }

        // load image
        Picasso.with(getActivity())
                .load(new File(mFilePath))
                .resize(250, 250)
                .placeholder(R.drawable.action_video_placeholder)
                .error(R.drawable.action_video_placeholder)
                .centerCrop()
                .into(image);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete_black, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            getContract().delete(mId);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        getContract().displayPhoto(mFilePath);  // ?? remove
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
    }



}
