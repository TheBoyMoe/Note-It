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

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.Utils;

public class TextNoteFragment extends ContractFragment<TextNoteFragment.Contract> {

    private EditText mTitle;
    private EditText mDescription;
    private long mId = 0;

    public interface Contract {
        void saveTextNote(String title, String description);
        void updateTextNote(long id, String title, String description);
        void quit();
        void delete(long id);
    }

    public TextNoteFragment(){}

    public static TextNoteFragment newInstance() {
        return new TextNoteFragment();
    }

    public static TextNoteFragment newInstance(long id, String title, String description) {
        TextNoteFragment fragment = new TextNoteFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ITEM_ID, id);
        args.putString(Constants.ITEM_TITLE, title);
        args.putString(Constants.ITEM_DESCRIPTION, description);
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
        View view = inflater.inflate(R.layout.fragment_text_note, container,false);

        // add the toolbar, enabling the up arrow to save notes
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            Utils.setupToolbar(getActivity(), toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // retrieve title & description and propagate upto hosting activity
                    String title = mTitle.getText() != null ? mTitle.getText().toString() : "";
                    String description = mDescription.getText() != null ? mDescription.getText().toString() : "";

                    if (title.isEmpty()) {
                        getContract().quit();
                    } else {
                        if (mId > 0) {
                            updateAndQuit(title, description);
                        } else {
                            saveAndQuit(title, description);
                        }
                    }
                }
            });
        }


        mTitle = (EditText) view.findViewById(R.id.note_text_title);
        mDescription = (EditText) view.findViewById(R.id.note_text_description);

        if (getArguments() != null){
            mId = getArguments().getLong(Constants.ITEM_ID);
            mTitle.setText(getArguments().getString(Constants.ITEM_TITLE));
            mDescription.setText(getArguments().getString(Constants.ITEM_DESCRIPTION));
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete_black, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {
            case R.id.action_delete:
                getContract().delete(mId);
                return true;
            case android.R.id.home:
                getActivity().supportFinishAfterTransition();
                // getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndQuit(String title, String description) {
        getContract().saveTextNote(title, description);
    }

    private void updateAndQuit(String title, String description) {
        getContract().updateTextNote(mId, title, description);
    }


}
