package ulric.li.ad.intf;

import java.io.Serializable;
import java.util.List;

import ulric.li.mode.intf.IXJsonSerialization;
import ulric.li.xlib.intf.IXObject;

public interface IAdConfig extends IXObject, IXJsonSerialization, Serializable {
    String getAdKey();

    String getAdType();

    int getCacheCount();

    long getInterstitialAdCloseTime(String strChannel);

    boolean isNeedMask(String strChannel);

    boolean isSupportRequestScene(String strRequestScene);

    boolean isSupportShowScene(String strShowScene);

    boolean isAdExist();

    boolean isSupportChannel(String strChannel);

    List<String> getChannelList();

    String getAdID(String strChannel);

    int getRetryCount(String strChannel);

    long getRetryDelayTime(String strChannel);

    int getCurrentRetryCount(String strChannel);

    void setCurrentRetryCount(String strChannel, int nCurrentRetryCount);

    List<Integer> getRetryCodeList(String strChannel);

    Object getBannerAdSize(String strChannel);

    String VALUE_STRING_UNKNOWN_AD_CHANNEL = "unknown";
    String VALUE_STRING_FACEBOOK_AD_CHANNEL = "facebook";
    String VALUE_STRING_ADMOB_AD_CHANNEL = "admob";

    String VALUE_STRING_UNKNOWN_AD_TYPE = "unknown";
    String VALUE_STRING_NATIVE_AD_TYPE = "native";
    String VALUE_STRING_INTERSTITIAL_AD_TYPE = "interstitial";
    String VALUE_STRING_BANNER_AD_TYPE = "banner";
}
