package fr.nicolaspomepuy.discreetapprate;

import android.content.Context;
import android.content.SharedPreferences;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final SharedPreferences settings;
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    // Constructor.
    public ExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Context context) {
        settings = context.getSharedPreferences(AppRate.PREFS_NAME, 0);
        defaultExceptionHandler = uncaughtExceptionHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {

        settings.edit().putLong(AppRate.KEY_LAST_CRASH, System.currentTimeMillis()).commit();

        // Call the original handler.
        defaultExceptionHandler.uncaughtException(thread, throwable);
    }
}