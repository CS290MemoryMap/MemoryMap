package compsci290.duke.edu.memorymap.memory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.database.MarkerTagDbHandler;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;
import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;
import compsci290.duke.edu.memorymap.memory.MarkerTag;
import compsci290.duke.edu.memorymap.memory.MarkerTagAdapter;

/**
 * TODO: rotating app when different sort selected does not save
 **/

public class MemoryListActivity extends AppCompatActivity implements RecyclerViewClickListener{
    private static final String MARKERTAG = "markertag";
    private static final int OPEN_MEMORY = 1;
    private static final String TAG = "MemoryListActivity";

    private List<MarkerTag> mMarkerTagList;
    private RecyclerView mRecyclerView;
    private MarkerTagDbHandler mDbHandler;
    private MarkerTagAdapter mAdapter;

    public ProgressDialog mProgressDialog;

    /* added for firebase database code */
    private FirebaseDatabaseHandler mFirebaseDbHandler;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_memorylist);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // get MarkerTag set from database, convert to ArrayList
        mDbHandler = new MarkerTagDbHandler();
        /* added for firebase database code */
        mFirebaseDbHandler = new FirebaseDatabaseHandler();
        mMarkerTagList = Collections.emptyList();
        queryMyMarkerTagList();
        // addToDatabase();
        //mMarkerTagList = mDbHandler.queryAllMarkerTags();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
    }

    /*
        Used for testing
     */
    private void addToDatabase() {
        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.uploadmedia);

        double lat = 123456789;
        double lon = 987654321;
        /*TODO: uncomment MarkerTag markerTag = new MarkerTag("tag 1", "Apr 16, 2017", "Details of this tag", img, lat, lon);
        long id = mDbHandler.insertMarkerTag(markerTag);*/
        //CharSequence text = "Inserted ID " + id;

        lat = 123456789;
        lon = 887654321;
        /*TODO: uncomment markerTag = new MarkerTag("tag 2", "Apr 16, 2017", "Details of this tag", img, lat, lon);
        id = mDbHandler.insertMarkerTag(markerTag);

        lat = 987654321;
        lon = 123456789;
        markerTag = new MarkerTag("tag 3", "Apr 18, 2017", "Details of this tag", img, lat, lon);
        id = mDbHandler.insertMarkerTag(markerTag);*/

    }

    private void initializeAdapter() {
        mAdapter = new MarkerTagAdapter(mMarkerTagList, this, this);
        if (mMarkerTagList.size() == 0) {
            Log.d(TAG, "MarkerTag list empty.");
        } else {
            Log.d(TAG, "MarkerTag list populated.");
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateList(List<MarkerTag> newList) {
        mMarkerTagList = newList;
        mAdapter.swap(mMarkerTagList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String menuString = (String) item.getTitle();
        if(menuString.equals(getResources().getString(R.string.location_sort))) {
            //mMarkerTagList = mDbHandler.querySortByLongLat();
            queryMyMarkerTagListByLocation();
        }
        else if (menuString.equals(getResources().getString(R.string.time_sort))) {
            //mMarkerTagList = mDbHandler.queryAllMarkerTags();
            queryMyMarkerTagList();
        }
        else if (menuString.equals(getResources().getString(R.string.title_sort))) {
            queryMyMarkerTagListByTitle();
        }
        else if (menuString.equals(getResources().getString(R.string.date_sort))) {
            queryMyMarkerTagListByDate();
        }
        //mAdapter.swap(mMarkerTagList);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // mDbHandler.deleteMarkerTagList(mMarkerTagList);
        mDbHandler.closeDatabase();
        super.onDestroy();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

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

    private void queryMyMarkerTagListByTitle() {
        Log.d(TAG, "Query by title called");
        showProgressDialog();
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mFirebaseDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .orderByChild(MarkerTagModel.CHILD_NAME_TITLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Query by title onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter user MarkerTag only
                            if (markerTagModel.getUserId().equals(mFirebaseDbHandler.getUserId())) {
                                markerTagList.add(new MarkerTag((markerTagModel)));
                                Log.d(TAG, "Queried by title " + markerTagModel.getTitle());
                            }
                        }

                        // MarkerTag list save in local variable markerTagList
                        hideProgressDialog();
                        updateList(markerTagList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error querying by title: " + databaseError.getMessage());
                        // TODO how do you want to handle error querying from database?
                    }
                });
    }

    private void queryMyMarkerTagListByDate() {
        Log.d(TAG, "Query by date called");
        showProgressDialog();
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mFirebaseDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .orderByChild(MarkerTagModel.CHILD_NAME_DATE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Query by date onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter user MarkerTag only
                            if (markerTagModel.getUserId().equals(mFirebaseDbHandler.getUserId())) {
                                markerTagList.add(new MarkerTag((markerTagModel)));
                                Log.d(TAG, "Queried by date: " + markerTagModel.getTitle());
                            }
                        }

                        // MarkerTag list save in local variable markerTagList
                        hideProgressDialog();
                        updateList(markerTagList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error: query by date: " + databaseError.getMessage());
                        // TODO how do you want to handle error querying from database?
                    }
                });
    }

    private void queryMyMarkerTagListByLocation() {
        Log.d(TAG, "Query by location called");
        showProgressDialog();
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mFirebaseDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .orderByChild(MarkerTagModel.CHILD_NAME_LOCATION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Query by location onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter user MarkerTag only
                            if (markerTagModel.getUserId().equals(mFirebaseDbHandler.getUserId())) {
                                markerTagList.add(new MarkerTag((markerTagModel)));
                                Log.d(TAG, "Queried by location: " + markerTagModel.getTitle());
                            }
                        }

                        // MarkerTag list save in local variable markerTagList
                        hideProgressDialog();
                        updateList(markerTagList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error: query by location: " + databaseError.getMessage());
                        // TODO how do you want to handle error querying from database?
                    }
                });
    }

    private void queryMyMarkerTagList() {
        showProgressDialog();
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mFirebaseDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter user MarkerTag only
                            if (markerTagModel.getUserId().equals(mFirebaseDbHandler.getUserId())) {
                                markerTagList.add(new MarkerTag((markerTagModel)));
                                Log.d(TAG, "QUERIED MarkerTag " + markerTagModel.getTitle());
                            }
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

    private void queryPublicMarkerTagList() {
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
                        updateList(markerTagList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error Querying Data: " + databaseError.getMessage());
                    }
                });
    }

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
        bundle.putParcelable(MARKERTAG, mMarkerTag);
        intent.putExtras(bundle);
        startActivity(intent);
        //Intent intent = listToMemoryIntent(mMarkerTag, MemoryActivity.class);
        //startActivityForResult(intent, OPEN_MEMORY);
    }

    /**
     * Returns an intent to a specified class that has a bundle with a MarkerTag
     * parcelable object already inside it
     *
     * @param  mTag  the MarkerTag to include in the intent
     * @param  toClass the class to which the intent will be passed
     * @return      the intent
     */

    private Intent listToMemoryIntent(MarkerTag mTag, Class<?> toClass) {
        Intent intent = new Intent(MemoryListActivity.this, toClass);
        Bundle bundle = new Bundle();
        if (mTag == null) {
            Log.d(TAG, "listToMemoryIntent mTag is null");
        }
        bundle.putParcelable(MARKERTAG, mTag);
        intent.putExtras(bundle);
        return intent;
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