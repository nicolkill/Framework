package com.nicolkill.framework.modules;

import android.util.Log;

import com.nicolkill.framework.activity.BaseActivity;

/**
 * Created by nicolkill on 7/10/17.
 */

public final class LogErrorsModule {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private static LogErrorsModuleRunner mRunner = new LogErrorsModuleRunner();

    public static void setModule(LogErrorsModuleRunner runner) {
        mRunner = runner;
    }

    public static void run(Throwable e) {
        mRunner.logError(e);
    }

    public static class LogErrorsModuleRunner {
        void logError(Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
