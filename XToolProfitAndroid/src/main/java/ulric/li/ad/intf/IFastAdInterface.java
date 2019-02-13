package ulric.li.ad.intf;

import android.view.ViewGroup;

/**
 * 快速获取信息
 */
public interface IFastAdInterface {

     IAdConfig getNativeAdConfig();

     ViewGroup getNativeAdLayout();

     String getNativeAdRequestScene();

     boolean showNativeAd(boolean withAnimation);

     IAdConfig getBannerAdConfig();

     ViewGroup getBannerAdLayout();

     String getBannerAdRequestScene();

     boolean showBannerAd(boolean withAnimation);

     IAdConfig getInterstitialAdConfig();

     String getInterstitialAdShowScene();

     String getInterstitialAdRequestScene();

}
