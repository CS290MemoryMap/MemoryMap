package compsci290.duke.edu.memorymap.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import compsci290.duke.edu.memorymap.MarkerTag;
import compsci290.duke.edu.memorymap.MyApplication;

/**
 * Handles CRUD operations on SQLite database for MarkerTag
 */

public class MarkerTagDbHandler {
    MarkerTagDbHelper mDbHelper = new MarkerTagDbHelper(MyApplication.getAppContext());

    /**
     * insert a MarkerTag row into the Marker Tag Table
     * @param title
     * @param date
     * @param details
     * @param img
     * @param latitude
     * @param longitude
     */
    public void insertMarkerTag(String title, String date, String details, Bitmap img,
                                double latitude, double longitude) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Converts the bitmap to byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgByteArray = stream.toByteArray();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE, title);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE, date);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS, details);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG, imgByteArray);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE, latitude);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE, longitude);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(MarkerTagContract.MarkerTagTable.TABLE_NAME, null, values);

        db.close();
        mDbHelper.close();
    }

    /**
     * Query for all rows and columns in the Marker Tag Table
     * @return a MarkerTag array with all the queried data
     */
    public Set<MarkerTag> queryAllMarkerTags() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
//                MarkerTagContract.MarkerTagTable._ID,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE,
                MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE
        };

//        // Filter results WHERE "title" = 'My Title'
//        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";
//        String[] selectionArgs = { "My Title" };

//        // How you want the results sorted in the resulting Cursor
//        String sortOrder =MarkerTagContract.MarkerTagTable._ID + " DESC";

        // Query the database for results (returns a cursor)
        Cursor cursor = db.query(
                MarkerTagContract.MarkerTagTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        // Makes MarkerTag objects from data queried and adds it to a Set
        Set<MarkerTag> markerTagSet = new HashSet<>();
        while(cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE));
            String details = cursor.getString(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS));
            byte[] imgByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE));

            // Converts byte[] back to bitmap
            Bitmap img = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);

            // Add MarkerTag to Set
            markerTagSet.add(new MarkerTag(title, date, details, img, latitude, longitude));
        }

        cursor.close();
        db.close();
        mDbHelper.close();

        return markerTagSet;
    }

    public void updateMarkerTag() {

    }

    public void deleteMarkerTag() {

    }
}
