package compsci290.duke.edu.memorymap.firebase.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Saeed on 4/19/2017.
 */

public class FirebaseDatabaseHandler {
    public void write() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // retrieve instance of database
        DatabaseReference myRef = database.getReference("message"); // reference location to write to

        myRef.setValue("Hello, World!");
    }

    public void read() {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // retrieve instance of database
        DatabaseReference myRef = database.getReference("message"); // reference location to write to

        // this is called when attached
        // and again every time the data changes (including the children)
        myRef.addValueEventListener(new ValueEventListener() {
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
        });
    }

}
