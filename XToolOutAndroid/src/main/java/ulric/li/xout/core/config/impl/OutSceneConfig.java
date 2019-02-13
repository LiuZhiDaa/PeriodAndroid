package ulric.li.xout.core.config.impl;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;
import ulric.li.xout.core.config.intf.IOutSceneConfig;

public class OutSceneConfig implements IOutSceneConfig {
    private boolean mIsEnable = false;
    private boolean mIsAdEnable = false;
    private int mCountLimitOneDay = 3;
    private long mProtectTime = 2 * 60 * 60 * 1000;
    private long mProtectRandomTime = 10 * 60 * 1000;

    private float mBoostMemoryRate = 0.5f;
    private float mCoolTemperature = 35f;
    private String mLockCondition = null;

    public OutSceneConfig() {
        _init();
    }

    private void _init() {
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mIsEnable = UtilsJson.JsonUnserialization(jsonObject, "enable", mIsEnable);
        mIsAdEnable = UtilsJson.JsonUnserialization(jsonObject, "ad_enable", mIsAdEnable);
        mCountLimitOneDay = UtilsJson.JsonUnserialization(jsonObject, "count_limit_one_day", mCountLimitOneDay);
        mProtectTime = UtilsJson.JsonUnserialization(jsonObject, "protect_time", mProtectTime);
        mProtectRandomTime = UtilsJson.JsonUnserialization(jsonObject, "protect_random_time", mProtectRandomTime);
        mBoostMemoryRate = UtilsJson.JsonUnserialization(jsonObject, "out_scene_boost_memory_rate", mBoostMemoryRate);
        mCoolTemperature = UtilsJson.JsonUnserialization(jsonObject, "out_scene_cool_temperature_rate", mCoolTemperature);
        mLockCondition = UtilsJson.JsonUnserialization(jsonObject, "out_scene_lock_condition", "");
    }

    @Override
    public boolean isEnable() {
        return mIsEnable;
    }

    @Override
    public boolean isAdEnable() {
        return mIsAdEnable;
    }

    @Override
    public int getCountLimitOneDay() {
        return mCountLimitOneDay;
    }

    @Override
    public long getProtectTime() {
        return mProtectTime;
    }

    @Override
    public long getProtectRandomTime() {
        return mProtectRandomTime;
    }

    @Override
    public float getOutSceneBoostRate() {
        return mBoostMemoryRate;
    }

    @Override
    public float getOutSceneCoolTemperature() {
        return mCoolTemperature;
    }

    @Override
    public String getOutSceneLockCondition() {
        return mLockCondition;
    }
}
