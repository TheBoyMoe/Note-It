package com.example.demoapp.common;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.demoapp.R;

public class NoteFragment extends Fragment{

    public NoteFragment(){}

    protected void setupToolbar(Toolbar toolbar) {
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
