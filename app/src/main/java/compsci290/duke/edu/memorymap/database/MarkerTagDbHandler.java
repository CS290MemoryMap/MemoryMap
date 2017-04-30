package compsci290.duke.edu.memorymap.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import compsci290.duke.edu.memorymap.memory.MarkerTag;

/**
 * Handles CRUD operations on SQLite database for MarkerTag
 */

public class MarkerTagDbHandler {
    private MarkerTagDbHelper mDbHelper = new MarkerTagDbHelper(new MyApplication().getAppContext());

    /**
     * insert a MarkerTag row into the Marker Tag Table
     * @param markerTag MarkerTag object containing memory data
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertMarkerTag(MarkerTag markerTag) {
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
        return db.insert(MarkerTagContract.MarkerTagTable.TABLE_NAME, null, values);
    }

    /**
     * Query for all rows and columns in the Marker Tag Table
     * @return a MarkerTag list of all the queried data
     */
    public ArrayList<MarkerTag> queryAllMarkerTags() {
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
//        Set<MarkerTag> markerTagList = new HashSet<>();
        ArrayList<MarkerTag> markerTagList = new ArrayList<>();

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
            //Bitmap img = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
            Bitmap img = Bitmap.createScaledBitmap(BitmapFactory.
                    decodeByteArray(imgByteArray, 0, imgByteArray.length), 100, 100, true);

            // Add MarkerTag to List
            //TODO: uncomment markerTagList.add(new MarkerTag(title, date, details, img, latitude, longitude));
        }

        cursor.close();

        return markerTagList;
    }

    /**
     * query all MarkerTag objects sorted by longitude and latitude (northwest to southeast)
     * @return List of sorted MarkerTag objects
     */
    public ArrayList<MarkerTag> querySortByLongLat() {
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

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE + " ASC" +
                ", " + MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE + " DESC";

        // Query the database for results (returns a cursor)
        Cursor cursor = db.query(
                MarkerTagContract.MarkerTagTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                      // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                     // The sort order
        );

        // Makes MarkerTag objects from data queried and adds it to a Set
//        Set<MarkerTag> markerTagList = new HashSet<>();
        ArrayList<MarkerTag> markerTagList = new ArrayList<>();

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
            //Bitmap img = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
            Bitmap img = Bitmap.createScaledBitmap(BitmapFactory.
                    decodeByteArray(imgByteArray, 0, imgByteArray.length), 100, 100, true);

            // Add MarkerTag to List
            //TODO: uncomment markerTagList.add(new MarkerTag(title, date, details, img, latitude, longitude));
        }

        cursor.close();

        return markerTagList;
    }

    /**
     * update a MarkerTag
     * @param markerTitle original title of the MarkerTag
     * @param updatedMarkerTag MarkerTag object after the update
     * @return the number of rows affected
     */
    public int updateMarkerTag(String markerTitle, MarkerTag updatedMarkerTag) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

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

        return db.update(
                MarkerTagContract.MarkerTagTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * delete a MarkerTag
     * @param markerTag MarkerTag object to delete
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise
     */
    public int deleteMarkerTag(MarkerTag markerTag) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { markerTag.getTitle() };

        // Issue SQL statement.
        return db.delete(MarkerTagContract.MarkerTagTable.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * delete a list of MarkerTag objects
     * @param markerTags List of MarkerTag objects to delete
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise
     */
    public int deleteMarkerTagList(ArrayList<MarkerTag> markerTags) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;

        // Define 'where' part of query.
        String selection = MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " = ?";

        for (int i=0; i<markerTags.size(); i++) {
            // Specify arguments in placeholder order.
            String[] selectionArgs = { markerTags.get(i).getTitle() };

            // Issue SQL statement.
            count += db.delete(MarkerTagContract.MarkerTagTable.TABLE_NAME, selection, selectionArgs);
        }

        return count;
    }

    /**
     * Close the database (call in the calling activity onDestroy())
     */
    public void closeDatabase(){
        mDbHelper.close();
    }
}
