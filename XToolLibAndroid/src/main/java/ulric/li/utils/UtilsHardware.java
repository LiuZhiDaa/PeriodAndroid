package ulric.li.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class UtilsHardware {
    public static int getCPUCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void vibrate(Context context, long lTime) {
        if (null == context)
            return;

        Vibrator vib = (Vibrator) context
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(lTime);
    }

    public static void vibrate(Context context, long[] pattern, boolean bRepeat) {
        if (null == context)
            return;

        Vibrator vib = (Vibrator) context
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, bRepeat ? 1 : -1);
    }
}
