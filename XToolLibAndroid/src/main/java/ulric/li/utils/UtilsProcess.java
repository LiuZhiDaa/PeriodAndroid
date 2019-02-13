package ulric.li.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

public class UtilsProcess {
    public static boolean killBackgroundProcess(Context context,
                                                String strPackageName) {
        if (null == context || TextUtils.isEmpty(strPackageName))
            return false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(strPackageName);
        return true;
    }

    public static boolean killProcessByRoot(int nPID) {
        return true;
    }

    public static boolean killProcessByForceStop(Context context, String strPackageName) {
        if (null == context || TextUtils.isEmpty(strPackageName))
            return false;

        Intent intent = null;
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", strPackageName, null));
        if (!Activity.class.isAssignableFrom(context.getClass())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
        return true;
    }

    public static int getMyPID() {
        return android.os.Process.myPid();
    }

    private static String sProcessName = null;

    public static String getMyProcessName(Context context) {
        if (null == context)
            return null;

        if (!TextUtils.isEmpty(sProcessName))
            return sProcessName;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : am.getRunningAppProcesses()) {
            if (null == runningAppProcessInfo)
                continue;

            if (runningAppProcessInfo.pid == getMyPID()) {
                sProcessName = runningAppProcessInfo.processName;
            }
        }

        return sProcessName;
    }
}
