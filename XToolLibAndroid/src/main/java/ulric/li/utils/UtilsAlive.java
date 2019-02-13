package ulric.li.utils;

import android.content.Context;

import ulric.li.logic.alive.AliveBroadcast;
import ulric.li.logic.alive.AliveJobService;
import ulric.li.logic.alive.AliveService;

public class UtilsAlive {
    public static void init(Context context) {
        if (null == context)
            return;

        AliveService.startAliveService(context);
        AliveBroadcast.registerAliveBroadcast(context);
        AliveJobService.jobSchedule(context);

        int nInstallType = UtilsInstall.getInstallType(context);
        if (UtilsInstall.VALUE_INT_INSTALL_NEW_TYPE == nInstallType) {
            UtilsLog.aliveLog("new", null);
        } else if (UtilsInstall.VALUE_INT_INSTALL_UPDATE_TYPE == nInstallType) {
            UtilsLog.aliveLog("update", null);
        } else {
            UtilsLog.aliveLog("application", null);
        }

        UtilsLog.sendLog();
    }
}
