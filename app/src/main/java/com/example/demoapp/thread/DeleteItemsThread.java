package com.example.demoapp.thread;

import android.content.Context;
import android.os.Process;

import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;

import timber.log.Timber;

public class DeleteItemsThread extends Thread{

    private String[] mIds;
    private Context mContext;

    public DeleteItemsThread(Context context, String[] itemIds) {
        mIds = itemIds;
        mContext = context;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            DatabaseHelper.getInstance(mContext).deleteItems(mContext, mIds);
        } catch (Exception e) {
            Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
        }
        // trigger ui update
        Utils.queryAllItems(mContext);
    }

}
