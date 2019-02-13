package ulric.li.ad.intf;

import android.view.View;

import ulric.li.xlib.intf.IXManager;

public interface IAdvertisementMgr extends IXManager {
    boolean isInterstitialAdLoaded(String strAdUnitID);

    int getInterstitialAdLoadedCount(String strAdUnitID);

    boolean requestInterstitialAdAsync(String strAdUnitID, IAdvertisementListener iAdvertisementListener);

    void showInterstitialAd(String strAdUnitID);

    boolean isNativeAdLoaded(String strAdUnitID);

    int getNativeAdLoadedCount(String strAdUnitID);

    boolean requestNativeAdAsync(String strAdUnitID, IAdvertisementListener iAdvertisementListener);

    Object getNativeAdInfo(String strAdUnitID);

    boolean isBannerAdLoaded(String strAdUnitID);

    int getBannerAdLoadedCount(String strAdUnitID);

    boolean requestBannerAdAsync(String strAdUnitID, Object objectAdSize, IAdvertisementListener iAdvertisementListener);

    View getBannerAdView(String strAdUnitID);
}
