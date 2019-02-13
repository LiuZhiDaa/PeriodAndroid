package com.period.app.core.config.impl;





import com.period.app.core.config.intf.ICloudConfig;

import org.json.JSONObject;

import ulric.li.utils.UtilsJson;

public class CloudConfig implements ICloudConfig {
    private boolean mIsAdEnable = false;
    private int mOpenInterstitial = 1;
    //点击次数
    private int mClickNum = 3;

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mIsAdEnable = UtilsJson.JsonUnserialization(jsonObject, "ad_enable", false);
        mOpenInterstitial = UtilsJson.JsonUnserialization(jsonObject, "open_interstitial_times", mOpenInterstitial);
        mClickNum = UtilsJson.JsonUnserialization(jsonObject, "click_num", mClickNum);
    }

    @Override
    public boolean isAdEnable() {
        return mIsAdEnable;
    }

    @Override
    public int getOpenInterstitialTimes() {
        return mOpenInterstitial;
    }

    @Override
    public int getmClickNum() {
        return mClickNum;
    }
}
