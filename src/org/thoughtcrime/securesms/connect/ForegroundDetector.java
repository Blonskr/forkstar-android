package org.thoughtcrime.securesms.connect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.thoughtcrime.securesms.ApplicationContext;

@SuppressLint("NewApi")
public class ForegroundDetector implements Application.ActivityLifecycleCallbacks {

    private int refs = 0;
    private static ForegroundDetector Instance = null;
    private ApplicationContext application;

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public ForegroundDetector(ApplicationContext application) {
        Instance = this;
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public boolean isForeground() {
        return refs > 0;
    }

    public boolean isBackground() {
        return refs == 0;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (refs == 0) {
            Log.i("DeltaChat", "++++++++++++++++++ first ForegroundDetector.onActivityStarted() ++++++++++++++++++");
            DcHelper.getContext(application).maybeStartIo();
            if (DcHelper.isNetworkConnected(application)) {
                new Thread(() -> {
                    Log.i("DeltaChat", "calling maybeNetwork()");
                    DcHelper.getContext(application).maybeNetwork();
                    Log.i("DeltaChat", "maybeNetwork() returned");
                }).start();
            }
        }

        refs++;
    }


    @Override
    public void onActivityStopped(Activity activity) {
        if( refs <= 0 ) {
            Log.w("DeltaChat", "invalid call to ForegroundDetector.onActivityStopped()");
            return;
        }

        refs--;

        if (refs == 0) {
            Log.i("DeltaChat", "++++++++++++++++++ last ForegroundDetector.onActivityStopped() ++++++++++++++++++");
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // pause/resume will also be called when the app is partially covered by a dialog
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
