package compsci290.duke.edu.memorymap;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compsci290.duke.edu.memorymap.database.MarkerTagDbHandler;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;

/**
 * Created by taranagar on 4/13/17.
 */




public class MemoryListActivity extends AppCompatActivity implements MarkerTagAdapter.OnItemClickListener {


    private static final String MARKERTAG = "markertag";
    private static final int OPEN_MEMORY = 1;

    private List<MarkerTag> mMarkerTagList;
    private RecyclerView mRecyclerView;
    private FirebaseDatabaseHandler mDbHandler;
    private MarkerTagAdapter mAdapter;


    /*
     * Retrieve the list of MarkerTags and display in RecyclerView
     */

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_memorylist);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // get MarkerTag set from database, convert to ArrayList
        mDbHandler = new FirebaseDatabaseHandler();
        // addToDatabase();
        mMarkerTagList = Collections.emptyList();
        //mMarkerTagList = mDbHandler.queryAllMarkerTags();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        initializeAdapter();
    }


    /*
     * Used for testing; adds 3 marker tags to the database
     */

    private void addToDatabase() {
        Log.d("MemoryList", "Adding tags to Firebase Database...");

        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.uploadmedia);

        double lat = 123456789;
        double lon = 987654321;
        MarkerTag markerTag = new MarkerTag("tag 1", "Apr 16, 2017", "Details of this tag", img, lat, lon);
        mDbHandler.insertMarkerTag(markerTag);

        //CharSequence text = "Inserted ID " + id;

        lat = 123456789;
        lon = 887654321;
        markerTag = new MarkerTag("tag 2", "Apr 16, 2017", "Details of this tag", img, lat, lon);
        mDbHandler.insertMarkerTag(markerTag);

        lat = 987654321;
        lon = 123456789;
        markerTag = new MarkerTag("tag 3", "Apr 18, 2017", "Details of this tag", img, lat, lon);
        mDbHandler.insertMarkerTag(markerTag);

    }


    /**
     * Initialize the custom MarkerTagAdapter for the RecyclerView
     **/

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
        // Sort by Location
        if(menuString.equals(getResources().getString(R.string.location_sort))) {
            mMarkerTagList = mDbHandler.querySortByLocation();
            Toast.makeText(this, "Sort by location", Toast.LENGTH_SHORT).show();
        }
        // Sort by Time Added
        else if (menuString.equals(getResources().getString(R.string.time_sort))) {
            mMarkerTagList = mDbHandler.queryAllMarkerTags();
        }
        // Sort by Date
        else if (menuString.equals(getResources().getString(R.string.date_sort))) {
            mMarkerTagList = mDbHandler.querySortByDate();
        }
        // Sort by Title
        else if (menuString.equals(getResources().getString(R.string.title_sort))) {
            mMarkerTagList = mDbHandler.querySortByTitle();
        }
        mAdapter.swap(mMarkerTagList);
        return super.onOptionsItemSelected(item);
    }


    /*
    TODO: implement onClick listeners to open editable memory when it is clicked in the list
     */


    @Override
    public void onItemClick(View view, int position) {
        // Retrieve MarkerTag to open in MemoryActivity
        MarkerTag mMarkerTag = mMarkerTagList.get(position);
        Intent intent = listToMemoryIntent(mMarkerTag, MemoryActivity.class);
        startActivityForResult(intent, OPEN_MEMORY);
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
