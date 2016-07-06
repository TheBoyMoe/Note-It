package com.example.demoapp.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
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

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.MultiChoiceModeListener;
import com.example.demoapp.common.Utils;
import com.example.demoapp.custom.CustomItemDecoration;
import com.example.demoapp.custom.CustomMultiChoiceCursorRecyclerViewAdapter;
import com.example.demoapp.event.ModelLoadedEvent;
import com.example.demoapp.thread.DeleteItemsThread;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class MainActivityFragment extends ContractFragment<MainActivityFragment.Contract>
        implements MultiChoiceModeListener{

    private CustomRecyclerViewAdapter mAdapter;
    private Cursor mCursor = null;
    private TextView mEmptyView;

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
            // TODO use Material Dialog to confirm deletion

            // determine all items that were selected and delete from the database
            SparseBooleanArray selectedItems = mAdapter.getSelectedPositions();

            Timber.i("%s selected items: %s, total no items: %d", Constants.LOG_TAG, selectedItems, mAdapter.getItemCount());

            Cursor cursor = mAdapter.getCursor();
            ArrayList<String> selectedIds = new ArrayList<>();
            String id = null;
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                if (selectedItems.get(i)) {
                    if (cursor != null && cursor.moveToPosition(i)) {
                        id = String.valueOf(cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID)));
                    }
                    selectedIds.add(id);
                }
            }
            Timber.i("%s selected ids: %s", Constants.LOG_TAG, selectedIds);
            // convert array list to string array
            String[] idArray = selectedIds.toArray(new String[selectedIds.size()]);
            // execute delete thread
            new DeleteItemsThread(getActivity(), idArray).start();

            mode.finish();
        }

        return true;
    }


    public interface Contract {
        // database tasks
        void deleteItemTask(long itemId);

        // onClick methods
        void onNoteItemClick(long id, String title, String description);
        void onAudioItemClick(long id, String title, String description, String filePath);
        void onVideoItemClick(long id, String title, String description, String filePath, String thumbnailPath, String mimeType);
        void onItemLongClick(long itemId); // TODO
    }

    public MainActivityFragment() {}

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

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
        recyclerView.addItemDecoration(new CustomItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_vertical_space), getResources().getDimensionPixelSize(R.dimen.dimen_horizontal_space)));
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
    public class CustomRecyclerViewAdapter extends CustomMultiChoiceCursorRecyclerViewAdapter<CustomViewHolder> {

        public CustomRecyclerViewAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = null;
            switch (viewType) {
                case Constants.ITEM_TEXT_NOTE:
                    view = inflater.inflate(R.layout.item_text, parent, false);
                    break;
                case Constants.ITEM_AUDIO_NOTE:
                    view = inflater.inflate(R.layout.item_audio, parent, false);
                    break;
                case Constants.ITEM_VIDEO_NOTE:
                    view = inflater.inflate(R.layout.item_thumbnail, parent, false);
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
                holder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(),
                    isSelected(position) ? R.color.colorDraggingBackgroundState : R.color.colorPrimaryBackground));
            }
        }

        @Override
        public int getItemViewType(int position) {
            // default type returned 0, define the specific value to return,
            // which can be tested for in onCreateViewHolder()
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            int type = cursor.getInt(cursor.getColumnIndex(Constants.ITEM_TYPE));
            switch (type) {
                case Constants.ITEM_TEXT_NOTE:
                    return Constants.ITEM_TEXT_NOTE;
                case Constants.ITEM_VIDEO_NOTE:
                    return Constants.ITEM_VIDEO_NOTE;
                case Constants.ITEM_AUDIO_NOTE:
                    return Constants.ITEM_AUDIO_NOTE;
            }
            return -1;
        }


    }

    private class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView mTitle;
        ImageView mThumbnail;

        long mId;
        int mViewType;
        String mTitleText;
        String mDescriptionText;
        String mFilePath;
        String mThumbnailPath;
        String mMimeType;


        public CustomViewHolder(View itemView, int viewType) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mViewType = viewType;
            switch (viewType) {
                case Constants.ITEM_TEXT_NOTE:
                    mTitle = (TextView) itemView.findViewById(R.id.item_title);
                    break;
                case Constants.ITEM_AUDIO_NOTE:
                case Constants.ITEM_VIDEO_NOTE:
                    mThumbnail = (ImageView) itemView.findViewById(R.id.item_thumbnail);
                    break;
            }
        }

        public void bindViewHolder(Cursor cursor) {
            mId = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));
            mTitleText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
            mDescriptionText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));

            switch (mViewType) {
                case Constants.ITEM_TEXT_NOTE:
                    mTitle.setText(mTitleText);
                    break;
                case Constants.ITEM_VIDEO_NOTE:
                    mFilePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
                    mThumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_THUMBNAIL_PATH));
                    mMimeType = cursor.getString(cursor.getColumnIndex(Constants.ITEM_MIME_TYPE));
                    Utils.loadThumbnail(getActivity(), mThumbnailPath, mThumbnail);
                    break;
                case Constants.ITEM_AUDIO_NOTE:
                    mFilePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
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
                    case Constants.ITEM_TEXT_NOTE:
                        getContract().onNoteItemClick(mId, mTitleText, mDescriptionText);
                        break;
                    case Constants.ITEM_AUDIO_NOTE:
                        getContract().onAudioItemClick(mId, mTitleText, mDescriptionText, mFilePath);
                        break;
                    case Constants.ITEM_VIDEO_NOTE:
                        getContract().onVideoItemClick(mId, mTitleText, mDescriptionText, mFilePath, mThumbnailPath, mMimeType);
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mAdapter.toggleSelected(getAdapterPosition());

            // TODO ?? move to onActionItemClicked()
            // getContract().onItemLongClick(v.getId());
            return true;
        }

    }



}
