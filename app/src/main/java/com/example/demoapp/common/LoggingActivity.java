package com.example.demoapp.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

public class LoggingActivity extends AppCompatActivity {

    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("%s %s onCreate()", Constants.LOG_TAG, CLASS_NAME);
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
    public void onBackPressed() {
        super.onBackPressed();
        Timber.i("%s %s onBackPressed()", Constants.LOG_TAG, CLASS_NAME);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.i("%s %s onRestart()", Constants.LOG_TAG, CLASS_NAME);
    }


}