package compsci290.duke.edu.memorymap.firebase.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import compsci290.duke.edu.memorymap.database.MyApplication;
import compsci290.duke.edu.memorymap.memory.MarkerTag;

/**
 * Handle CRUD operation for Firebase database
 */

public class FirebaseDatabaseHandler {
    private static final String TAG = "FDbHandler"; // TAG for Logging

    private DatabaseReference mDatabase; // database reference
    private String mUserId; // authenticated firebase user ID

    /**
     * Default Constructor
     */
    public FirebaseDatabaseHandler() {
        // retrieve instance of database and reference location for read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // retrieve current authenticated user
        mUserId = new MyApplication().getUserId();

        Log.d(TAG, "instantiated handler for user " + mUserId);
    }

    /**
     * insert a new MarkerTag into the database
     * @param markerTag a MarkerTag object to insert
     * @return the MarkerTag object with updated ID
     */
    public MarkerTag insertMarkerTag(MarkerTag markerTag) {
        // get primary ID for new object
        String key = mDatabase.child(MarkerTagModel.TABLE_NAME_MARKERTAG).push().getKey();
        // update MarkerTag with ID
        markerTag.setID(key);
        // convert MarkerTag to MarkerTagModel
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        // Write a MarkerTag to the database
        mDatabase.child(MarkerTagModel.TABLE_NAME_MARKERTAG).child(key).setValue(markerTagModel);

        Log.d(TAG, "inserted Marker Tag " + markerTagModel.getMarkerTagId() + " with title "
                + markerTagModel.getTitle());

        return markerTag;
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
        mDatabase.child(MarkerTagModel.TABLE_NAME_MARKERTAG).child(markerTagModel.getMarkerTagId()).setValue(markerTagModel);

        Log.d(TAG, "updated Marker Tag " + markerTagModel.getMarkerTagId() + " with title "
                + markerTagModel.getTitle());

        return markerTag;
    }

    /**
     * delete a MarkerTag object from the database (does nothing if object ID does not exist)
     * @param markerTag the MarkerTag object to delete
     */
    public void deleteMarkerTag(MarkerTag markerTag) {
        // delete MarkerTag
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        mDatabase.child(MarkerTagModel.TABLE_NAME_MARKERTAG).child(markerTagModel.getMarkerTagId()).setValue(null);

        Log.d(TAG, "deleted Marker Tag " + markerTagModel.getMarkerTagId() + " with title "
                + markerTagModel.getTitle());

    }

    /**
     * delete a list of MarkerTag objects (does nothing if object ID does not exist)
     * @param markerTags a list of all MarkerTag objects to delete
     */
    public void deleteMarkerTagList(List<MarkerTag> markerTags) {
        for (int i=0; i<markerTags.size(); i++) {
            // delete MarkerTag
            MarkerTagModel markerTagModel = new MarkerTagModel(markerTags.get(i));
            mDatabase.child(MarkerTagModel.TABLE_NAME_MARKERTAG).child(markerTagModel.getMarkerTagId()).setValue(null);

            Log.d(TAG, i + ": deleted Marker Tag " + markerTagModel.getMarkerTagId() + " with title "
                    + markerTagModel.getTitle());
        }
    }

    /* Getters */
    public DatabaseReference getDatabase() {
        return mDatabase;
    }
    public String getUserId() {
        return mUserId;
    }
}