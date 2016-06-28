package com.example.demoapp.event;

import android.database.Cursor;

public class ModelLoadedEvent extends BaseEvent{

    private final Cursor mModel;

    public ModelLoadedEvent(Cursor model) {
        mModel = model;
    }

    public Cursor getModel() {
        return mModel;
    }
}
