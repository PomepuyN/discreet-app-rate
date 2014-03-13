package fr.nicolaspomepuy.discreetapprate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by nicolas on 06/03/14.
 */
public class AppRate {

    protected static final String PREFS_NAME = "app_rate_prefs";
    private static final String KEY_ELAPSED_TIME = "elapsed_time";
    private static final String KEY_COUNT = "count";
    private static final String KEY_CLICKED = "clicked";
    protected static final String KEY_LAST_CRASH = "last_crash";
    private static final String KEY_MONITOR_START = "monitor_start";
    private static final String KEY_MONITOR_TOTAL = "monitor_total";
    private Activity activity;
    private String text;
    private int initialLaunchCount = 5;
    private RetryPolicy policy = RetryPolicy.EXPONENTIAL;
    private OnShowListener onShowListener;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private int delay = 0;
    private long installedSince;
    private boolean debug;
    private AppRateTheme theme = AppRateTheme.DARK;
    private long pauseAfterCrash;
    private boolean fromTop = false;
    private long minimumMonitoringTime;

    private AppRate(Activity activity) {
        this.activity = activity;
    }

    public static AppRate with(Activity activity) {

        if (activity == null) {
            throw new IllegalStateException("Activity cannot be null");
        }

        AppRate instance = new AppRate(activity);
        instance.text = activity.getString(R.string.dra_rate_app);
        instance.settings = activity.getSharedPreferences(PREFS_NAME, 0);
        instance.editor = instance.settings.edit();
        return instance;
    }

    /*
     *
     * ******************** SETTINGS ********************
     *
     */


    /**
     * Enable debug mode which will send state when actions are triggered
     *
     * @param debug has to be debuggable
     * @return the {@link AppRate} instance
     */
    public AppRate debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * Text to be displayed in the view
     *
     * @param text text to be displayed
     * @return the {@link AppRate} instance
     */
    public AppRate text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Text to be displayed in the view
     *
     * @param textRes text ressource to be displayed
     * @return the {@link AppRate} instance
     */
    public AppRate text(int textRes) {
        this.text = activity.getString(textRes);
        return this;
    }

    /**
     * Initial times {@link AppRate} has to be called before the view is shown
     *
     * @param initialLaunchCount times count
     * @return the {@link AppRate} instance
     */
    public AppRate initialLaunchCount(int initialLaunchCount) {
        this.initialLaunchCount = initialLaunchCount;
        return this;
    }

    /**
     * Policy to use to show the {@link AppRate} again
     *
     * @param policy the {@link RetryPolicy} to be used
     * @return the {@link AppRate} instance
     */
    public AppRate retryPolicy(RetryPolicy policy) {
        this.policy = policy;
        return this;
    }

    /**
     * Listener used to get {@link AppRate} lifecycle
     *
     * @param onShowListener the listener
     * @return the {@link AppRate} instance
     */
    public AppRate listener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
        return this;
    }

    /**
     * Add a constraint to show the view only if the app is installed for more than
     *
     * @param installedSince the time in seconds
     * @return the {@link AppRate} instance
     */
    public AppRate atLeastInstalledSince(long installedSince) {
        this.installedSince = installedSince;
        return this;
    }

    /**
     * Delay the {@link AppRate} showing time
     *
     * @param delay the delay in ms
     * @return the {@link AppRate} instance
     */
    public AppRate delay(int delay) {
        this.delay = delay;
        return this;
    }

    /**
     * Set the theme (LIGHT or DARK)
     *
     * @param theme the {@link fr.nicolaspomepuy.discreetapprate.AppRateTheme} to be used
     * @return the {@link AppRate} instance
     */
    public AppRate theme(AppRateTheme theme) {
        this.theme = theme;
        return this;
    }


    /**
     * Pause duration after a crash (in sec.)
     * /!\ Calling {@link #initExceptionHandler(android.content.Context)} is mandatory to make it work.
     * You should do it in your {@link android.app.Application} class
     *
     * @param pauseAfterCrash the time to pause
     * @return the {@link AppRate} instance
     */
    public AppRate pauseTimeAfterCrash(long pauseAfterCrash) {
        this.pauseAfterCrash = pauseAfterCrash;
        return this;
    }

    /**
     * Set the minimum monitoring time needed before showing the view
     *
     * @param minimumMonitoringTime the minimum time in seconds
     * @return the {@link AppRate} instance
     */
    public AppRate minimumMonitoringTime(long minimumMonitoringTime) {
        this.minimumMonitoringTime = minimumMonitoringTime;
        return this;
    }

    /*
     *
     * ******************** ACTIONS ********************
     *
     */


    /**
     * Check and show if showing the view is needed
     */
    public void checkAndShow() {

        if (!Utils.isGooglePlayInstalled(activity)) {
            if (debug) LogD("Play Store is not installed. Won't do anything");
            return;
        }

        if (debug) LogD("Last crash: " + ((System.currentTimeMillis() - settings.getLong(KEY_LAST_CRASH, 0L)) / 1000) + " seconds ago");
        if ((System.currentTimeMillis() - settings.getLong(KEY_LAST_CRASH, 0L)) / 1000 < pauseAfterCrash) {
            if (debug) LogD("A recent crash avoids anything to be done.");
            return;
        }

        if (settings.getLong(KEY_MONITOR_TOTAL, 0L) / 1000 < minimumMonitoringTime) {
            if (debug)
                LogD("Monitor time not reached. Nothing will be done");
            return;
        }

        if (!Utils.isOnline(activity)) {
            if (debug)
                LogD("Device is not online. AppRate try to show up next time.");
            return;
        }

        incrementViews();


        Date installDate = Utils.installTimeFromPackageManager(activity.getPackageManager(), activity.getPackageName());
        Date now = new Date();
        if (now.getTime() - installDate.getTime() < installedSince * 1000) {
            if (debug)
                LogD("Date not reached. Time elapsed since installation (in sec.): " + ((now.getTime() - installDate.getTime()) / 1000));
            return;
        }
        if (!settings.getBoolean(KEY_ELAPSED_TIME, false)) {
            // It's the first time the time is elapsed
            editor.putBoolean(KEY_ELAPSED_TIME, true);
            if (debug) LogD("First time after the time is elapsed");
            if (settings.getInt(KEY_COUNT, 5) > initialLaunchCount) {
                if (debug) LogD("Initial count passed. Resetting to initialLaunchCount");
                // Initial count passed. Resetting to initialLaunchCount
                editor.putInt(KEY_COUNT, initialLaunchCount);

            }

            editor.commit();

        }

        boolean clicked = settings.getBoolean(KEY_CLICKED, false);
        if (clicked) return;
        int count = settings.getInt(KEY_COUNT, 0);
        if (count == initialLaunchCount) {
            if (debug) LogD("initialLaunchCount reached");
            showAppRate();
        } else if (policy == RetryPolicy.INCREMENTAL && count % initialLaunchCount == 0) {
            if (debug) LogD("initialLaunchCount incremental reached");
            showAppRate();
        } else if (policy == RetryPolicy.EXPONENTIAL && count % initialLaunchCount == 0 && Utils.isPowerOfTwo(count / initialLaunchCount)) {
            if (debug) LogD("initialLaunchCount exponential reached");
            showAppRate();
        } else {
            if (debug)
                LogD("Nothing to show. initialLaunchCount: " + initialLaunchCount + " - Current count: " + count);
        }
    }

    /**
     * Reset the count to start over
     */
    public void reset() {
        if (debug) LogD("Count reset");
        editor.putInt(KEY_COUNT, 0);
        editor.putBoolean(KEY_CLICKED, false);
        editor.putLong(KEY_LAST_CRASH, 0L);
        editor.commit();
    }

    /**
     * Will force the {@link AppRate} to show
     */
    public void forceShow() {
        if (!Utils.isGooglePlayInstalled(activity)) {
            if (debug) LogD("Play Store is not installed. Won't do anything");
        }
        showAppRate();
    }

    /**
     * Avoid showing the view again. Can only be undone by {@link #reset()}.
     */
    public void neverShowAgain() {
        editor.putBoolean(KEY_CLICKED, true);
        editor.commit();
    }

    /**
     * Start monitoring
     */
    public void startMonitoring() {
        if (debug) LogD("Start monitoring");
        long start = settings.getLong(KEY_MONITOR_START, 0);
        if (start != 0) {
            if (debug) LogD("Monitor error. Start monitoring called before end. Adding the result");
            endMonitoring();
        }
        editor.putLong(KEY_MONITOR_START, System.currentTimeMillis());
        editor.commit();
    }

    /**
     * End montoring
     */
    public void endMonitoring() {
        if (debug) LogD("End monitoring");
        editor.commit();
        long start = settings.getLong(KEY_MONITOR_START, 0);
        if (start == 0) {
            if (debug) LogD("Monitor error. End monitoring called before start.");
            return;
        }
        editor.putLong(KEY_MONITOR_TOTAL, settings.getLong(KEY_MONITOR_TOTAL, 0) + (System.currentTimeMillis() - start));
        editor.putLong(KEY_MONITOR_START, 0);
        editor.commit();
    }

    /**
     * Initialize the {@link ExceptionHandler}.
     */
    public static void initExceptionHandler(Context context) {

        Log.d("AppRate", "Init AppRate ExceptionHandler");

        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Don't register again if already registered.
        if (!(currentHandler instanceof ExceptionHandler)) {

            // Register default exceptions handler.
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, context));
        }
    }

    /*
     *
     * ******************** PRIVATE ********************
     *
     */

    private void incrementViews() {

        editor.putInt(KEY_COUNT, settings.getInt(KEY_COUNT, 0) + 1);
        editor.commit();
    }


    public AppRate fromTop(boolean fromTop) {
        this.fromTop = fromTop;
        return this;
    }


    private void showAppRate() {
        final ViewGroup mainView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.app_rate, null);


        ImageView close = (ImageView) mainView.findViewById(R.id.close);
        TextView textView = (TextView) mainView.findViewById(R.id.text);
        RelativeLayout container = (RelativeLayout) mainView.findViewById(R.id.container);
        if (fromTop) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
            lp.gravity = Gravity.TOP;
            container.setLayoutParams(lp);
        } else {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
            lp.gravity = Gravity.BOTTOM;
            container.setLayoutParams(lp);
        }

        textView.setText(text);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllViews(mainView);
                if (onShowListener != null) onShowListener.onRateAppDismissed();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
                hideAllViews(mainView);
                editor.putBoolean(KEY_CLICKED, true);
                editor.commit();
                if (onShowListener != null) onShowListener.onRateAppClicked();

            }
        });

        if (theme == AppRateTheme.LIGHT) {
            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
            Drawable d = activity.getResources().getDrawable(R.drawable.ic_action_remove);
            d.setColorFilter(Color.BLACK, mMode);
            close.setImageDrawable(d);

            textView.setTextColor(Color.BLACK);

            container.setBackgroundColor(0X88ffffff);

        } else {
            Drawable d = activity.getResources().getDrawable(R.drawable.ic_action_remove);
            d.clearColorFilter();
            close.setImageDrawable(d);

            container.setBackgroundColor(0Xaa000000);

        }


        if (delay > 0) {
            activity.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayViews(mainView);
                }
            }, delay);
        } else {
            displayViews(mainView);
        }

    }

    private void hideAllViews(final ViewGroup mainView) {
        Animation hideAnimation;
        if (fromTop) {
            hideAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out_from_top);
        } else {
            hideAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        }

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainView.removeAllViews();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainView.startAnimation(hideAnimation);
    }

    private void displayViews(ViewGroup mainView) {
        activity.addContentView(mainView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        Animation fadeInAnimation;
        if (fromTop) {
            fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in_from_top);
        } else {
            fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        }
        mainView.startAnimation(fadeInAnimation);

        if (onShowListener != null) onShowListener.onRateAppShowing();
    }

    public interface OnShowListener {
        void onRateAppShowing();

        void onRateAppDismissed();

        void onRateAppClicked();
    }

    private void LogD(String s) {
        Log.d("DicreetAppRate", s);
    }
}
