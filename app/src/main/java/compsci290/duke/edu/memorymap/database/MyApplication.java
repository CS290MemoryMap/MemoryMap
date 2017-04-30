package compsci290.duke.edu.memorymap.database;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Application class used to access context
 */

public class MyApplication extends Application {
    public static final String MY_USER_ID = "UserID";
    public static final String MY_PREFS_NAME = "MyUserPrefsFile";
    public static final String GUEST_ID = "guest";

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public String getUserId() {
        SharedPreferences prefs = MyApplication.context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(MY_USER_ID, GUEST_ID);
    }
}