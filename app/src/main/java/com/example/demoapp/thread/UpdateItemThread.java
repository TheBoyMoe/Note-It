package com.example.demoapp.thread;

import android.content.ContentValues;
import android.content.Context;
import android.os.Process;

import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;

import timber.log.Timber;

public class UpdateItemThread extends Thread{

    private ContentValues mValues;
    private Context mContext;

    public UpdateItemThread(Context context, ContentValues values) {
        mValues = values;
        mContext = context;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            DatabaseHelper.getInstance(mContext).updateItem(mContext, mValues);
        } catch (Exception e) {
            Timber.e("%s: error deleting item from the database, %s", Constants.LOG_TAG, e.getMessage());
        }
        // trigger ui update
        Utils.queryAllItems(mContext);
    }
}
