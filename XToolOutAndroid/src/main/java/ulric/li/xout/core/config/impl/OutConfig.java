package ulric.li.xout.core.config.impl;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.config.intf.IOutSceneConfig;

public class OutConfig implements IOutConfig {
    private boolean mIsOutEnable = false;
    private boolean mIsOutAdEnable = false;
    private boolean mIsOutStatementEnable = false;
    private long mOutLatentTime = 8 * 60 * 60 * 1000;
    private long mOutSceneLoopTime = 60 * 1000;
    private List<String> mListScene = null;
    private Map<String, IOutSceneConfig> mMapOutSceneConfig = null;
    private long mActiveSceneProtectBaseTime = 600000;
    private long mActiveSceneProtectRandomTime = 300000;
    private boolean isShowNotification = false;

    public OutConfig() {
        _init();
    }

    private void _init() {
        mListScene = new ArrayList<>();
        mMapOutSceneConfig = new HashMap<>();
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mIsOutEnable = UtilsJson.JsonUnserialization(jsonObject, "out_enable", mIsOutEnable);
        mIsOutAdEnable = UtilsJson.JsonUnserialization(jsonObject, "out_ad_enable", mIsOutAdEnable);
        mIsOutStatementEnable = UtilsJson.JsonUnserialization(jsonObject, "out_statement_enable", mIsOutStatementEnable);
        mOutLatentTime = UtilsJson.JsonUnserialization(jsonObject, "out_latent_time", mOutLatentTime);
        mOutSceneLoopTime = UtilsJson.JsonUnserialization(jsonObject, "out_scene_loop_time", mOutSceneLoopTime);
        mListScene = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "out_scene_list", mListScene, String.class, null, null);
        UtilsJson.JsonUnserialization(jsonObject, "out_scene", mMapOutSceneConfig, String.class, IOutSceneConfig.class, IOutSceneConfig.class, OutSceneConfig.class);
        mActiveSceneProtectBaseTime = UtilsJson.JsonUnserialization(jsonObject, "out_active_scene_protect_base_time", mActiveSceneProtectBaseTime);
        mActiveSceneProtectRandomTime = UtilsJson.JsonUnserialization(jsonObject, "out_active_scene_protect_random_time", mActiveSceneProtectRandomTime);
        isShowNotification = UtilsJson.JsonUnserialization(jsonObject, "", false);
    }

    @Override
    public boolean isOutEnable() {
        return mIsOutEnable;
    }

    @Override
    public boolean isOutAdEnable() {
        return mIsOutAdEnable;
    }

    @Override
    public boolean isOutStatementEnable() {
        return mIsOutStatementEnable;
    }

    @Override
    public long getOutLatentTime() {
        return mOutLatentTime;
    }

    @Override
    public boolean isInLatentTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        long first_start_time = sp.getLong("first_start_time", 0);
        if (first_start_time == 0) {
            sp.edit().putLong("first_start_time", System.currentTimeMillis()).apply();
            return true;
        }
        if (System.currentTimeMillis() - first_start_time < getOutLatentTime()) {
            UtilsLog.logI("wangyu", "正在潜伏期：" + getOutLatentTime());
            return true;
        }
        return false;
    }

    @Override
    public long getOutSceneLoopTime() {
        return mOutSceneLoopTime;
    }

    @Override
    public List<String> getOutSceneList() {
        return mListScene;
    }

    @Override
    public boolean isOutSceneEnable(String strSceneType) {
        if (TextUtils.isEmpty(strSceneType) || !mIsOutEnable
                || !mListScene.contains(strSceneType) || !mMapOutSceneConfig.containsKey(strSceneType))
            return false;

        IOutSceneConfig iOutSceneConfig = mMapOutSceneConfig.get(strSceneType);
        if (null == iOutSceneConfig)
            return false;

        return iOutSceneConfig.isEnable();
    }

    @Override
    public boolean isOutSceneAdEnable(String strSceneType) {
        if (TextUtils.isEmpty(strSceneType) || !mIsOutEnable
                || !mListScene.contains(strSceneType) || !mMapOutSceneConfig.containsKey(strSceneType))
            return false;

        IOutSceneConfig iOutSceneConfig = mMapOutSceneConfig.get(strSceneType);
        if (null == iOutSceneConfig)
            return false;

        return iOutSceneConfig.isAdEnable();
    }

    @Override
    public int getOutSceneCountLimitOneDay(String strSceneType) {
        if (TextUtils.isEmpty(strSceneType) || !mIsOutEnable
                || !mListScene.contains(strSceneType) || !mMapOutSceneConfig.containsKey(strSceneType))
            return 0;

        IOutSceneConfig iOutSceneConfig = mMapOutSceneConfig.get(strSceneType);
        if (null == iOutSceneConfig)
            return 0;

        return iOutSceneConfig.getCountLimitOneDay();
    }

    @Override
    public long getOutSceneProtectTime(String strSceneType) {
        if (TextUtils.isEmpty(strSceneType) || !mIsOutEnable
                || !mListScene.contains(strSceneType) || !mMapOutSceneConfig.containsKey(strSceneType))
            return 0;

        IOutSceneConfig iOutSceneConfig = mMapOutSceneConfig.get(strSceneType);
        if (null == iOutSceneConfig)
            return 0;

        return iOutSceneConfig.getProtectTime();
    }

    @Override
    public long getOutSceneProtectRandomTime(String strSceneType) {
        if (TextUtils.isEmpty(strSceneType) || !mIsOutEnable
                || !mListScene.contains(strSceneType) || !mMapOutSceneConfig.containsKey(strSceneType))
            return 0;

        IOutSceneConfig iOutSceneConfig = mMapOutSceneConfig.get(strSceneType);
        if (null == iOutSceneConfig)
            return 0;

        return iOutSceneConfig.getProtectRandomTime();
    }

    @Override
    public IOutSceneConfig getOutSceneConfig(String strSceneType) {
        if (mMapOutSceneConfig == null || TextUtils.isEmpty(strSceneType)) {
            return null;
        }
        return mMapOutSceneConfig.get(strSceneType);
    }

    @Override
    public boolean isInActiveSceneProtectTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        long lastTriggerTime = sp.getLong("active_scene_trigger_time", 0);
        Random random = new Random();
        UtilsLog.logI("wangyu", "goTime:" + (System.currentTimeMillis() - lastTriggerTime));
        UtilsLog.logI("wangyu", "randomTime:" + random.nextFloat() * mActiveSceneProtectRandomTime);
        return (System.currentTimeMillis() - lastTriggerTime) < (mActiveSceneProtectBaseTime + random.nextFloat() * mActiveSceneProtectRandomTime);
    }

    @Override
    public void recordActiveSceneTriggerTime() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
        sp.edit().putLong("active_scene_trigger_time", System.currentTimeMillis()).apply();
    }

    @Override
    public boolean isShowNotification() {
        return false;
    }

}
