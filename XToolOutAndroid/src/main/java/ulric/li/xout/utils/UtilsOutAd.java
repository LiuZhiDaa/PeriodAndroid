package ulric.li.xout.utils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.utils.ProfitAdUtils;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.config.intf.IOutSceneConfig;

public class UtilsOutAd {

    public static boolean isCanRequestAd(IAdConfig iAdConfig, String strScene, String outScene) {
        if (null == iAdConfig)
            return false;

        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (null == iOutConfig || !iOutConfig.isOutAdEnable() || !iOutConfig.isOutSceneAdEnable(outScene))
            return false;

        if (!TextUtils.isEmpty(strScene) && !iAdConfig.isSupportRequestScene(strScene))
            return false;

        return true;
    }

    public static boolean requestAd(IAdConfig iAdConfig, String strScene, String outScene) {
        if (null == iAdConfig || !isCanRequestAd(iAdConfig, strScene, outScene))
            return false;

        return ProfitAdUtils.requestAd(iAdConfig);
    }

    public static boolean isCanShowAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig) {
            UtilsLog.logI("out", "ad_config_is_null");
            return false;
        }

        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (!iOutConfig.isOutAdEnable()) {
            UtilsLog.logI("out", "ad_enable_false");
            return false;
        }

        if (!TextUtils.isEmpty(strScene) && !iAdConfig.isSupportShowScene(strScene)) {
            UtilsLog.logI("out", "no_show_scene");
            return false;
        }

        return true;
    }

    public static boolean showInterstitialAd(IAdConfig iAdConfig, String strScene) {
        if (null == iAdConfig || !isCanShowAd(iAdConfig, strScene))
            return false;

        return ProfitAdUtils.showInterstitialAd(iAdConfig);
    }

    /**
     * 展示原生或者banner广告
     *
     * @param container
     * @param iAdConfig
     * @param iOutSceneConfig
     * @param withAnimation
     * @return
     */
    public static Object showAdView(ViewGroup container, final IAdConfig iAdConfig, IOutSceneConfig iOutSceneConfig, boolean withAnimation) {
        if (!UtilsOutAd.isCanShowAd(iAdConfig, null)) {
            UtilsLog.logI("out", "view_ad_switch_of");
            return null;
        }

        if (null == container) {
            UtilsLog.logI("out", "container_is_null");
            return null;
        }

        IAdMgr iAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        Object objAd = null;
        View adView = null;
        int bottom = 0;
        if (iAdConfig.getAdType().equals(IAdConfig.VALUE_STRING_NATIVE_AD_TYPE)) {
            objAd = iAdMgr.getNativeAdInfo(iAdConfig);
            adView = ProfitAdUtils.getNativeAdView(objAd);
        } else if (iAdConfig.getAdType().equals(IAdConfig.VALUE_STRING_BANNER_AD_TYPE)) {
            adView = iAdMgr.getBannerAdView(iAdConfig);
            objAd = adView;
        }
        ProfitAdUtils.showAdView(container,iAdConfig,adView,bottom,withAnimation);
        return objAd;
    }

    public static void releaseAdMap(Map<IAdConfig, Object> map) {
        ProfitAdUtils.releaseAdMap(map);
    }

}
