package com.example.demoapp.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.CursorRecyclerViewAdapter;
import com.example.demoapp.common.Utils;
import com.example.demoapp.custom.CustomItemDecoration;
import com.example.demoapp.event.ModelLoadedEvent;

import de.greenrobot.event.EventBus;

public class MainActivityFragment extends ContractFragment<MainActivityFragment.Contract>{

    // private List<NoteItem> mList;
    private CustomCursorRecyclerViewAdapter mAdapter;
    private Cursor mCursor = null;
    private TextView mEmptyView;


    public interface Contract {
        // database tasks
        void deleteItemTask(long itemId);

        // onClick methods
        void onItemClick(long id, String title, String description);
        void onItemClick(long id, String title, String filePath, String thumbnailPath, String mimeType);
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
        mAdapter = new CustomCursorRecyclerViewAdapter(getActivity(), mCursor);
        if (isAdded())
            recyclerView.setAdapter(mAdapter);

        // TODO shoe empty view when adapter empty



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
        // if (mAdapter.getCursor() != null && mAdapter.getCursor().getCount() > 0) {
        showHideEmpty();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEventMainThread(ModelLoadedEvent event) {
//        Cursor cursor = event.getModel();
//        if (cursor.moveToFirst()) {
//            do {
//                Timber.i("%s: id: %s, title: %s" ,
//                        Constants.LOG_TAG, cursor.getString(cursor.getColumnIndex(Constants.ITEM_ID)),
//                        cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE)));
//            } while (cursor.moveToNext());
//        }

        // passed the retrieved cursor to the adapter
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
    public class CustomCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<CustomViewHolder> {

        public CustomCursorRecyclerViewAdapter(Context context, Cursor cursor) {
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
                case Constants.ITEM_VIDEO_NOTE:
                case Constants.ITEM_AUDIO_NOTE:
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
                case Constants.ITEM_VIDEO_NOTE:
                case Constants.ITEM_AUDIO_NOTE:
                    mThumbnail = (ImageView) itemView.findViewById(R.id.item_thumbnail);
                    break;
            }
        }

        public void bindViewHolder(Cursor cursor) {
            mId = cursor.getLong(cursor.getColumnIndex(Constants.ITEM_ID));
            mTitleText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));

            switch (mViewType) {
                case Constants.ITEM_TEXT_NOTE:
                    mDescriptionText = cursor.getString(cursor.getColumnIndex(Constants.ITEM_DESCRIPTION));
                    mTitle.setText(mTitleText);
                    break;
                case Constants.ITEM_VIDEO_NOTE:
                case Constants.ITEM_AUDIO_NOTE:
                    mFilePath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_FILE_PATH));
                    mThumbnailPath = cursor.getString(cursor.getColumnIndex(Constants.ITEM_THUMBNAIL_PATH));
                    mMimeType = cursor.getString(cursor.getColumnIndex(Constants.ITEM_MIME_TYPE));
                    Utils.loadThumbnail(getActivity(), mThumbnailPath, mThumbnail);
                    break;
            }

        }

        @Override
        public void onClick(View v) {

            switch (mViewType) {
                case Constants.ITEM_TEXT_NOTE:
                    getContract().onItemClick(mId, mTitleText, mDescriptionText);
                    break;
                case Constants.ITEM_VIDEO_NOTE:
                case Constants.ITEM_AUDIO_NOTE:
                    getContract().onItemClick(mId, mTitleText, mFilePath, mThumbnailPath, mMimeType);
                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            // TODO
            getContract().onItemLongClick(v.getId());
            return true;
        }

    }



}
