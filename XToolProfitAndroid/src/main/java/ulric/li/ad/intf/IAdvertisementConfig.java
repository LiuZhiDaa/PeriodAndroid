package ulric.li.ad.intf;

import java.util.List;

import ulric.li.mode.intf.IXJsonSerialization;
import ulric.li.xlib.intf.IXObject;

public interface IAdvertisementConfig extends IXObject, IXJsonSerialization {
    String getAdID();

    int getRetryCount();

    long getRetryDelayTime();

    int getCurrentRetryCount();

    void setCurrentRetryCount(int nCurrentRetryCount);

    List<Integer> getRetryCodeList();

    Object getBannerAdSize();

    double getMaskRate();

    long getInterstitialAdCloseTime();

}
