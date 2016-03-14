package barqsoft.footballscores.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * Created by xiaoma on 14/03/16.
 */
public class Utilities {
    public static float getSmallWithDisplay(Activity activity) {

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float scaleFactor = metrics.density;

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        return Math.min(widthDp, heightDp);
    }

    public static boolean isLandScape(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
