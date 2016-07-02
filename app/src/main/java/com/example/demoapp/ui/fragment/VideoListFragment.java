package com.example.demoapp.ui.fragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.example.demoapp.R;
import com.example.demoapp.common.ContractFragment;
import com.squareup.picasso.Picasso;

public class VideoListFragment extends ContractFragment<VideoListFragment.Contract> implements
        AdapterView.OnItemClickListener,
        SimpleCursorAdapter.ViewBinder,
        LoaderManager.LoaderCallbacks<Cursor>{


    public interface Contract {
        void onClick(String title, String filePath, String mimeType);
    }

    private GridView mGridView;
    private SimpleCursorAdapter mAdapter;

    public VideoListFragment(){}

    public static VideoListFragment newInstance(){
        return new VideoListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view, container,false);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] from = {MediaStore.Video.Media._ID};
        int[] to = {R.id.item_thumbnail};
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_thumbnail,
                null,
                from,
                to,
                0);
        mAdapter.setViewBinder(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Video.Media.TITLE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(MediaStore.Video.Media._ID)) {
            Uri thumbnailUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    cursor.getInt(columnIndex));

            Picasso.with(getActivity())
                    .load(thumbnailUri.toString())
                    .resize(160, 160)
                    .centerCrop()
                    .placeholder(R.drawable.action_video_placeholder)
                    .error(R.drawable.action_video_placeholder)
                    .into((ImageView) view);

            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // forward call to hosting activity
        Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        getContract().onClick(title, filePath, mimeType);
    }


}
