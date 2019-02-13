package ulric.li.logic.alive;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IDelayTool;
import ulric.li.tool.intf.IScreenObserver;
import ulric.li.tool.intf.IScreenObserverListener;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsTime;

public class AliveService extends Service {
    private IScreenObserver mIScreenObserver = null;
    private IScreenObserverListener mIScreenObserverListener = null;
    private IDelayTool mIDelayTool = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mIScreenObserver = (IScreenObserver) XLibFactory.getInstance().createInstance(IScreenObserver.class);
        mIScreenObserverListener = new IScreenObserverListener() {
            @Override
            public void onScreenOn() {
                if (mIDelayTool.isExceedSpacing(true)) {
                    UtilsLog.aliveLog("service", null);
                    UtilsLog.sendLog();
                    UtilsBroadcast.sendAliveBroadcast(XLibFactory.getApplication());
                }
            }

            @Override
            public void onScreenOff() {
            }

            @Override
            public void onUserPresent() {
            }
        };
        mIScreenObserver.addListener(mIScreenObserverListener);
        mIScreenObserver.startListen();

        mIDelayTool = (IDelayTool) XLibFactory.getInstance().createInstance(IDelayTool.class);
        mIDelayTool.setSpacingTime(UtilsTime.VALUE_LONG_TIME_ONE_MINUTE * 5);
        mIDelayTool.updateBeforeTime();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIScreenObserver.stopListen();
        mIScreenObserver.removeListener(mIScreenObserverListener);
    }

    public static void startAliveService(Context context) {
        if (null == context)
            return;

        try {
            Intent intent = new Intent(context, AliveService.class);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
