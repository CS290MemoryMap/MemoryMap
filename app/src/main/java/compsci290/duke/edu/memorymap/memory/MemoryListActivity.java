package compsci290.duke.edu.memorymap.memory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.database.MarkerTagDbHandler;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;
import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;
import compsci290.duke.edu.memorymap.memory.MarkerTag;
import compsci290.duke.edu.memorymap.memory.MarkerTagAdapter;

/**
 * Created by taranagar on 4/13/17.
 */

public class MemoryList extends AppCompatActivity {
    private static final String TAG = "MemoryList";

    private ArrayList<MarkerTag> mMarkerTagList;
    private RecyclerView mRecyclerView;
    private MarkerTagDbHandler mDbHandler;
    private MarkerTagAdapter mAdapter;

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
        // addToDatabase();
        mMarkerTagList = mDbHandler.queryAllMarkerTags();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        initializeAdapter();
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
        mAdapter = new MarkerTagAdapter(mMarkerTagList, this);
        mRecyclerView.setAdapter(mAdapter);
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
            mMarkerTagList = mDbHandler.querySortByLongLat();
        }
        else if (menuString.equals(getResources().getString(R.string.time_sort))) {
            mMarkerTagList = mDbHandler.queryAllMarkerTags();
        }
        mAdapter.swap(mMarkerTagList);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // mDbHandler.deleteMarkerTagList(mMarkerTagList);
        mDbHandler.closeDatabase();
        super.onDestroy();
    }

    private void queryMyMarkerTagListByTitle() {
        Log.d(TAG, "Query by title called");
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

                        // TODO: call a method to update your list
                        // MarkerTag list save in local variable markerTagList
                        // e.g. updateList(markerTagList)
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

                        // TODO: call a method to update your list
                        // MarkerTag list save in local variable markerTagList
                        // e.g. updateList(markerTagList)
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

                        // TODO: call a method to update your list
                        // MarkerTag list save in local variable markerTagList
                        // e.g. updateList(markerTagList)
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error: query by location: " + databaseError.getMessage());
                        // TODO how do you want to handle error querying from database?
                    }
                });
    }


}
