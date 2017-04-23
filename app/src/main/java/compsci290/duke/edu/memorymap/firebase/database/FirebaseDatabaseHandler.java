package compsci290.duke.edu.memorymap.firebase.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import compsci290.duke.edu.memorymap.MarkerTag;

/**
 * Handle CRUD operation for Firebase database
 */

public class FirebaseDatabaseHandler {
    private static final String MARKERTAG_NODE_NAME = "markertags";
    private static final String TAG = "DB_HANDLER"; // TAG for Logging

    private DatabaseReference mDatabase;
    private List<MarkerTag> mMarkerTagList;
    private List<MarkerTag> mMarkerTagListByLocation;
    private List<MarkerTag> mMarkerTagListByDate;
    private List<MarkerTag> mMarkerTagListByTitle;

    public FirebaseDatabaseHandler() {
        // retrieve instance of database and reference location for read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // initialize/update all MarkerTag lists
        mMarkerTagList = new ArrayList<>();
        mMarkerTagListByTitle = new ArrayList<>();
        mMarkerTagListByDate = new ArrayList<>();
        mMarkerTagListByLocation = new ArrayList<>();
        updateMarkerTagLists();
    }

    /**
     * insert a new MarkerTag into the database
     * @param markerTag a MarkerTag object to insert
     * @return the MarkerTag object with updated ID
     */
    public MarkerTag insertMarkerTag(MarkerTag markerTag) {
        // get primary ID for new object
        String key = mDatabase.child(MARKERTAG_NODE_NAME).push().getKey();
        // update MarkerTag with ID
        markerTag.setID(key);
        // convert MarkerTag to MarkerTagModel
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        // Write a MarkerTag to the database
        mDatabase.child(MARKERTAG_NODE_NAME).child(key).setValue(markerTagModel);

        // update MarkerTagList with SingleEventListener
        updateMarkerTagLists();

        return markerTag;
    }

    /**
     * return a list of all MarkerTag objects
     * @return MarkerTag list
     */
    public List<MarkerTag> queryAllMarkerTags() {
        return mMarkerTagList;
    }

    /**
     * return a list of all MarkerTag objects sorted ascending by title
     * @return MarkerTag list
     */
    public List<MarkerTag> querySortByTitle() {
        return mMarkerTagListByTitle;
    }

    /**
     * return a list of all MarkerTag objects sorted ascending by date
     * @return MarkerTag list
     */
    public List<MarkerTag> querySortByDate() {
        return mMarkerTagListByDate;
    }

    /**
     * return a list of all MarkerTag objects sorted ascending by location
     * @return MarkerTag list
     */
    public List<MarkerTag> querySortByLocation() {
        return mMarkerTagListByLocation;
    }

    /**
     * updates an existing MarkerTag in the database (object ID must exist in database,
     * otherwise will add a new MarkerTag to database)
     * @param markerTag a MarkerTag object with the updated data
     * @return the MarkerTag object updated (or added)
     */
    public MarkerTag updateMarkerTag(MarkerTag markerTag) {
        // convert MarkerTag to MarkerTagModel
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        // Write a MarkerTag to the database
        mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId()).setValue(markerTagModel);

        // update MarkerTagList with SingleEventListener
        updateMarkerTagLists();

        return markerTag;
    }

    /**
     * delete a MarkerTag object from the database (does nothing if object ID does not exist)
     * @param markerTag the MarkerTag object to delete
     */
    public void deleteMarkerTag(MarkerTag markerTag) {
        // delete MarkerTag
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId()).setValue(null);

        // update MarkerTagList with SingleEventListener
        updateMarkerTagLists();

//        mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId())
//                .removeValue(new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                // update MarkerTagList with SingleEventListener
//                updateMarkerTagLists();
//            }
//        });
    }

    /**
     * delete a list of MarkerTag objects (does nothing if object ID does not exist)
     * @param markerTags a list of all MarkerTag objects to delete
     */
    public void deleteMarkerTagList(List<MarkerTag> markerTags) {
        for (int i=0; i<markerTags.size(); i++) {
            // delete MarkerTag
            MarkerTagModel markerTagModel = new MarkerTagModel(markerTags.get(i));
            mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId()).setValue(null);
        }

        // update MarkerTagList with SingleEventListener
        updateMarkerTagLists();
    }

    /**
     * update all MarkerTag lists (global variables)
     */
    private void updateMarkerTagLists() {
        readMarkerTagListUnsorted();
        readMarkerTagListSortByTitle();
        readMarkerTagListSortByDate();
        readMarkerTagListSortByLocation();

    }

    /**
     * read and update the MarkerTag list sorted by title
     */
    private void readMarkerTagListSortByTitle() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).orderByChild("title")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "TITLE SORT onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            markerTagList.add(new MarkerTag((markerTagModel)));
                            Log.d(TAG, "TITLE SORT " + markerTagModel.getTitle());
                        }
                        mMarkerTagListByTitle = markerTagList;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "TITLE SORT " + databaseError.getMessage());
                    }
                });
    }

    /**
     * read and update the MarkerTag list sorted by date
     */
    private void readMarkerTagListSortByDate() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).orderByChild("date")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "DATE SORT onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            markerTagList.add(new MarkerTag((markerTagModel)));
                            Log.d(TAG, "Date SORT " + markerTagModel.getTitle());
                        }
                        mMarkerTagListByDate = markerTagList;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "DATE SORT " + databaseError.getMessage());
                    }
                });
    }

    /**
     * read and update the MarkerTag list sorted by location
     */
    private void readMarkerTagListSortByLocation() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).orderByChild("latitude")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "LOCATION SORT onDataChange");
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            markerTagList.add(new MarkerTag((markerTagModel)));
                            Log.d(TAG, "LOCATION SORT " + markerTagModel.getTitle());
                        }
                        mMarkerTagListByLocation = markerTagList;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "LOCATION SORT " + databaseError.getMessage());
                    }
                });
    }

    /**
     * read and update the MarkerTag list unsorted
     */
    private void readMarkerTagListUnsorted() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                    markerTagList.add(new MarkerTag((markerTagModel)));
                    Log.d(TAG, "QUERY ALL " + markerTagModel.getTitle());
                }
                mMarkerTagList = markerTagList;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "QUERY ALL " + databaseError.getMessage());
            }
        });
    }

}
