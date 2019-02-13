package ulric.li.xout.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.util.ArrayList;

import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;

/**
 * Created by WangYu on 2018/6/29.
 */
public class OutService extends Service {
    private static final ArrayList<String> mForegroundDeviceList = new ArrayList<>();
    static {
        mForegroundDeviceList.add("samsung");
    }
    private IOutSceneMgr mIOutSceneMgr = null;


    public static void start(Context context) {
        if (context == null) {
            return;
        }
        try {
            if (shouldStartForeground()) {
                context.startForegroundService(new Intent(context, OutService.class));
            } else {
                context.startService(new Intent(context, OutService.class));
            }
        } catch (Exception e) {
//            UtilsProfitLog.crashLog(e);
        }
    }

    private static boolean shouldStartForeground() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mForegroundDeviceList.contains(android.os.Build.BRAND);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UtilsLog.statisticsLog("out", "start_service", null);
        if (shouldStartForeground()) {
            startForeground(1,new Notification());
        }

        try {
            mIOutSceneMgr = (IOutSceneMgr) XOutFactory.getInstance().createInstance(IOutSceneMgr.class);
            mIOutSceneMgr.startListen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        start(XOutFactory.getApplication());
    }


}
