package com.example.demoapp.thread;


import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.demoapp.common.Constants;
import com.example.demoapp.model.DatabaseHelper;
import com.example.demoapp.ui.activity.InfoNoteActivity;

import timber.log.Timber;

public class LoadInfoItemTask extends AsyncTask<Long, Void, Cursor>{

    private Activity mContext;

    public LoadInfoItemTask(Activity context) {
        mContext = context;
    }

    @Override
    protected Cursor doInBackground(Long... params) {
        Timber.i("%s: Execute database query");
        Long itemId = params[0];
        return DatabaseHelper.getInstance(mContext).loadItem(mContext, itemId);
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        if (cursor != null) {
            // query the database and launch the info activity
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));
            String title = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
            InfoNoteActivity.launch(mContext, id, title, description);
            cursor.close();
        }
    }


}
