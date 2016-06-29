package com.example.demoapp.ui.activity;

import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.demoapp.R;
import com.example.demoapp.common.Constants;
import com.example.demoapp.common.Utils;
import com.example.demoapp.model.DatabaseHelper;
import com.example.demoapp.ui.fragment.MainActivityFragment;
import com.example.demoapp.ui.fragment.ModelFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Contract{

    private static final String MODEL_FRAGMENT = "model_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // cache a reference to a fragment
        MainActivityFragment recyclerFragment =
            (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (recyclerFragment == null) {
            recyclerFragment = MainActivityFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, recyclerFragment)
                    .commit();
        }

        // cache a reference to the model fragment
        Fragment modelFragment = getSupportFragmentManager().findFragmentByTag(MODEL_FRAGMENT);
        if (modelFragment == null) {
            modelFragment = ModelFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(modelFragment, MODEL_FRAGMENT)
                    .commit();
        }

        FloatingActionButton addItem = (FloatingActionButton) findViewById(R.id.fab);
        if (addItem != null) {
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // launch dialog to allow user to create task
//                    new MaterialDialog.Builder(MainActivity.this)
//                            .title("Define a task you wish to complete")
//                            .inputType(InputType.TYPE_CLASS_TEXT)
//                            .inputRange(2, 100)
//                            .input(null, null, new MaterialDialog.InputCallback() {
//                                @Override
//                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                    // fetch entered text and save to dbase
//                                    long taskId = Utils.generateCustomId();
//                                    String title = input.toString();
//                                    new InsertItemThread(taskId, title, "add description").start();
//                                }
//                            })
//                            .positiveText("Save")
//                            .negativeText("Cancel")
//                            .show();


                    // launch text note activity
                    TextNoteActivity.launch(MainActivity.this);
                }
            });
        }

    }


    // contract methods
    @Override
    public void deleteItemTask(long itemId) {
        new DeleteItemThread(itemId).start();
    }


    @Override
    public void onItemClick(long id, String title, String description) {
        // launch activity displaying note
        TextNoteActivity.launch(MainActivity.this, id, title, description);
    }

    @Override
    public void onItemLongClick(long itemId) {
        // TODO ?? delete item or delete multiple(via cab)
        Utils.showToast(this, "Item clicked on: " + itemId);
    }
    // END



    // delete item from database via a bkgd thread
    class DeleteItemThread extends Thread {

        private long mId;

        public DeleteItemThread(long itemId) {
            mId = itemId;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(MainActivity.this).deleteTaskItem(MainActivity.this, mId);
            } catch (Exception e) {
                Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
            }
            // trigger ui update
            Utils.queryAllItems(MainActivity.this);
        }
    }



}
