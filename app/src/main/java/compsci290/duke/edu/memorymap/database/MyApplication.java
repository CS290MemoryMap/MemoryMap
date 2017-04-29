package compsci290.duke.edu.memorymap.database;

import android.app.Application;
import android.content.Context;

/**
 * Application class used to access context
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
