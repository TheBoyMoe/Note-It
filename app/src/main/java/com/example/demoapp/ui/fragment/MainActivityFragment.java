package com.example.demoapp.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.ContractFragment;
import com.example.demoapp.common.CursorRecyclerViewAdapter;
import com.example.demoapp.custom.CustomItemDecoration;
import com.example.demoapp.event.ModelLoadedEvent;
import com.example.demoapp.model.NoteItem;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivityFragment extends ContractFragment<MainActivityFragment.Contract>{

    private List<NoteItem> mList;
    private CustomCursorRecyclerViewAdapter mAdapter;
    private Cursor mCursor;

    public interface Contract {
        // database tasks
        void deleteItemTask(long itemId);
        void updateItemTask(long itemId, String title, String description);

        // onClick method
        void onItemClick(String title);
        void onItemLongClick(long itemId);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new CustomItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.dimen_vertical_space),
                getResources().getDimensionPixelSize(R.dimen.dimen_horizontal_space)));

        // query the database and populate the adapter
        // Utils.queryAllItems(getActivity());

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
    }


    // Custom adapter and view holder
    public class CustomCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<CustomViewHolder> {

        public CustomCursorRecyclerViewAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, Cursor cursor) {
            // retrieve item from cursor
            if(cursor != null) {
                holder.bindListItem(cursor);
            }
        }

    }

    private class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        TextView mTitle;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.item_title);
        }

        public void bindListItem(Cursor cursor) {
            String title = cursor.getString(cursor.getColumnIndex(Constants.ITEM_TITLE));
            mTitle.setText(title);
        }

        @Override
        public void onClick(View v) {
            // forward up to hosting activity via interface
            getContract().onItemClick(mTitle.getText().toString());
        }

        @Override
        public boolean onLongClick(View v) {
            getContract().onItemLongClick(v.getId());
            return true;
        }
    }



    //    private class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
//
//        @Override
//        public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.list_item, parent, false);
//            return new ListItemViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(ListItemViewHolder holder, int position) {
//            DummyItem item = mList.get(position);
//            holder.bindListItem(item);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mList.size();
//        }
//
//    }


}
