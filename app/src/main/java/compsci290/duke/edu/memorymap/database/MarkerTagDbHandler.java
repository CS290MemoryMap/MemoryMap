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
     * @param markerTag
     */
    public void insertMarkerTag(MarkerTag markerTag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Converts the bitmap to byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        markerTag.getImg().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgByteArray = stream.toByteArray();

        // Create a new map of values, where column names are the key
        ContentValues values = new ContentValues();
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE, markerTag.getTitle());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE, markerTag.getDate());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS, markerTag.getDetails());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG, imgByteArray);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE, markerTag.getLatitude());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE, markerTag.getLongitude());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(MarkerTagContract.MarkerTagTable.TABLE_NAME, null, values);
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

        return markerTagSet;
    }

    /**
     * update a MarkerTag
     * @param markerTitle
     * @param updatedMarkerTag
     */
    public void updateMarkerTag(String markerTitle, MarkerTag updatedMarkerTag) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Converts the bitmap to byte[]
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        updatedMarkerTag.getImg().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgByteArray = stream.toByteArray();

        // new values for all columns
        ContentValues values = new ContentValues();
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE, updatedMarkerTag.getTitle());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE, updatedMarkerTag.getDate());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS, updatedMarkerTag.getDetails());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG, imgByteArray);
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE, updatedMarkerTag.getLatitude());
        values.put(MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE, updatedMarkerTag.getLongitude());

        // Which row to update, based on the title
        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { markerTitle };

        int count = db.update(
                MarkerTagContract.MarkerTagTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * delete a MarkerTag
     * @param markerTag
     */
    public void deleteMarkerTag(MarkerTag markerTag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { markerTag.getTitle() };

        // Issue SQL statement.
        db.delete(MarkerTagContract.MarkerTagTable.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * delete a list of MarkerTag objects
     * @param markerTags
     */
    public void deleteMarkerTagList(Set<MarkerTag> markerTags) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = new String[markerTags.size()];
        int i = 0; // loop index
        for (MarkerTag tag: markerTags) {
            selectionArgs[i] = tag.getTitle();
            i++;
        }

        // Issue SQL statement.
        db.delete(MarkerTagContract.MarkerTagTable.TABLE_NAME, selection, selectionArgs);
    }
}
