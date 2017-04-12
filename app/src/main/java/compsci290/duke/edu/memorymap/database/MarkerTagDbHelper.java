package compsci290.duke.edu.memorymap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite Helper class (create and upgrade table)
 */

public class MarkerTagDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MarkerTag.db";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + MarkerTagContract.MarkerTagTable.TABLE_NAME + " (" +
                    MarkerTagContract.MarkerTagTable._ID + " INTEGER PRIMARY KEY," +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_TITLE + " TEXT," +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_DATE + " TEXT)" +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_DETAILS + " TEXT)" +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_IMG + " BLOB)" +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_LATITUDE + " FLOAT)" +
                    MarkerTagContract.MarkerTagTable.COLUMN_NAME_LONGITUDE + " FLOAT)";


    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + MarkerTagContract.MarkerTagTable.TABLE_NAME;

    public MarkerTagDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO replace SQL with desired action
        db.execSQL(SQL_DELETE_TABLE);
    }
}
