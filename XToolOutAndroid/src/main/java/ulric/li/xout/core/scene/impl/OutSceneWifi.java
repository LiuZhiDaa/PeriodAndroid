package ulric.li.xout.core.scene.impl;

import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import ulric.li.XLibFactory;
import ulric.li.utils.UtilsNetwork;
import ulric.li.utils.UtilsTime;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneListener;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;

public class OutSceneWifi implements IOutScene {
    @Override
    public String getType() {
        return IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE;
    }

    @Override
    public boolean isNeedTriggered(JSONObject condition) {
        // 检查开关
        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (!iOutConfig.isOutEnable() || !iOutConfig.isOutSceneEnable(getType()))
            return false;

        long lTime = System.currentTimeMillis();

        // 场景次数更新为今天
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        String strSceneTime = sp.getString("out_wifi_scene_date", "");
        String strTime = UtilsTime.getDateStringYyyyMmDd(lTime);
        if (0 != strTime.compareTo(strSceneTime)) {
            sp.edit().putString("out_wifi_scene_date", strTime).apply();
            sp.edit().putInt("out_wifi_scene_count", 0).apply();
        }

        // 场景每天次数限制
        int nSceneCount = sp.getInt("out_wifi_scene_count", 0);
        if (nSceneCount >= iOutConfig.getOutSceneCountLimitOneDay(getType()))
            return false;

        // 保护时间
        long lSceneTriggeredTime = sp.getLong("out_wifi_scene_triggered_time", 0);
        if (lTime - lSceneTriggeredTime < iOutConfig.getOutSceneProtectTime(getType())
                + Math.random() * iOutConfig.getOutSceneProtectRandomTime(getType()))
            return false;

        // 触发逻辑判断
        if (!UtilsNetwork.isConnectNetwork(XOutFactory.getApplication()))
            return false;

        return true;
    }

    @Override
    public boolean updateTriggered() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        int nSceneCount = sp.getInt("out_wifi_scene_count", 0);
        sp.edit().putInt("out_wifi_scene_count", ++nSceneCount).apply();
        sp.edit().putLong("out_wifi_scene_triggered_time", System.currentTimeMillis()).apply();
        return true;
    }

    @Override
    public boolean executeAsync(final IOutSceneListener iOutSceneListener) {
        if (null == iOutSceneListener)
            return false;

        IXThreadPool iXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        iXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                ulric.li.xout.utils.UtilsApp.killBackgroundProcess(XOutFactory.getApplication());
            }

            @Override
            public void onTaskComplete() {
                if (null != iOutSceneListener)
                    iOutSceneListener.onExecuteAsyncComplete(OutSceneWifi.this);
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }
}
