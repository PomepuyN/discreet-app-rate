package fr.nicolaspomepuy.discreetapprate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InflateException;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
    private static final String KEY_LAST_COUNT_UPDATE = "last_count_update";
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
    private long minimumInterval;
    private int view;
    private ViewGroup mainView;

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
     * /!\ This is only available for 2.2+ devices
     *
     * @param installedSince the time in milliseconds
     * @return the {@link AppRate} instance
     */
    public AppRate installedSince(long installedSince) {
        this.installedSince = installedSince;
        return this;
    }

    /**
     * @param installedSince the time in seconds
     * @return the {@link AppRate} instance
     * @deprecated : Use {@link #installedSince(long)} instead. Be careful to use milliseconds.
     * Add a constraint to show the view only if the app is installed for more than
     * /!\ This is only available for 2.2+ devices
     */
    public AppRate atLeastInstalledSince(long installedSince) {
        this.installedSince = installedSince * 1000;
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
     * Pause duration after a crash (in ms.)
     * /!\ Calling {@link #initExceptionHandler(android.content.Context)} is mandatory to make it work.
     * You should do it in your {@link android.app.Application} class
     *
     * @param pauseAfterCrash the time to pause
     * @return the {@link AppRate} instance
     */
    public AppRate pauseAfterCrash(long pauseAfterCrash) {
        this.pauseAfterCrash = pauseAfterCrash;
        return this;
    }

    /**
     * @param pauseAfterCrash the time to pause
     * @return the {@link AppRate} instance
     * @deprecated : Use {@link #pauseAfterCrash(long)} instead. Be careful to use milliseconds.
     * Pause duration after a crash (in sec.)
     * /!\ Calling {@link #initExceptionHandler(android.content.Context)} is mandatory to make it work.
     * You should do it in your {@link android.app.Application} class
     */
    public AppRate pauseTimeAfterCrash(long pauseAfterCrash) {
        this.pauseAfterCrash = pauseAfterCrash * 1000;
        return this;
    }

    /**
     * @param minimumMonitoringTime the minimum time in seconds
     * @return the {@link AppRate} instance
     * @deprecated : Use {@link #minMonitoringTime(long)} )} instead. Be careful to use milliseconds.
     * Set the minimum monitoring time needed before showing the view
     */
    public AppRate minimumMonitoringTime(long minimumMonitoringTime) {
        this.minimumMonitoringTime = minimumMonitoringTime * 1000;
        return this;
    }

    /**
     * Set the minimum monitoring time needed before showing the view
     *
     * @param minimumMonitoringTime the minimum time in milliseconds
     * @return the {@link AppRate} instance
     */
    public AppRate minMonitoringTime(long minimumMonitoringTime) {
        this.minimumMonitoringTime = minimumMonitoringTime;
        return this;
    }

    /**
     * @param minimumInterval the minimum interval in seconds
     * @return the {@link AppRate} instance
     * @deprecated : Use {@link #minInterval(long)} instead. Be careful to use milliseconds.
     * Set the minimum interval to increment the count
     */
    @Deprecated
    public AppRate minimumInterval(long minimumInterval) {
        this.minimumInterval = minimumInterval * 1000;
        return this;
    }

    /**
     * Set the minimum interval to increment the count
     *
     * @param minimumInterval the minimum interval in milliseconds
     * @return the {@link AppRate} instance
     */
    public AppRate minInterval(long minimumInterval) {
        this.minimumInterval = minimumInterval;
        return this;
    }

    /**
     * Set the view to display
     *
     * @param view the view to display
     * @return the {@link AppRate} instance
     */
    public AppRate view(int view) {
        this.view = view;
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

//        if (!Utils.isGooglePlayInstalled(activity)) {
//            if (debug) LogD("Play Store is not installed. Won't do anything");
//            return;
//        }

        if (debug)
            LogD("Last crash: " + ((System.currentTimeMillis() - settings.getLong(KEY_LAST_CRASH, 0L)) / 1000) + " seconds ago");
        if ((System.currentTimeMillis() - settings.getLong(KEY_LAST_CRASH, 0L)) < pauseAfterCrash) {
            if (debug) LogD("A recent crash avoids anything to be done.");
            return;
        }

        if (settings.getLong(KEY_MONITOR_TOTAL, 0L) < minimumMonitoringTime) {
            if (debug)
                LogD("Monitor time not reached. Nothing will be done");
            return;
        }

        if (!Utils.isOnline(activity)) {
            if (debug)
                LogD("Device is not online. AppRate try to show up next time.");
            return;
        }

        if (!incrementViews()) {
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Date installDate = Utils.installTimeFromPackageManager(activity.getPackageManager(), activity.getPackageName());
            Date now = new Date();
            if (now.getTime() - installDate.getTime() < installedSince) {
                if (debug)
                    LogD("Date not reached. Time elapsed since installation (in sec.): " + (now.getTime() - installDate.getTime()));
                return;
            }
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

            commitEditor();

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
        commitEditor();
    }

    /**
     * Will force the {@link AppRate} to show
     */
    public void forceShow() {
        if (!Utils.isGooglePlayInstalled(activity)) {
            if (debug) LogD("Play Store is not installed. Won't do anything");
        }
        if (debug) LogD("Force Show");
        showAppRate();
    }

    /**
     * Avoid showing the view again. Can only be undone by {@link #reset()}.
     */
    public void neverShowAgain() {
        editor.putBoolean(KEY_CLICKED, true);
        commitEditor();
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
        commitEditor();
    }

    /**
     * End montoring
     */
    public void endMonitoring() {
        if (debug) LogD("End monitoring");
        commitEditor();
        long start = settings.getLong(KEY_MONITOR_START, 0);
        if (start == 0) {
            if (debug) LogD("Monitor error. End monitoring called before start.");
            return;
        }
        editor.putLong(KEY_MONITOR_TOTAL, settings.getLong(KEY_MONITOR_TOTAL, 0) + (System.currentTimeMillis() - start));
        editor.putLong(KEY_MONITOR_START, 0);
        commitEditor();
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

    public void hide() {
        hideAllViews(mainView);
    }

    /*
     *
     * ******************** PRIVATE ********************
     *
     */

    private boolean incrementViews() {

        if (System.currentTimeMillis() - settings.getLong(KEY_LAST_COUNT_UPDATE, 0L) < minimumInterval) {
            if (debug) LogD("Count not incremented due to minimum interval not reached");
            return false;
        }

        editor.putInt(KEY_COUNT, settings.getInt(KEY_COUNT, 0) + 1);
        editor.putLong(KEY_LAST_COUNT_UPDATE, System.currentTimeMillis());
        commitEditor();
        return true;
    }


    public AppRate fromTop(boolean fromTop) {
        this.fromTop = fromTop;
        return this;
    }


    @SuppressLint("NewApi")
    private void showAppRate() {
        if (view != 0) {
            mainView = new FrameLayout(activity);
            try {
                activity.getLayoutInflater().inflate(view, mainView);
            } catch (InflateException e) {
                mainView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.app_rate, null);
                view = 0;
            } catch (Resources.NotFoundException e) {
                mainView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.app_rate, null);
                view = 0;
            }
        } else {
            mainView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.app_rate, null);
        }


        View close = (View) mainView.findViewById(R.id.dar_close);
        TextView rateElement = (TextView) mainView.findViewById(R.id.dar_rate_element);
        ViewGroup container = (ViewGroup) mainView.findViewById(R.id.dar_container);

        if (container != null) {
            if (fromTop) {
                if (container.getParent() instanceof FrameLayout) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
                    lp.gravity = Gravity.TOP;
                    container.setLayoutParams(lp);
                } else if (container.getParent() instanceof RelativeLayout) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) container.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    container.setLayoutParams(lp);
                }
            } else {
                if (container.getParent() instanceof FrameLayout) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
                    lp.gravity = Gravity.BOTTOM;
                    container.setLayoutParams(lp);
                } else if (container.getParent() instanceof RelativeLayout) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) container.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    container.setLayoutParams(lp);
                }
            }
        }

        if (rateElement != null) {
            rateElement.setText(text);
            rateElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                    hideAllViews(mainView);
                    editor.putBoolean(KEY_CLICKED, true);
                    commitEditor();
                    if (onShowListener != null) onShowListener.onRateAppClicked();

                }
            });
        }

        if (close != null) {
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideAllViews(mainView);
                    if (onShowListener != null) onShowListener.onRateAppDismissed();
                }
            });

        }
        if (view == 0) {
            if (theme == AppRateTheme.LIGHT) {
                PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
                Drawable d = activity.getResources().getDrawable(R.drawable.ic_action_remove);
                d.setColorFilter(Color.BLACK, mMode);
                ((ImageView) close).setImageDrawable(d);

                rateElement.setTextColor(Color.BLACK);

                container.setBackgroundColor(0X88ffffff);


                if (Build.VERSION.SDK_INT >= 16) {
                    close.setBackground(activity.getResources().getDrawable(R.drawable.selectable_button_light));
                } else {
                    close.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selectable_button_light));
                }

            } else {
                Drawable d = activity.getResources().getDrawable(R.drawable.ic_action_remove);
                d.clearColorFilter();
                ((ImageView) close).setImageDrawable(d);

                container.setBackgroundColor(0Xaa000000);

                if (Build.VERSION.SDK_INT >= 16) {
                    close.setBackground(activity.getResources().getDrawable(R.drawable.selectable_button_dark));
                } else {
                    close.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.selectable_button_dark));
                }

            }
        }
        // Manage translucent themes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && container != null) {

            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();

            if (fromTop) {
                boolean isTranslucent = Utils.hasFlag(winParams.flags, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (isTranslucent) {
                    if (debug) LogD("Activity is translucent");


                    int actionBarHeight = 0;
                    //ActionBar size
                    if (activity.getActionBar() != null) {
                        actionBarHeight = activity.getActionBar().getHeight();
                    }

                    if (container.getParent() instanceof FrameLayout) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) container.getLayoutParams();
                        lp.topMargin = Utils.getStatusBarHeight(activity) + actionBarHeight;
                        container.setLayoutParams(lp);
                    } else if (container.getParent() instanceof RelativeLayout) {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) container.getLayoutParams();
                        lp.topMargin = Utils.getStatusBarHeight(activity) + actionBarHeight;
                        container.setLayoutParams(lp);
                    }
                }
            } else {
                boolean isTranslucent = Utils.hasFlag(winParams.flags, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                if (isTranslucent) {
                    if (debug) LogD("Activity is translucent");
                    Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int orientation = display.getRotation();

                    ViewGroup.MarginLayoutParams lp = null;
                    if (container.getParent() instanceof FrameLayout) {
                        lp = (FrameLayout.LayoutParams) container.getLayoutParams();
                    } else if (container.getParent() instanceof RelativeLayout) {
                        lp = (RelativeLayout.LayoutParams) container.getLayoutParams();
                    }


                    if (lp != null) {
                        switch (orientation) {
                            case Surface.ROTATION_0:
                            case Surface.ROTATION_180:
                                lp.bottomMargin = Utils.getSoftbuttonsbarHeight(activity);
                                container.setLayoutParams(lp);
                                break;
                            case Surface.ROTATION_90:
                            case Surface.ROTATION_270:
                                lp.rightMargin = Utils.getSoftbuttonsbarWidth(activity);
                                container.setLayoutParams(lp);
                                break;
                        }
                    }


                }
            }
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

    @SuppressLint("NewApi")
    private void commitEditor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            editor.apply();
        } else {
            editor.commit();
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


        if (mainView != null) {
            Animation fadeInAnimation;
            if (fromTop) {
                fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in_from_top);
            } else {
                fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
            }
            mainView.startAnimation(fadeInAnimation);
        }

        if (onShowListener != null) onShowListener.onRateAppShowing(this, mainView);
    }

    public interface OnShowListener {
        void onRateAppShowing(AppRate appRate, View view);

        void onRateAppDismissed();

        void onRateAppClicked();
    }

    private void LogD(String s) {
        Log.d("DicreetAppRate", s);
    }
}
