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
        void displayPhotoInfo(long id);
        void delete(long id);
    }

    private long mId;
    private String mPreviewPath;

    public PhotoNoteFragment() {}

    public static PhotoNoteFragment newInstance(long id, String previewPath) {
        PhotoNoteFragment fragment = new PhotoNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_PREVIEW_PATH, previewPath);
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

        }

        ImageView image = (ImageView) view.findViewById(R.id.photo_note_image);
        FloatingActionButton infobtn = (FloatingActionButton) view.findViewById(R.id.action_info_btn);
        infobtn.setOnClickListener(this);
        infobtn.setIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.action_info_btn));

        if (getArguments() != null) {
            mId = getArguments().getLong(Constants.ITEM_ID);
            mPreviewPath = getArguments().getString(Constants.ITEM_PREVIEW_PATH);
        }

        // retrieve saved state
        if (savedInstanceState != null) {
            mPreviewPath = savedInstanceState.getString(Constants.ITEM_PREVIEW_PATH);
        }

        // load image
        Picasso.with(getActivity())
                .load(new File(mPreviewPath))
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
        outState.putString(Constants.ITEM_PREVIEW_PATH, mPreviewPath);
    }


}
