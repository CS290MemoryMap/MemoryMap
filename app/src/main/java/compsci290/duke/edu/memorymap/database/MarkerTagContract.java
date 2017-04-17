package compsci290.duke.edu.memorymap.database;

import android.provider.BaseColumns;

/**
 * Define a Schema and Contract for the SQLite Database
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * This lets you change a column name in one place and have it propagate throughout your code.
 */

final class MarkerTagContract {
    /**
     * Constructor
     * private to prevent someone from accidentally instantiating the contract class
     */
    private MarkerTagContract() {}

    /**
     * MarkerTagEntry: Inner class that defines the table contents
     */
    static class MarkerTagTable implements BaseColumns {
        static final String TABLE_NAME = "marker_tag";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_DETAILS = "details";
        static final String COLUMN_NAME_IMG = "img";
        static final String COLUMN_NAME_LATITUDE = "latitude";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
}
