package fr.nicolaspomepuy.discreetapprate;

import android.content.Context;

/**
 * Created by nicolas on 06/03/14.
 */
public class Utils {

    /**
     * Convert a size in dp to a size in pixels
     * @param context the {@link android.content.Context} to be used
     * @param dpi size in dp
     * @return the size in pixels
     */
    public static int convertDPItoPixels(Context context, int dpi) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpi * scale + 0.5f);
    }

    public static boolean isPowerOfTwo(int x)    {
        return (x & (x - 1)) == 0;
    }
}
