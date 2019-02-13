package ulric.li.xout.core.scene.impl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import ulric.li.utils.UtilsTime;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneListener;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;

import static ulric.li.xout.core.scene.constant.BatteryOptConstant.VALUE_STR_OUT_BATTERY_OPT_COUNT;
import static ulric.li.xout.core.scene.constant.BatteryOptConstant.VALUE_STR_OUT_BATTERY_OPT_DATE;
import static ulric.li.xout.core.scene.constant.BatteryOptConstant.VALUE_STR_OUT_BATTERY_OPT_TRIGGER_TIME;

/**
 * Project name XToolCameraAndroid
 * Created by gongguan on 2018/8/23.
 */
public class OutSceneOptimizeBattery implements IOutScene {

    @Override
    public String getType() {
        return IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE;
    }

    @Override
    public boolean isNeedTriggered(JSONObject condition) {
        //检查开关
        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (!iOutConfig.isOutAdEnable() || !iOutConfig.isOutSceneAdEnable(getType())) {
            return false;
        }

        //场景更新为今天
        long lTime = System.currentTimeMillis();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        String strSceneTime = sp.getString(VALUE_STR_OUT_BATTERY_OPT_DATE, "");
        String strTime = UtilsTime.getDateStringYyyyMmDd(lTime);
        if (0 != strTime.compareTo(strSceneTime)) {
            sp.edit().putString(VALUE_STR_OUT_BATTERY_OPT_DATE, strTime).apply();
            sp.edit().putInt(VALUE_STR_OUT_BATTERY_OPT_COUNT, 0).apply();
        }

        //每日限制次数
        int nSceneCount = sp.getInt(VALUE_STR_OUT_BATTERY_OPT_COUNT, 0);
        if (nSceneCount >= iOutConfig.getOutSceneCountLimitOneDay(getType())) {
            return false;
        }

        //保护时间
        long lSceneTriggerTime = sp.getLong(VALUE_STR_OUT_BATTERY_OPT_TRIGGER_TIME, 0);
        return !(lTime - lSceneTriggerTime < iOutConfig.getOutSceneProtectTime(getType())
                + Math.random() * iOutConfig.getOutSceneProtectRandomTime(getType()));
    }

    @Override
    public boolean updateTriggered() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        int nSceneCount = sp.getInt(VALUE_STR_OUT_BATTERY_OPT_COUNT, 0);
        sp.edit().putInt(VALUE_STR_OUT_BATTERY_OPT_COUNT, ++nSceneCount).apply();
        sp.edit().putLong(VALUE_STR_OUT_BATTERY_OPT_TRIGGER_TIME, System.currentTimeMillis()).apply();

        return false;
    }

    @Override
    public boolean executeAsync(IOutSceneListener iOutSceneListener) {
        return false;
    }
}
