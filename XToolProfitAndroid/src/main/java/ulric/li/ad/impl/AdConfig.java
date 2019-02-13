package ulric.li.ad.impl;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdvertisementConfig;
import ulric.li.ad.utils.UtilsAdMob;
import ulric.li.ad.utils.UtilsFacebookAd;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;

public class AdConfig implements IAdConfig {
    private String mKey = null;
    private String mAdType = VALUE_STRING_UNKNOWN_AD_TYPE;
    private int mCacheCount = 1;
    private List<String> mListRequestScene = null;
    private List<String> mListShowScene = null;
    private List<String> mListChannel = null;
    private HashMap<String, IAdvertisementConfig> mMapAdvertisementConfig = null;

    public AdConfig() {
        _init();
    }

    private void _init() {
        mListRequestScene = new ArrayList<>();
        mListShowScene = new ArrayList<>();
        mListChannel = new ArrayList<>();
        mMapAdvertisementConfig = new HashMap<>();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object || getClass() != object.getClass())
            return false;

        if (TextUtils.isEmpty(mKey))
            return false;

        if (this == object)
            return true;

        AdConfig adConfig = (AdConfig) object;
        return mKey.equals(adConfig.getAdKey());
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(mKey))
            return 0;

        return mKey.hashCode();
    }

    @Override
    public JSONObject Serialization() {
        return null;
    }

    @Override
    public void Deserialization(JSONObject jsonObject) {
        if (null == jsonObject)
            return;

        mAdType = UtilsJson.JsonUnserialization(jsonObject, "type", VALUE_STRING_UNKNOWN_AD_TYPE);
        mCacheCount = UtilsJson.JsonUnserialization(jsonObject, "cache_count", mCacheCount);

        mListRequestScene = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "request_scene", mListRequestScene, String.class, null, null);

        mListShowScene = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "show_scene", mListShowScene, String.class, null, null);

        mListChannel = new ArrayList<>();
        UtilsJson.JsonUnserialization(jsonObject, "channel", mListChannel, String.class, null, null);

        mMapAdvertisementConfig = new HashMap<>();
        for (String strChannel : mListChannel) {
            if (TextUtils.isEmpty(strChannel))
                continue;

            IAdvertisementConfig iAdvertisementConfig = null;
            if (VALUE_STRING_FACEBOOK_AD_CHANNEL.equals(strChannel)) {
                iAdvertisementConfig = UtilsJson.JsonUnserialization(jsonObject, "facebook", IAdvertisementConfig.class, FacebookAdConfig.class);
            } else if (VALUE_STRING_ADMOB_AD_CHANNEL.equals(strChannel)) {
                iAdvertisementConfig = UtilsJson.JsonUnserialization(jsonObject, "admob", IAdvertisementConfig.class, AdMobConfig.class);
            }

            if (null == iAdvertisementConfig)
                continue;

            mMapAdvertisementConfig.put(strChannel, iAdvertisementConfig);
        }
    }

    @Override
    public String getAdKey() {
        return mKey;
    }

    @Override
    public String getAdType() {
        return mAdType;
    }

    @Override
    public int getCacheCount() {
        return mCacheCount;
    }

    @Override
    public long getInterstitialAdCloseTime(String strChannel) {
        IAdvertisementConfig iAdvertisementConfig = getAdvertisementConfig(strChannel);
        if (iAdvertisementConfig == null) {
            return 0;
        }
        return iAdvertisementConfig.getInterstitialAdCloseTime();
    }


    @Override
    public boolean isNeedMask(String strChannel) {
        IAdvertisementConfig iAdvertisementConfig = getAdvertisementConfig(strChannel);
        if (iAdvertisementConfig == null) {
            return false;
        }
        return Math.random() < iAdvertisementConfig.getMaskRate();
    }

    private IAdvertisementConfig getAdvertisementConfig(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel)) {
            return null;
        }
        return mMapAdvertisementConfig.get(strChannel);
    }

    @Override
    public boolean isSupportRequestScene(String strRequestScene) {
        return !mListRequestScene.isEmpty() && !TextUtils.isEmpty(strRequestScene) && mListRequestScene.contains(strRequestScene);
    }

    @Override
    public boolean isSupportShowScene(String strShowScene) {
        return !mListShowScene.isEmpty() && !TextUtils.isEmpty(strShowScene) && mListShowScene.contains(strShowScene);
    }

    @Override
    public boolean isAdExist() {
        return !mListChannel.isEmpty() && !mMapAdvertisementConfig.isEmpty();
    }

    @Override
    public boolean isSupportChannel(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return false;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return false;

        return !TextUtils.isEmpty(iAdvertisementConfig.getAdID());
    }

    @Override
    public List<String> getChannelList() {
        return mListChannel;
    }

    @Override
    public String getAdID(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return null;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return null;

        if (VALUE_STRING_FACEBOOK_AD_CHANNEL.equals(strChannel)) {
            if (UtilsEnv.isDebuggable(XProfitFactory.getApplication())) {
                return UtilsFacebookAd.VALUE_STRING_CAROUSEL_IMG_SQUARE_LINK_TEST_TYPE + "#" + iAdvertisementConfig.getAdID();
            } else {
                return iAdvertisementConfig.getAdID();
            }
        } else if (VALUE_STRING_ADMOB_AD_CHANNEL.equals(strChannel)) {
            if (UtilsEnv.isDebuggable(XProfitFactory.getApplication())) {
                if (mAdType.equals(VALUE_STRING_NATIVE_AD_TYPE)) {
                    return UtilsAdMob.VALUE_STRING_ADMOB_NATIVE_ADVANCED_TEST_ID;
                } else if (mAdType.equals(VALUE_STRING_INTERSTITIAL_AD_TYPE)) {
                    return UtilsAdMob.VALUE_STRING_ADMOB_INTERSTITIAL_TEST_ID;
                } else if (mAdType.equals(VALUE_STRING_BANNER_AD_TYPE)) {
                    return UtilsAdMob.VALUE_STRING_ADMOB_BANNER_TEST_ID;
                }
            } else {
                return iAdvertisementConfig.getAdID();
            }
        }

        return null;
    }

    @Override
    public int getRetryCount(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return 0;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return 0;

        return iAdvertisementConfig.getRetryCount();
    }

    @Override
    public long getRetryDelayTime(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return 0;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return 0;

        return iAdvertisementConfig.getRetryDelayTime();
    }

    @Override
    public int getCurrentRetryCount(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return 0;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return 0;

        return iAdvertisementConfig.getCurrentRetryCount();
    }

    @Override
    public void setCurrentRetryCount(String strChannel, int nCurrentRetryCount) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return;

        iAdvertisementConfig.setCurrentRetryCount(nCurrentRetryCount);
    }

    @Override
    public List<Integer> getRetryCodeList(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return null;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return null;

        return iAdvertisementConfig.getRetryCodeList();
    }

    @Override
    public Object getBannerAdSize(String strChannel) {
        if (TextUtils.isEmpty(strChannel) || !mListChannel.contains(strChannel) || !mMapAdvertisementConfig.containsKey(strChannel))
            return null;

        IAdvertisementConfig iAdvertisementConfig = mMapAdvertisementConfig.get(strChannel);
        if (null == iAdvertisementConfig)
            return null;

        return iAdvertisementConfig.getBannerAdSize();
    }

    public void setAdKey(String strKey) {
        mKey = strKey;
    }

}
