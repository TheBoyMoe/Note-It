package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PhotoNoteFragment extends ContractFragment<PhotoNoteFragment.Contract>
        implements View.OnClickListener{

    public interface Contract {
        // void updatePhotoNote(long id, String title, String description);
        // void displayPhoto(String filePath, String mimeType);
        void displayPhotoInfo(long id);
        void delete(long id);
        // void quit();
    }

    //private EditText mTitle;
    //private EditText mDescription;

    private long mId;
    private String mTitleText;
    private String mDescriptionText;
    private String mFilePath;
    private String mMimeType;


    public PhotoNoteFragment() {}

    public static PhotoNoteFragment newInstance(long id, String title, String description, String filePath, String mimeType) {
        PhotoNoteFragment fragment = new PhotoNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
        args.putString(Constants.ITEM_FILE_PATH, filePath);
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
        final View view = inflater.inflate(R.layout.fragment_photo_note, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                // hide title by default
                actionBar.setDisplayShowTitleEnabled(false);
                toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_back_white));
            }

//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // retrieve title & description, save to the database if req'd
//                    String title = mTitle.getText().toString();
//                    String description = mDescription.getText().toString();
//                    if (mTitleText.equals(title) && mDescriptionText.equals(description)) {
//                        // if neither has changed, quit
//                        getContract().quit();
//                    } else {
//                        // either/both have changed, update the database record
//                        getContract().updatePhotoNote(mId, title, description);
//                    }
//                }
//            });
        }

        //mTitle = (EditText) view.findViewById(R.id.photo_note_title);
        //mDescription = (EditText) view.findViewById(R.id.photo_note_description);
        ImageView image = (ImageView) view.findViewById(R.id.photo_note_image);
        FloatingActionButton infobtn = (FloatingActionButton) view.findViewById(R.id.action_info_btn);
        infobtn.setOnClickListener(this);
        infobtn.setIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_info_btn));

        if (getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitleText = getArguments().getString(Constants.ITEM_TITLE);
            mDescriptionText = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mFilePath = getArguments().getString(Constants.ITEM_FILE_PATH);
            mMimeType = getArguments().getString(Constants.ITEM_MIME_TYPE);
        }

        // retrieve saved state
        if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(Constants.ITEM_FILE_PATH);
            mMimeType = savedInstanceState.getString(Constants.ITEM_MIME_TYPE);
        }

        // load image
        Picasso.with(getActivity())
                .load(new File(mFilePath))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.action_video_placeholder)
                .error(R.drawable.action_video_placeholder)
                .into(image);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete, menu);
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
    public void onClick(View v) {
        // display photo details
        getContract().displayPhotoInfo(mId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ITEM_FILE_PATH, mFilePath);
    }



}
