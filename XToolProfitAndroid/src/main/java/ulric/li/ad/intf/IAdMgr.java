package ulric.li.ad.intf;

import android.view.View;

import org.json.JSONObject;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IAdMgr extends IXManager, IXObserver<IAdMgrListener> {
    boolean isNeedLoadConfig();

    boolean loadConfig(JSONObject jsonObject);

    IAdConfig getAdConfig(String strKey);

    int getInterstitialAdRequestAndLoadedCount(IAdConfig iAdConfig);

    boolean isInterstitialAdLoaded(IAdConfig iAdConfig);

    boolean requestInterstitialAdAsync(IAdConfig iAdConfig);

    void showInterstitialAd(IAdConfig iAdConfig);

    int getNativeAdRequestAndLoadedCount(IAdConfig iAdConfig);

    boolean isNativeAdLoaded(IAdConfig iAdConfig);

    boolean requestNativeAdAsync(IAdConfig iAdConfig);

    Object getNativeAdInfo(IAdConfig iAdConfig);

    int getBannerAdRequestAndLoadedCount(IAdConfig iAdConfig);

    boolean isBannerAdLoaded(IAdConfig iAdConfig);

    boolean requestBannerAdAsync(IAdConfig iAdConfig);

    View getBannerAdView(IAdConfig iAdConfig);
}
