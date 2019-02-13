package ulric.li.xout.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.util.List;

import ulric.li.xout.core.XOutFactory;

/**
 * Created by WangYu on 2018/7/19.
 */
public class UtilsApp {

    public static void killBackgroundProcess(Context context) {
        if (context == null) {
            return;
        }
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ApplicationInfo> list = XOutFactory.getApplication().getPackageManager().getInstalledApplications(0);
            for (ApplicationInfo ai : list) {
                if (null == ai || TextUtils.isEmpty(ai.packageName))
                    continue;

                // 忽略系统应用
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    continue;

                if (ai.packageName.contains("google") || ai.packageName.contains("android")
                        || ai.packageName.contains("facebook")) {
                    continue;
                }

                // 忽略自身应用
                if (ai.packageName.contains(ulric.li.utils.UtilsApp.getMyAppPackageName(XOutFactory.getApplication())))
                    continue;

                am.killBackgroundProcesses(ai.packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
