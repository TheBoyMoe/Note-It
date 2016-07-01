package com.example.demoapp.thread;

import android.content.Context;
import android.os.Process;

import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;

import timber.log.Timber;

public class DeleteItemThread extends Thread{

    private long mId;
    private Context mContext;

    public DeleteItemThread(Context context, long itemId) {
        mId = itemId;
        mContext = context;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            DatabaseHelper.getInstance(mContext).deleteTaskItem(mContext, mId);
        } catch (Exception e) {
            Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
        }
        // trigger ui update
        Utils.queryAllItems(mContext);
    }

}
