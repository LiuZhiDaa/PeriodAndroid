package ulric.li.ad.impl;

import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdvertisementConfig;
import ulric.li.ad.utils.UtilsAdMob;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;

public class AdMobConfig implements IAdvertisementConfig {
    private String mID = null;
    private int mRetryCount = 1;
    private long mRetryDelayTime = 0;
    private int mCurrentRetryCount = 0;
    private Object mBannerAdSize = null;
    private double mMaskRate = 0;
    private long mInterstitialAdCloseTime = 2000;

    private static boolean sIsAdmobInit = false;

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mID = UtilsJson.JsonUnserialization(jsonObject, "id", "");
        mRetryCount = UtilsJson.JsonUnserialization(jsonObject, "retry_count", mRetryCount);
        mRetryDelayTime = UtilsJson.JsonUnserialization(jsonObject, "retry_delay_time", mRetryDelayTime);
        if (jsonObject.has("banner_size")) {
            String strBannerSize = "";
            strBannerSize = UtilsJson.JsonUnserialization(jsonObject, "banner_size", strBannerSize);
            if (!TextUtils.isEmpty(strBannerSize)) {
                if (UtilsAdMob.VALUE_STRING_BANNER_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.BANNER;
                } else if (UtilsAdMob.VALUE_STRING_LARGE_BANNER_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.FULL_BANNER;
                } else if (UtilsAdMob.VALUE_STRING_MEDIUM_RECTANGLE_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE;
                } else if (UtilsAdMob.VALUE_STRING_FULL_BANNER_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.FULL_BANNER;
                } else if (UtilsAdMob.VALUE_STRING_LEADERBOARD_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.LEADERBOARD;
                } else if (UtilsAdMob.VALUE_STRING_SMART_BANNER_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = com.google.android.gms.ads.AdSize.SMART_BANNER;
                }
            }
        }

        if (!sIsAdmobInit && jsonObject.has("app_id")) {
            String strAppID = UtilsJson.JsonUnserialization(jsonObject, "app_id", "");
            sIsAdmobInit = true;
            if (UtilsEnv.isDebuggable(XProfitFactory.getApplication())) {
                UtilsAdMob.init(XProfitFactory.getApplication(), UtilsAdMob.VALUE_STRING_ADMOB_APP_TEST_ID);
            } else {
                UtilsAdMob.init(XProfitFactory.getApplication(), strAppID);
            }
        }

        //控制点击率相关参数
        mMaskRate = UtilsJson.JsonUnserialization(jsonObject, "mask_rate", mMaskRate);
        mInterstitialAdCloseTime = UtilsJson.JsonUnserialization(jsonObject, "close_time", mInterstitialAdCloseTime);
    }

    @Override
    public String getAdID() {
        return mID;
    }

    @Override
    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public long getRetryDelayTime() {
        return mRetryDelayTime;
    }

    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    @Override
    public void setCurrentRetryCount(int nCurrentRetryCount) {
        mCurrentRetryCount = nCurrentRetryCount;
    }

    @Override
    public List<Integer> getRetryCodeList() {
        List<Integer> listRetryCode = new ArrayList<>();
        listRetryCode.add(AdRequest.ERROR_CODE_NETWORK_ERROR);
        return listRetryCode;
    }

    @Override
    public Object getBannerAdSize() {
        return mBannerAdSize;
    }

    @Override
    public double getMaskRate() {
        return mMaskRate;
    }

    @Override
    public long getInterstitialAdCloseTime() {
        return mInterstitialAdCloseTime;
    }

}
