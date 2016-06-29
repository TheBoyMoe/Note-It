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
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class TextNoteFragment extends ContractFragment<TextNoteFragment.Contract> {

    private EditText mTitle;
    private EditText mDescription;

    public interface Contract {
        void saveTextNote(String title, String description);
    }

    public TextNoteFragment(){}

    public static TextNoteFragment newInstance() {
        return new TextNoteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_note, container,false);

        // add the toolbar, enabling the up arrow to save notes
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAndQuit();
                }
            });
        }

        mTitle = (EditText) view.findViewById(R.id.note_text_title);
        mDescription = (EditText) view.findViewById(R.id.note_text_description);

        return view;
    }


    private void saveAndQuit() {
        // retrieve title & description and propagate upto hosting activity
        String title = mTitle.getText() != null ? mTitle.getText().toString() : "";
        String description = mDescription.getText() != null ? mDescription.getText().toString() : "";
        getContract().saveTextNote(title, description);
    }

    private void setupToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) {
                // hide title by default
                actionBar.setDisplayShowTitleEnabled(false);
                // set navigation icon and color
                toolbar.setNavigationIcon(Utils.tintDrawable(ContextCompat
                        .getDrawable(getActivity(), R.drawable.action_back), R.color.colorIcon));
                // set title text color
                toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
            }
        }
    }

}
