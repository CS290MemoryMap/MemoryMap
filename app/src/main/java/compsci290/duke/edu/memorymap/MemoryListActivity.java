package compsci290.duke.edu.memorymap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.database.MarkerTagDbHandler;
import compsci290.duke.edu.memorymap.MarkerTag;
import compsci290.duke.edu.memorymap.MarkerTagAdapter;

/**
 * Created by taranagar on 4/13/17.
 */

public class MemoryListActivity extends AppCompatActivity {

    private static final String TAG = "MemoryListActivity";

    private List<MarkerTag> mMarkerTagList;
    private RecyclerView mRecyclerView;
    private MarkerTagDbHandler mDbHandler;
    private MarkerTagAdapter mAdapter;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_memorylist);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // get MarkerTag set from database, convert to ArrayList
        mDbHandler = new MarkerTagDbHandler();
        Log.d(TAG, "Empty MarkerTagList");
        addToDatabase();
        mMarkerTagList = mDbHandler.queryAllMarkerTags();
        Log.d(TAG, "MarkerTag list queried.");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        if(mMarkerTagList.size() == 0) {
            Log.d(TAG, "MarkerTag list is empty");
        }

        initializeAdapter();
    }

    /*
        Used for testing
     */
    private void addToDatabase() {
        Log.d(TAG, "Adding to database...");

        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.uploadmedia);

        double lat = 123456789;
        double lon = 987654321;
        MarkerTag markerTag = new MarkerTag("tag 1", "04/16/2017", "Details of this tag", img, lat, lon);
        long id = mDbHandler.insertMarkerTag(markerTag);
        //CharSequence text = "Inserted ID " + id;

        lat = 123456789;
        lon = 887654321;
        markerTag = new MarkerTag("tag 2", "04/16/2017", "Details of this tag", img, lat, lon);
        id = mDbHandler.insertMarkerTag(markerTag);
        lat = 987654321;
        lon = 123456789;
        markerTag = new MarkerTag("tag 3", "04/18/2017", "Details of this tag", img, lat, lon);
        id = mDbHandler.insertMarkerTag(markerTag);

    }

    private void initializeAdapter() {
        mAdapter = new MarkerTagAdapter(mMarkerTagList, this);
        mRecyclerView.setAdapter(mAdapter);
        Log.d(TAG, "Adapter initialized");
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
}