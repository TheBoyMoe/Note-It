package com.example.demoapp.common;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by http://jimmy0251.github.io/effective-recycler-adapter/
 */
public interface MultiChoiceModeListener {

    MultiChoiceModeListener EMPTY_LISTENER = new MultiChoiceModeListener() {
        @Override
        public void onItemSelectionChanged(ActionMode mode, int position, boolean selected) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode() {

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    };

    void onItemSelectionChanged(ActionMode mode, int position, boolean selected);

    boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu);

    void onDestroyActionMode();

    boolean onActionItemClicked(ActionMode mode, MenuItem item);


}
