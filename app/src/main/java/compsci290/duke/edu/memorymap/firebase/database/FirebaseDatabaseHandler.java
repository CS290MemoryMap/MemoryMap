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

import static android.content.ContentValues.TAG;

/**
 * Handle CRUD transaction on Firebase database
 */

public class FirebaseDatabaseHandler {
    private static final String MARKERTAG_NODE_NAME = "markertags";

    private DatabaseReference mDatabase;
    private List<MarkerTag> mMarkerTagList;

    public FirebaseDatabaseHandler() {
        // retrieve instance of database and reference location for read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // activate MarkerTag listener (updates onDataChange)
        readMarkerTagList();
    }

    private void readMarkerTagList() {
        mMarkerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                    mMarkerTagList.add(new MarkerTag((markerTagModel)));
                    Log.d("FB READ", "added " + markerTagModel.getTitle());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB Read", databaseError.getMessage());
            }
        });
    }

    public MarkerTag insertMarkerTag(MarkerTag markerTag) {
        // get primary ID for new object
        String key = mDatabase.child(MARKERTAG_NODE_NAME).push().getKey();
        // update MarkerTag with ID
        markerTag.setID(key);
        // convert MarkerTag to MarkerTagModel
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        // Write a MarkerTag to the database
        mDatabase.child(MARKERTAG_NODE_NAME).child(key).setValue(markerTagModel);
        // e.g.
//        mDatabase.child("users").child(userId).child("username").setValue(name);

        return markerTag;
    }

    public List<MarkerTag> queryAllMarkerTags() {
        return mMarkerTagList;
    }

}
