package ulric.li.xout.core.communication;

import ulric.li.xlib.intf.IXManager;

/**
 * Created by WangYu on 2018/9/5.
 */
public interface IOutComponent extends IXManager {
    String BROADCAST_ACTION = "com.action.initOutComponent";

    String getConfigUrl();

    String getNativeAdKey(String strScene);

    String getBannerAdKey(String strScene);

    String getInterstitialAdKey(String strScene);

    String getSceneName(String adKey);

    Class<?> getOutPageClass(String strScene);
}
