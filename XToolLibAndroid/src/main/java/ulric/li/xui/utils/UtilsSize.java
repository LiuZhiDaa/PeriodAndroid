package ulric.li.xui.utils;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class UtilsSize {
    public static int dpToPx(Context context, float fDp) {
        if (null == context)
            return 0;

        return Math.round(fDp * context.getResources().getDisplayMetrics().density);
    }

    public static int pxToDp(Context context, float fPx) {
        if (null == context)
            return 0;

        return Math.round(fPx / context.getResources().getDisplayMetrics().density);
    }

    public static int getDPI(Context context) {
        if (null == context)
            return 0;

        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getScreenWidth(Context context) {
        if (null == context)
            return 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        if (null == context)
            return 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getHeight();
    }
}
