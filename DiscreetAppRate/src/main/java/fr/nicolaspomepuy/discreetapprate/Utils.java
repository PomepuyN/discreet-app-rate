package fr.nicolaspomepuy.discreetapprate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by nicolas on 06/03/14.
 */
public class Utils {

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
        try
        {
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            app_installed = (label != null && !label.equals("Market"));
        }
        catch (PackageManager.NameNotFoundException e)
        {
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
}
