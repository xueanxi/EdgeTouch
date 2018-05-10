package eagetouch.anxi.com.edgetouch;

import android.app.Application;


/**
 * Created by user on 11/29/17.
 */

public class App extends Application {
    private static final String TAG = "=App";

    private static final Object mSyncObject = new Object();
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public App() {
        super();
        instance = this;
    }


    /**
     * Singleton
     *
     * @return singleton Application instance
     */
    public static App getInstance() {
        synchronized (mSyncObject) {
            if (null == instance) {
                instance = new App();
            }
        }
        return instance;
    }

}
