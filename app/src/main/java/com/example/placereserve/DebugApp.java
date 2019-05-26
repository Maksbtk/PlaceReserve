package com.example.placereserve;

import android.app.Application;

public class DebugApp extends Application {

    /**
     * public static RefWatcher getRefWatcher(Context context) {
     * DebugApp application = (DebugApp) context.getApplicationContext();
     * return application.refWatcher;
     * }
     * <p>
     * private RefWatcher refWatcher;
     **/

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         if (LeakCanary.isInAnalyzerProcess(this)) {
         // This process is dedicated to LeakCanary for heap analysis.
         // You should not init your app in this process.
         return;
         }
         refWatcher = LeakCanary.install(this);
         **/
        // Normal app init code...
    }
}
