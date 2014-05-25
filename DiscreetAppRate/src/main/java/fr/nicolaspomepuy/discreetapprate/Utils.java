package fr.nicolaspomepuy.discreetapprate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by nicolas on 06/03/14.
 */
public class Utils {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";

    /**
     * Convert a size in dp to a size in pixels
     *
     * @param context the {@link android.content.Context} to be used
     * @param dpi     size in dp
     * @return the size in pixels
     */
    public static int convertDPItoPixels(Context context, int dpi) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpi * scale + 0.5f);
    }

    public static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    public static Date installTimeFromPackageManager(
            PackageManager packageManager, String packageName) {
        // API level 9 and above have the "firstInstallTime" field.
        // Check for it with reflection and return if present.
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            Field field = PackageInfo.class.getField("firstInstallTime");
            long timestamp = field.getLong(info);
            return new Date(timestamp);
        } catch (PackageManager.NameNotFoundException e) {
            return null; // package not found
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        // field wasn't found
        return null;
    }

    public static boolean isGooglePlayInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            app_installed = (label != null && !label.equals("Market"));
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /*
     * This method check network state for opening Google Play Store If network
     * is not avaible AppRate will not try to show up.
     *
     * This method return true if application doesn't has "ACCESS_NETWORK_STATE" permission.
     */
    public static boolean isOnline(Context context) {
        int res = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        if (res == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }

        return true;
    }

    @SuppressLint("NewApi")
    public static int getSoftbuttonsbarHeight(Activity activity) {
        if (!getInternalBoolean(activity.getResources(), SHOW_NAV_BAR_RES_NAME)) {
            return 0;
        }
        return getInternalDimensionSize(activity.getResources(), NAV_BAR_HEIGHT_RES_NAME);
    }

    @SuppressLint("NewApi")
    public static int getSoftbuttonsbarWidth(Activity activity) {
        if (!getInternalBoolean(activity.getResources(), SHOW_NAV_BAR_RES_NAME)) {
            return 0;
        }
        return getInternalDimensionSize(activity.getResources(), NAV_BAR_WIDTH_RES_NAME);
    }

    @SuppressLint("NewApi")
    public static int getStatusBarHeight(Activity activity) {
        return getInternalDimensionSize(activity.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }


    public static int getActionStatusBarHeight(Activity activity) {
        return getStatusBarHeight(activity) + getActionBarHeight(activity);
    }

    public static int getActionBarHeight(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
        }
        return 0;
    }

    public static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static boolean getInternalBoolean(Resources res, String key) {
        int resourceId = res.getIdentifier(key, "bool", "android");
        return (resourceId > 0) ? res.getBoolean(resourceId) : false;
    }

    public static boolean hasFlag(int flags, int flag) {
        return (flags & flag) == flag;
    }
}
