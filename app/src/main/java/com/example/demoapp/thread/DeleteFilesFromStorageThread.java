package com.example.demoapp.thread;

import android.content.Context;
import android.database.Cursor;
import android.os.Process;

import com.example.demoapp.common.Constants;
import com.example.demoapp.model.DatabaseHelper;

import java.io.File;

import timber.log.Timber;

public class DeleteFilesFromStorageThread extends Thread{

    private Context mContext;
    private String[] mItems;

    public DeleteFilesFromStorageThread(Context context, String[] items) {
        mContext = context;
        mItems = items;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Cursor cursor = null;
        try {
            cursor = DatabaseHelper.getInstance(mContext).loadItems(mContext);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));
                    String filePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
                    String thumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_THUMBNAIL_PATH));
                    Timber.i("%s: id: %d, filePath: %s, thumbnailPath: %s", Constants.LOG_TAG, id, filePath, thumbnailPath);
                    for (String mItem : mItems) {
                        if (mItem.equals(String.valueOf(id))) {
                            if (filePath != null && !filePath.isEmpty()) {
                                deleteFile(filePath);
                            }
                            if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                                deleteFile(thumbnailPath);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Timber.e("%s: error deleting file from external storage, %s", Constants.LOG_TAG, e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteFile(String thumbnailPath) {
        File temp = new File(thumbnailPath);
        temp.delete();
    }


}
