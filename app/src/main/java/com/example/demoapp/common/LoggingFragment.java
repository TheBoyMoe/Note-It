package com.example.demoapp.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

// Log fragment life cycles
public class LoggingFragment extends Fragment {

    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Timber.i("%s %s onAttach()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s %s onCreate()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.i("%s %s onCreateView()", Constants.LOG_TAG, CLASS_NAME);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("%s %s onViewCreated()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("%s %s onStart()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("%s %s onResume()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("%s %s onPause()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("%s %s onStop()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("%s %s onDestroy()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("%s %s onDetach()", Constants.LOG_TAG, CLASS_NAME);
    }

}
