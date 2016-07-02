package com.example.demoapp.model;

import com.example.demoapp.common.Constants;

public class NoteItem {

    private long mId;
    private int mType;
    private String mTitle;
    private String mDescription;
    private String mFilePath;
    private String mThumbnailPath;
    private String mMimeType;

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String toString() {
        return String.format("%s id: %d, title: %s", Constants.LOG_TAG, getId(), getTitle());
    }


}
