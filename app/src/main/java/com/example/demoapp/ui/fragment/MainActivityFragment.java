package com.example.demoapp.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.MultiChoiceModeListener;
import com.example.demoapp.common.Utils;
import com.example.demoapp.custom.CustomItemDecoration;
import com.example.demoapp.custom.CustomMultiChoiceCursorRecyclerViewAdapter;
import com.example.demoapp.event.ModelLoadedEvent;
import com.example.demoapp.thread.DeleteFilesFromStorageThread;
import com.example.demoapp.thread.DeleteItemsThread;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivityFragment extends ContractFragment<MainActivityFragment.Contract>
        implements MultiChoiceModeListener {

    private CustomRecyclerViewAdapter mAdapter;
    private Cursor mCursor = null;
    private TextView mEmptyView;

    // impl MultiChoiceSelection
    @Override
    public void onItemSelectionChanged(ActionMode mode, int position, boolean selected) {
        mode.setTitle(mAdapter.getSelectedCount() + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, MenuInflater inflater, Menu menu) {
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode() {
        // no-op
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            // determine all items that were selected and delete from the database
            final SparseBooleanArray selectedItems = mAdapter.getSelectedPositions();

            // confirm deletion
            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.note_deletion_dialog_title))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            Cursor cursor = mAdapter.getCursor();
                            ArrayList<String> selectedIds = new ArrayList<>();
                            String id = null;
                            if (cursor != null) {
                                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                    if (selectedItems.get(i)) {
                                        if (cursor.moveToPosition(i)) {
                                            id = String.valueOf(cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID)));
                                            selectedIds.add(id);
                                        }
                                    }
                                }
                                // convert array list to string array
                                String[] idArray = selectedIds.toArray(new String[selectedIds.size()]);
                                // delete file from external storage
                                new DeleteFilesFromStorageThread(getActivity(), idArray).start();
                                // delete items from database
                                new DeleteItemsThread(getActivity(), idArray).start();
                            }
                        }
                    })
                    .positiveText(getString(R.string.dialog_positive_text))
                    .negativeText(getString(R.string.dialog_negative_text))
                    .show();

            mode.finish();
        }

        return true;
    }
    // END

    public interface Contract {
        // onClick methods
        void onNoteItemClick(long id, String title, String description);
        void onAudioItemClick(long id, String title, String description, String filePath);
        void onVideoItemClick(long id, String filePath, String thumbnailPath, String mimeType);
        void onPhotoItemClick(long id, String filePath);
    }

    public MainActivityFragment() {}

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // hide the toolbar shadow on devices API 21+
        View toolbarShadow = view.findViewById(R.id.toolbar_shadow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarShadow.setVisibility(View.GONE);
        }

        // instantiate view and adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.setHasFixedSize(true);

        // use a 3-col grid on screens >= 540dp
        StaggeredGridLayoutManager layoutManager = null;
        Configuration config = getResources().getConfiguration();
        if (config.screenWidthDp >= 540) {
            layoutManager = new StaggeredGridLayoutManager(3, 1);
        } else {
            layoutManager = new StaggeredGridLayoutManager(2, 1);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new CustomItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.dimen_vertical_space),
                getResources().getDimensionPixelSize(R.dimen.dimen_vertical_space)));
        mAdapter = new CustomRecyclerViewAdapter(getActivity(), mCursor);
        mAdapter.setMultiChoiceModeListener((AppCompatActivity)getActivity(), this);
        if (isAdded())
            recyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mAdapter.restoreInstanceState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        showHideEmpty();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(ModelLoadedEvent event) {
        // pass the retrieved cursor to the adapter
        mAdapter.changeCursor(event.getModel());
        showHideEmpty();
    }

    private void showHideEmpty() {
        if (mAdapter.getItemCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }


    // Custom adapter and view holder
    public class CustomRecyclerViewAdapter extends
            CustomMultiChoiceCursorRecyclerViewAdapter<CustomViewHolder> {

        public CustomRecyclerViewAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = null;
            switch (viewType) {
                case Constants.ITEM_TYPE_TEXT:
                    view = inflater.inflate(R.layout.item_text, parent, false);
                    break;
                case Constants.ITEM_TYPE_AUDIO:
                    view = inflater.inflate(R.layout.item_audio, parent, false);
                    break;
                case Constants.ITEM_TYPE_VIDEO:
                    view = inflater.inflate(R.layout.item_video, parent, false);
                    break;
                case Constants.ITEM_TYPE_PHOTO:
                    view = inflater.inflate(R.layout.item_photo, parent, false);
                    break;
            }
            return new CustomViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, Cursor cursor) {
            // retrieve item from cursor
            if(cursor != null) {
                holder.bindViewHolder(cursor);
                int position = cursor.getPosition();
                holder.itemView.setBackgroundColor(ContextCompat.getColor(
                    getActivity(), isSelected(position) ? R.color.colorPrimary : R.color.colorSecondaryBackground));
            }
        }

        @Override
        public int getItemViewType(int position) {
            // default type returned 0, define the specific value to return,
            // which can be tested for in onCreateViewHolder()
            Cursor cursor = getCursor();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(position);
                int type = cursor.getInt(cursor.getColumnIndex(Constants.ITEM_TYPE));
                switch (type) {
                    case Constants.ITEM_TYPE_TEXT:
                        return Constants.ITEM_TYPE_TEXT;
                    case Constants.ITEM_TYPE_VIDEO:
                        return Constants.ITEM_TYPE_VIDEO;
                    case Constants.ITEM_TYPE_AUDIO:
                        return Constants.ITEM_TYPE_AUDIO;
                    case Constants.ITEM_TYPE_PHOTO:
                        return Constants.ITEM_TYPE_PHOTO;
                }
            }
            return -1;
        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView mTitle;
        TextView mDescription;
        ImageView mThumbnail;

        long mId;
        int mViewType;
        String mTitleText;
        String mDescriptionText;
        String mFilePath;
        String mPreviewPath;
        String mThumbnailPath;
        String mMimeType;


        public CustomViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mViewType = viewType;
            switch (viewType) {
                case Constants.ITEM_TYPE_TEXT:
                    mTitle = (TextView) itemView.findViewById(R.id.item_title);
                    mDescription = (TextView) itemView.findViewById(R.id.item_description);
                    break;
                case Constants.ITEM_TYPE_PHOTO:
                case Constants.ITEM_TYPE_AUDIO:
                case Constants.ITEM_TYPE_VIDEO:
                    mTitle = (TextView) itemView.findViewById(R.id.item_title);
                    mThumbnail = (ImageView) itemView.findViewById(R.id.item_thumbnail);
                    break;
            }
        }

        public void bindViewHolder(Cursor cursor) {
            mId = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));

            switch (mViewType) {
                case Constants.ITEM_TYPE_VIDEO:
                    mFilePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
                    mMimeType = cursor.getString(cursor.getColumnIndex(Constants.ITEM_MIME_TYPE));
                case Constants.ITEM_TYPE_PHOTO:
                    mTitleText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
                    mPreviewPath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_PREVIEW_PATH));
                    mThumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_THUMBNAIL_PATH));
                    Utils.setTitleText(mTitle, mTitleText);
                    Utils.loadThumbnailWithPicasso(getActivity(), mThumbnailPath, mThumbnail);
                    break;
                case Constants.ITEM_TYPE_AUDIO:
                    mTitleText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
                    mDescriptionText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
                    mFilePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
                    Utils.setTitleText(mTitle, mTitleText);
                    break;
                case Constants.ITEM_TYPE_TEXT:
                    mTitleText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
                    mDescriptionText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
                    mTitle.setText(mTitleText);
                    if (mDescriptionText != null && !mDescriptionText.isEmpty()) {
                        mDescription.setText(mDescriptionText);
                        mDescription.setPadding(0, 8, 0, 0);
                        mDescription.setVisibility(View.VISIBLE);
                    } else {
                        mDescription.setVisibility(View.GONE);
                    }
                    break;
            }
        }

                @Override
        public void onClick(View v) {

            if (mAdapter.isActionModeActive()) {
                mAdapter.toggleSelected(getAdapterPosition());
            }
            else {
                // clicking on text/video/audio note forwards call up to hosting activity
                switch (mViewType) {
                    case Constants.ITEM_TYPE_TEXT:
                        getContract().onNoteItemClick(mId, mTitleText, mDescriptionText);
                        break;
                    case Constants.ITEM_TYPE_AUDIO:
                        getContract().onAudioItemClick(mId, mTitleText, mDescriptionText, mFilePath);
                        break;
                    case Constants.ITEM_TYPE_VIDEO:
                        getContract().onVideoItemClick(mId, mFilePath, mPreviewPath, mMimeType);
                        break;
                    case Constants.ITEM_TYPE_PHOTO:
                        getContract().onPhotoItemClick(mId, mPreviewPath);
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mAdapter.toggleSelected(getAdapterPosition());
            return true;
        }

    }



}
