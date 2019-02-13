package ulric.li.xout.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.scene.impl.OutSceneLock;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgr;

/**
 * Created by WangYu on 2018/7/3.
 */
public class OutBroadcastReceiver extends BroadcastReceiver {
    private boolean mIsFirstNetChange = true;

    public static String[] getCommonFilterAction() {
        return mCommonActionArr;
    }

    public static String[] getPackageFilterAction() {
        return mPackageActionArr;
    }

    private static final String[] mCommonActionArr = {
            Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF,//亮灭屏
            Intent.ACTION_USER_PRESENT,//解锁
            Intent.ACTION_POWER_CONNECTED, Intent.ACTION_POWER_DISCONNECTED,//充电断电
//            Intent.ACTION_BATTERY_CHANGED, Intent.ACTION_BATTERY_LOW, Intent.ACTION_BATTERY_OKAY,//电池变化
            ConnectivityManager.CONNECTIVITY_ACTION//网络变化
    };

    private static final String[] mPackageActionArr = {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED, Intent.ACTION_MY_PACKAGE_REPLACED
    };

    public OutBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        UtilsLog.logI("UtilsProfitLog", intent.getAction());
        String scene = null;
        JSONObject data=null;
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED://充电
                scene = IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE;
                break;
            case Intent.ACTION_POWER_DISCONNECTED://断电
                scene = IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE;
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION://网络变化
                if (mIsFirstNetChange) {
                    mIsFirstNetChange = false;
                    return;
                }
                int netType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, 999);
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager == null)
                    return;

                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况
                if (activeNetInfo == null)
                    return;

                Log.i("wangyu", "netInfo:"+activeNetInfo.getType());
                Log.i("wangyu", "netType:"+netType);
                if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                        && netType == ConnectivityManager.TYPE_WIFI) {
                    scene = IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE;
                } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE
                        && netType == ConnectivityManager.TYPE_MOBILE) {
                    scene = IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE;
                }
                break;
            case Intent.ACTION_PACKAGE_REMOVED://应用卸载
                boolean isUpdate = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                if (!isUpdate) {
                    scene = IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE;
                }
                break;
            case Intent.ACTION_PACKAGE_ADDED://应用新安装或者更新
                boolean isUpdate1 = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                scene = isUpdate1 ? IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE : IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE;
                break;
            case Intent.ACTION_SCREEN_ON://亮屏
                scene=IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE;
                data = new JSONObject();
                UtilsJson.JsonSerialization(data, OutSceneLock.VALUE_STRING_STATUS_KEY,"screen_on");
                break;
            case Intent.ACTION_SCREEN_OFF://灭屏
                scene=IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE;
                data = new JSONObject();
                UtilsJson.JsonSerialization(data,OutSceneLock.VALUE_STRING_STATUS_KEY,"screen_off");
                break;
            case Intent.ACTION_USER_PRESENT://解锁
                scene=IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE;
                data = new JSONObject();
                UtilsJson.JsonSerialization(data,OutSceneLock.VALUE_STRING_STATUS_KEY,"present");
                break;
        }
        if (TextUtils.isEmpty(scene)) {
            return;
        }
        //触发被动逻辑
        IOutTriggerMgr IOutTriggerMgr = (IOutTriggerMgr) XOutFactory.getInstance().createInstance(IOutTriggerMgr.class);
        IOutTriggerMgr.triggerPassivePage(scene, data);

    }

}
