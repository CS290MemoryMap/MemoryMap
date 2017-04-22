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
    private static final String NODE_NAME = "markertags";

    private DatabaseReference mDatabase;

    public FirebaseDatabaseHandler() {
        // retrieve instance of database and reference location for read/write
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewMarkerTag(MarkerTag markerTag) {
        // get primary get ID for new object
        String key = mDatabase.child(NODE_NAME).push().getKey();
        // Write a MarkerTag to the database
        mDatabase.child(NODE_NAME).child(key).setValue(markerTag);
        // e.g.
//        mDatabase.child("users").child(userId).child("username").setValue(name);
    }

    public void read() {
        final List<MarkerTag> markerTagList = new ArrayList<>();
        mDatabase.child(NODE_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    MarkerTag tag = noteSnapshot.getValue(MarkerTag.class);
                    markerTagList.add(tag);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB Read", databaseError.getMessage());
            }
        });
        for (int i=0; i<markerTagList.size(); i++) {
            Log.d("FB READ", markerTagList.get(i).getTitle());
        }

        // Read from the database
        // this is called when attached
        // and again every time the data changes (including the children)
        /*mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
    }

}
