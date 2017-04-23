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
 * Handle CRUD transactions on Firebase database
 */

public class FirebaseDatabaseHandler {
    private static final String MARKERTAG_NODE_NAME = "markertags";

    private DatabaseReference mDatabase;
    private List<MarkerTag> mMarkerTagList;

    public FirebaseDatabaseHandler() {
        // retrieve instance of database and reference location for read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // activate MarkerTag listener (updates onDataChange)
        mMarkerTagList = new ArrayList<>();
        readMarkerTagList();
    }

    private void readMarkerTagList() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list
        mDatabase.child(MARKERTAG_NODE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                    markerTagList.add(new MarkerTag((markerTagModel)));
                    Log.d("FB READ", "added " + markerTagModel.getTitle());
                }
                mMarkerTagList = markerTagList;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB Read", databaseError.getMessage());
            }
        });
//        mDatabase.child(MARKERTAG_NODE_NAME).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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

        readMarkerTagList();

        return markerTag;
    }

    public List<MarkerTag> queryAllMarkerTags() {
        return mMarkerTagList;
    }

    public MarkerTag updateMarkerTag(MarkerTag markerTag) {
        // convert MarkerTag to MarkerTagModel
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        // Write a MarkerTag to the database
        mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId()).setValue(markerTagModel);

        readMarkerTagList();

        return markerTag;
    }

    public void deleteMarkerTag(MarkerTag markerTag) {
        MarkerTagModel markerTagModel = new MarkerTagModel(markerTag);
        mDatabase.child(MARKERTAG_NODE_NAME).child(markerTagModel.getId())
                .removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                // update MarkerTagList with SingleEventListener
                readMarkerTagList();
            }
        });
    }

    public void deleteMarkerTagList(ArrayList<MarkerTag> markerTags) {

    }

}
