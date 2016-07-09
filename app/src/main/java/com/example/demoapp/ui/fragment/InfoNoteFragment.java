package com.example.demoapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class InfoNoteFragment extends ContractFragment<InfoNoteFragment.Contract>{

    public interface Contract {
        void updateInfoNote(long id, String title, String description);
        void quit();
    }

    private EditText mTitle;
    private EditText mDescription;
    private long mId;
    private String mTitleText;
    private String mDescriptionText;

    public InfoNoteFragment() {}

    public static InfoNoteFragment newInstance(long id, String title, String description) {
        InfoNoteFragment fragment = new InfoNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_note, container,false);

        // add the toolbar, enabling the up arrow to save notes
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                // hide title by default
                actionBar.setDisplayShowTitleEnabled(false);
            }
            // set navigation icon and color
            toolbar.setNavigationIcon(Utils.tintDrawable(ContextCompat
                    .getDrawable(getActivity(), R.drawable.action_back), R.color.colorButtonIcon));
            // set title text color
            //toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // retrieve title & description, save to the database if req'd
                    String title = mTitle.getText().toString();
                    String description = mDescription.getText().toString();
                    if (mTitleText.equals(title) && mDescriptionText.equals(description)) {
                        // if neither has changed, quit
                        getContract().quit();
                    } else {
                        // either/both have changed, update the database record
                        getContract().updateInfoNote(mId, title, description);
                    }
                }
            });
        }

        mTitle = (EditText) view.findViewById(R.id.note_text_title);
        mDescription = (EditText) view.findViewById(R.id.note_text_description);

        if (getArguments() != null){
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitleText = getArguments().getString(Constants.ITEM_TITLE);
            mDescriptionText = getArguments().getString(Constants.ITEM_DESCRIPTION);
            mTitle.setText(mTitleText);
            mDescription.setText(mDescriptionText);
        }

        return view;
    }




}
