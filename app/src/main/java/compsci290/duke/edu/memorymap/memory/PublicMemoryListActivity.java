package compsci290.duke.edu.memorymap.memory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;
import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;

/**
 * TODO: rotating app when different sort selected does not save
 **/

public class PublicMemoryListActivity extends AppCompatActivity implements RecyclerViewClickListener{
    private static final String MARKERTAG = "markertag";
    private static final int OPEN_MEMORY = 1;
    private static final String TAG = "PubMemoryListActivity";

    protected List<MarkerTag> mMarkerTagList;
    protected RecyclerView mRecyclerView;
    protected MarkerTagAdapter mAdapter;

    public ProgressDialog mProgressDialog;

    /* added for firebase database code */
    protected FirebaseDatabaseHandler mFirebaseDbHandler;

    /**
     * Sets the layout and initializes a Firebase handler
     * Query public MarkerTags to be displayed in RecyclerView
     **/
    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_memorylist);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // get MarkerTag set from database, convert to ArrayList
        /* added for firebase database code */
        mFirebaseDbHandler = new FirebaseDatabaseHandler();
        mMarkerTagList = Collections.emptyList();
        queryPublicMarkerTagList();
        // addToDatabase();
        //mMarkerTagList = mDbHandler.queryAllMarkerTags();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
    }

    /**
     * Initialize and set custom adapter for RecyclerView
     **/
    private void initializeAdapter() {
        mAdapter = new MarkerTagAdapter(mMarkerTagList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Updates RecyclerView in MemoryList
     *
     * @param  newList  the updated list to be shown after a new query
     **/
    private void updateList(List<MarkerTag> newList) {
        if (mMarkerTagList != null && newList != null) {
            mMarkerTagList = newList;
            mAdapter.swap(mMarkerTagList);
        } else if (newList != null) {
            mMarkerTagList = Collections.emptyList();
            mMarkerTagList = newList;
            Log.d(TAG, "newList is null");
        }
    }

    /**
     * Defines showing ProgressDialog as database is being queried
     **/
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    /**
     * Defines hiding Progress Dialog
     **/
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    /**
     * queries first 20 public MarkerTags from Firebase Database
     * displays in RecyclerView
     **/
    protected void queryPublicMarkerTagList() {
        showProgressDialog();
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        // Query for most recent 20 public MarkerTag
        mFirebaseDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .orderByChild(MarkerTagModel.CHILD_NAME_PUBLICMARKERTAG)
                .equalTo(true).limitToFirst(20)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter public MarkerTag only (most recent 20)
//                            if (markerTagModel.isPublicMarkerTag()) {
                            markerTagList.add(new MarkerTag((markerTagModel)));
                            Log.d(TAG, "QUERIED MarkerTag " + markerTagModel.getTitle());
//                            }
                        }

                        // MarkerTag list save in local variable markerTagList
                        hideProgressDialog();
                        if (mMarkerTagList.size() == 0) {
                            initializeAdapter();
                        }
                        updateList(markerTagList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error Querying Data: " + databaseError.getMessage());
                    }
                });
    }

    /**
     * Send MarkerTag to be displayed in view-only MemoryActivity
     *
     * @param  v  View of item clicked
     * @param position  position of item clicked
     **/
    @Override
    public void recyclerViewListClicked(View v, int position) {
        // Retrieve MarkerTag to open in MemoryActivity
        MarkerTag mMarkerTag = mMarkerTagList.get(position);
        if (mMarkerTag == null) {
            Log.d(TAG, "MarkerTag is null");
        }
        Intent intent = new Intent(this, MemoryActivity.class);
        Bundle bundle = new Bundle();
        if (mMarkerTag == null) {
            Log.d(TAG, "listToMemoryIntent mTag is null");
        }
        bundle.putBoolean("PublicList", true);
        bundle.putParcelable(MARKERTAG, mMarkerTag);
        intent.putExtras(bundle);
        Log.d(TAG, "Sending MarkerTag to MemoryActivity");
        startActivity(intent);
    }

    /**
     * Handles the result from an activity.
     * Currently defined only for MemoryActivity passing a result
     *
     * @param  requestCode  code identifying the previous activity
     * @param  resultCode   describes if the previous activity was a success
     * @param  data         intent from the previous activity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if startActivityForResult requested opening memory
        if (requestCode == OPEN_MEMORY) {
            Toast.makeText(this, "requestCode == OPEN_MEMORY", Toast.LENGTH_SHORT).show();
        }
    }
}
