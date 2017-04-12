package compsci290.duke.edu.memorymap.database;

import android.provider.BaseColumns;

/**
 * Define a Schema and Contract for the SQLite Database
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * This lets you change a column name in one place and have it propagate throughout your code.
 */

public final class MarkerTagContract {
    /**
     * Constructor
     * private to prevent someone from accidentally instantiating the contract class
     */
    private MarkerTagContract() {}

    /**
     * MarkerTagEntry: Inner class that defines the table contents
     */
    public static class MarkerTagTable implements BaseColumns {
        public static final String TABLE_NAME = "marker_tag";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DETAILS = "details";
        public static final String COLUMN_NAME_IMG = "img";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
}
