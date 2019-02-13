package ulric.li.xout.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONObject;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IWakeLockTool;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.service.OutService;

/**
 * Created by WangYu on 2018/9/3.
 */
public class HelpServiceReceiver extends BroadcastReceiver {
    private static final String BROADCAST_ACTION = "com.start.out.service";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent==null) {
            return;
        }
        UtilsLog.logI("UtilsProfitLog","receive_alive_broadcast：" + intent.getAction());
        if (BROADCAST_ACTION.equals(intent.getAction())) {
            IWakeLockTool wakeLockTool = (IWakeLockTool) XLibFactory.getInstance().createInstance(IWakeLockTool.class);
            if (wakeLockTool.setWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "broadcast_wake")) {
                wakeLockTool.acquire(60*1000);
            }

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
            long lastLoopTime = sharedPreferences.getLong("out_loop_time", 0);
            long currentTime = System.currentTimeMillis();
            IOutConfig mIOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
            boolean shouldStartOutService=false;
            if ((currentTime - lastLoopTime) > mIOutConfig.getOutSceneLoopTime()*2) {
                shouldStartOutService=true;
                OutService.start(context);
                IOutSceneMgr outSceneMgr = (IOutSceneMgr) XOutFactory.getInstance().createInstance(IOutSceneMgr.class);
                outSceneMgr.startActiveSceneLoop();
            }
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "shouldStart", shouldStartOutService);
            UtilsJson.JsonSerialization(jsonObject, "intervalTime", currentTime-lastLoopTime);
            UtilsJson.JsonSerialization(jsonObject, "loopTime", mIOutConfig.getOutSceneLoopTime());
            UtilsLog.statisticsLog("out", "broad_result", jsonObject);

        }
    }

    public static void register(Context context) {
        try {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION);
            localBroadcastManager.registerReceiver(ClassHolder.instance, intentFilter);
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }

    }

    private final static class ClassHolder {
        private final static HelpServiceReceiver instance = new HelpServiceReceiver();
    }
}
