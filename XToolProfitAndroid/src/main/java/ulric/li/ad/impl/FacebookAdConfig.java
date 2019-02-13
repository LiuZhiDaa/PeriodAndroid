package ulric.li.ad.impl;

import android.text.TextUtils;

import com.facebook.ads.AdSize;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ulric.li.ad.intf.IAdvertisementConfig;
import ulric.li.ad.utils.UtilsFacebookAd;
import ulric.li.utils.UtilsJson;

public class FacebookAdConfig implements IAdvertisementConfig {
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
                if (UtilsFacebookAd.VALUE_STRING_BANNER_50_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = AdSize.BANNER_HEIGHT_50;
                } else if (UtilsFacebookAd.VALUE_STRING_BANNER_90_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = AdSize.BANNER_HEIGHT_90;
                } else if (UtilsFacebookAd.VALUE_STRING_BANNER_RECTANGLE_HEIGHT_250_SIZE.equals(strBannerSize)) {
                    mBannerAdSize = AdSize.RECTANGLE_HEIGHT_250;
                }
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
        listRetryCode.add(1000);
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
