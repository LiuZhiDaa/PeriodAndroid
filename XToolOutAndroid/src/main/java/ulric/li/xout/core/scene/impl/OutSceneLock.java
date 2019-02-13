package ulric.li.xout.core.scene.impl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsTime;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.config.intf.IOutSceneConfig;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneListener;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;

/**
 * Created by WangYu on 2018/8/2.
 */
public class OutSceneLock implements IOutScene {
    public static final String VALUE_STRING_STATUS_KEY = "lock_status";

    @Override
    public String getType() {
        return IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE;
    }

    @Override
    public boolean isNeedTriggered(JSONObject condition) {
        // 检查开关
        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (!iOutConfig.isOutEnable() || !iOutConfig.isOutSceneEnable(getType())) {
            sendBreakLog("switch off");
            return false;
        }

        long lTime = System.currentTimeMillis();

        // 场景次数更新为今天
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        String strSceneTime = sp.getString("out_lock_scene_date", "");
        String strTime = UtilsTime.getDateStringYyyyMmDd(lTime);
        if (0 != strTime.compareTo(strSceneTime)) {
            sp.edit().putString("out_lock_scene_date", strTime).apply();
            sp.edit().putInt("out_lock_scene_count", 0).apply();
        }

        // 场景每天次数限制
        int nSceneCount = sp.getInt("out_lock_scene_count", 0);
        int totalCount = iOutConfig.getOutSceneCountLimitOneDay(getType());
        if (nSceneCount >= totalCount) {
            sendBreakLog("reach config count, current:"+nSceneCount+" config:"+totalCount);
            return false;
        }

        // 保护时间
        long lSceneTriggeredTime = sp.getLong("out_lock_scene_triggered_time", 0);
        double configTime = iOutConfig.getOutSceneProtectTime(getType())
                + Math.random() * iOutConfig.getOutSceneProtectRandomTime(getType());
        if (lTime - lSceneTriggeredTime < configTime) {
            sendBreakLog("in protect time, last:"+lSceneTriggeredTime+" config:"+condition);
            return false;
        }

        if (condition == null) {
            sendBreakLog("condition json is null");
            return false;
        }

        String status = UtilsJson.JsonUnserialization(condition, VALUE_STRING_STATUS_KEY, "");

        if (TextUtils.isEmpty(status)) {
            sendBreakLog("status is empty");
            return false;
        }
        IOutSceneConfig outSceneConfig = iOutConfig.getOutSceneConfig(getType());
        if (outSceneConfig==null) {
            sendBreakLog("scene config is null");
            return false;
        }
        if (!status.equals(outSceneConfig.getOutSceneLockCondition())) {
            sendBreakLog("status is not match, status:"+status+" config:"+outSceneConfig.getOutSceneLockCondition());
            return false;
        }

        return true;
    }

    @Override
    public boolean updateTriggered() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        int nSceneCount = sp.getInt("out_lock_scene_count", 0);
        sp.edit().putInt("out_lock_scene_count", ++nSceneCount).apply();
        sp.edit().putLong("out_lock_scene_triggered_time", System.currentTimeMillis()).apply();
        return true;
    }

    @Override
    public boolean executeAsync(IOutSceneListener iOutSceneListener) {
        return false;
    }

    private void sendBreakLog(String reason) {
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject,"reason",reason);
        UtilsLog.statisticsLog("out","lock_no_condition",jsonObject);
    }
}
