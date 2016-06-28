package com.example.demoapp.model;

import com.example.demoapp.common.Constants;

public class NoteItem {

    private long mId;
    private String mTitle;
    private String mDescription;

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
