package ulric.li.ad.impl;

import android.text.TextUtils;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdvertisementListener;
import ulric.li.ad.intf.IAdvertisementMgr;
import ulric.li.ad.intf.IAdMgrListener;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.utils.UtilsJson;
import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class AdMgr extends XObserverAutoRemove<IAdMgrListener> implements IAdMgr {
    private Map<String, IAdvertisementMgr> mapAdMgr = null;
    private Map<IAdConfig, Integer> mMapAdConfigRequestNoLoadedCount = null;
    private Map<String, IAdConfig> mMapAdConfig = null;

    public AdMgr() {
        _init();
    }

    private void _init() {
        mapAdMgr = new HashMap<>();
        mapAdMgr.put(IAdConfig.VALUE_STRING_FACEBOOK_AD_CHANNEL, (IAdvertisementMgr) XProfitFactory.getInstance().createInstance(IAdvertisementMgr.class, FacebookAdvertisementMgr.class));
        mapAdMgr.put(IAdConfig.VALUE_STRING_ADMOB_AD_CHANNEL, (IAdvertisementMgr) XProfitFactory.getInstance().createInstance(IAdvertisementMgr.class, AdMobAdvertisementMgr.class));
        mMapAdConfigRequestNoLoadedCount = new HashMap<>();
        mMapAdConfig = new HashMap<>();
    }

    @Override
    public boolean isNeedLoadConfig() {
        return mMapAdConfig.isEmpty();
    }

    @Override
    public boolean loadConfig(JSONObject jsonObject) {
        if (null == jsonObject)
            return false;

        mMapAdConfig = new HashMap<>();
        Iterator iteratorKeys = jsonObject.keys();
        while (iteratorKeys.hasNext()) {
            String strKey = (String) iteratorKeys.next();
            if (TextUtils.isEmpty(strKey))
                continue;

            AdConfig adConfig = UtilsJson.JsonUnserialization(jsonObject, strKey, IAdConfig.class, AdConfig.class);
            if (null != adConfig) {
                adConfig.setAdKey(strKey);
                mMapAdConfig.put(strKey, adConfig);
            }
        }

        return true;
    }

    @Override
    public IAdConfig getAdConfig(String strKey) {
        if (TextUtils.isEmpty(strKey))
            return null;

        return mMapAdConfig.get(strKey);
    }

    @Override
    public int getInterstitialAdRequestAndLoadedCount(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return 0;

        int nAdConfigRequestNoLoadedCount = 0;
        if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
            nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
        }

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            nAdConfigRequestNoLoadedCount += iAdvertisementMgr.getInterstitialAdLoadedCount(iAdConfig.getAdID(strChannel));
        }

        return nAdConfigRequestNoLoadedCount;
    }

    @Override
    public boolean isInterstitialAdLoaded(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isInterstitialAdLoaded(iAdConfig.getAdID(strChannel)))
                return true;
        }

        return false;
    }

    @Override
    public boolean requestInterstitialAdAsync(final IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        IAdvertisementMgr iAdvertisementMgr = null;
        final String[] channel = new String[]{IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL};
        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            if (iAdConfig.isSupportChannel(strChannel) && iAdConfig.getRetryCount(strChannel) >= 1
                    && iAdConfig.getCurrentRetryCount(strChannel) < iAdConfig.getRetryCount(strChannel)) {
                iAdvertisementMgr = mapAdMgr.get(strChannel);
                channel[0] = strChannel;
                break;
            }
        }

        if (null != iAdvertisementMgr && IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL != channel[0]) {
            int nCurrentRetryCount = 0;
            for (String strChannel : iAdConfig.getChannelList()) {
                if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                    continue;

                nCurrentRetryCount += iAdConfig.getCurrentRetryCount(strChannel);
            }

            if (0 == nCurrentRetryCount) {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, ++nAdConfigRequestNoLoadedCount);
                } else {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, 1);
                }
            }
        }

        if (null == iAdvertisementMgr || IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
            if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                if (nAdConfigRequestNoLoadedCount > 0) {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                }
            }

            resetAdConfig(iAdConfig);
            return false;
        }

        return iAdvertisementMgr.requestInterstitialAdAsync(iAdConfig.getAdID(channel[0]), new IAdvertisementListener() {
            @Override
            public void onAdLoaded() {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    if (nAdConfigRequestNoLoadedCount > 0) {
                        mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                    }
                }

                resetAdConfig(iAdConfig);
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLoaded(iAdConfig);
                }
            }

            @Override
            public void onAdFailed(final int var1) {
                if (IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
                    for (IAdMgrListener listener : getListenerList()) {
                        listener.onAdFailed(iAdConfig, var1);
                    }

                    return;
                }

                List<Integer> listRetryCode = iAdConfig.getRetryCodeList(channel[0]);
                iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getCurrentRetryCount(channel[0]) + 1);
                if (iAdConfig.getCurrentRetryCount(channel[0]) < iAdConfig.getRetryCount(channel[0])) {
                    if (null != listRetryCode && listRetryCode.contains(var1)) {
                        if (iAdConfig.getRetryDelayTime(channel[0]) > 0) {
                            IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
                            iXTimer.start(iAdConfig.getRetryDelayTime(channel[0]), new IXTimerListener() {
                                @Override
                                public void onTimerComplete() {
                                    if (requestInterstitialAdAsync(iAdConfig))
                                        return;

                                    for (IAdMgrListener listener : getListenerList()) {
                                        listener.onAdFailed(iAdConfig, var1);
                                    }
                                }

                                @Override
                                public void onTimerRepeatComplete() {
                                }
                            });

                            return;
                        }
                    } else {
                        iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getRetryCount(channel[0]));
                    }
                }

                if (requestInterstitialAdAsync(iAdConfig))
                    return;

                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdFailed(iAdConfig, var1);
                }
            }

            @Override
            public void onAdOpened() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdOpened(iAdConfig);
                }
            }

            @Override
            public void onAdClosed() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClosed(iAdConfig);
                }
            }

            @Override
            public void onAdClicked() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClicked(iAdConfig);
                }
            }

            @Override
            public void onAdImpression() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdImpression(iAdConfig);
                }
            }

            @Override
            public void onAdLeft() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLeft(iAdConfig);
                }
            }
        });
    }

    @Override
    public void showInterstitialAd(IAdConfig iAdConfig) {
        if (null == iAdConfig || !isInterstitialAdLoaded(iAdConfig) || null == iAdConfig.getChannelList())
            return;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isInterstitialAdLoaded(iAdConfig.getAdID(strChannel))) {
                iAdvertisementMgr.showInterstitialAd(iAdConfig.getAdID(strChannel));
                break;
            }
        }
    }

    @Override
    public int getNativeAdRequestAndLoadedCount(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return 0;

        int nAdConfigRequestNoLoadedCount = 0;
        if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
            nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
        }

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            nAdConfigRequestNoLoadedCount += iAdvertisementMgr.getNativeAdLoadedCount(iAdConfig.getAdID(strChannel));
        }

        return nAdConfigRequestNoLoadedCount;
    }

    @Override
    public boolean isNativeAdLoaded(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isNativeAdLoaded(iAdConfig.getAdID(strChannel)))
                return true;
        }

        return false;
    }

    @Override
    public boolean requestNativeAdAsync(final IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        IAdvertisementMgr iAdvertisementMgr = null;
        final String[] channel = new String[]{IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL};
        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            if (iAdConfig.isSupportChannel(strChannel) && iAdConfig.getRetryCount(strChannel) >= 1
                    && iAdConfig.getCurrentRetryCount(strChannel) < iAdConfig.getRetryCount(strChannel)) {
                iAdvertisementMgr = mapAdMgr.get(strChannel);
                channel[0] = strChannel;
                break;
            }
        }

        if (null != iAdvertisementMgr && IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL != channel[0]) {
            int nCurrentRetryCount = 0;
            for (String strChannel : iAdConfig.getChannelList()) {
                if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                    continue;

                nCurrentRetryCount += iAdConfig.getCurrentRetryCount(strChannel);
            }

            if (0 == nCurrentRetryCount) {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, ++nAdConfigRequestNoLoadedCount);
                } else {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, 1);
                }
            }
        }

        if (null == iAdvertisementMgr || IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
            if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                if (nAdConfigRequestNoLoadedCount > 0) {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                }
            }

            resetAdConfig(iAdConfig);
            return false;
        }

        return iAdvertisementMgr.requestNativeAdAsync(iAdConfig.getAdID(channel[0]), new IAdvertisementListener() {
            @Override
            public void onAdLoaded() {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    if (nAdConfigRequestNoLoadedCount > 0) {
                        mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                    }
                }

                resetAdConfig(iAdConfig);
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLoaded(iAdConfig);
                }
            }

            @Override
            public void onAdFailed(final int var1) {
                if (IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
                    for (IAdMgrListener listener : getListenerList()) {
                        listener.onAdFailed(iAdConfig, var1);
                    }

                    return;
                }

                List<Integer> listRetryCode = iAdConfig.getRetryCodeList(channel[0]);
                iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getCurrentRetryCount(channel[0]) + 1);
                if (iAdConfig.getCurrentRetryCount(channel[0]) < iAdConfig.getRetryCount(channel[0])) {
                    if (null != listRetryCode && listRetryCode.contains(var1)) {
                        if (iAdConfig.getRetryDelayTime(channel[0]) > 0) {
                            IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
                            iXTimer.start(iAdConfig.getRetryDelayTime(channel[0]), new IXTimerListener() {
                                @Override
                                public void onTimerComplete() {
                                    if (requestNativeAdAsync(iAdConfig))
                                        return;

                                    for (IAdMgrListener listener : getListenerList()) {
                                        listener.onAdFailed(iAdConfig, var1);
                                    }
                                }

                                @Override
                                public void onTimerRepeatComplete() {
                                }
                            });

                            return;
                        }
                    } else {
                        iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getRetryCount(channel[0]));
                    }
                }

                if (requestNativeAdAsync(iAdConfig))
                    return;

                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdFailed(iAdConfig, var1);
                }
            }

            @Override
            public void onAdOpened() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdOpened(iAdConfig);
                }
            }

            @Override
            public void onAdClosed() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClosed(iAdConfig);
                }
            }

            @Override
            public void onAdClicked() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClicked(iAdConfig);
                }
            }

            @Override
            public void onAdImpression() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdImpression(iAdConfig);
                }
            }

            @Override
            public void onAdLeft() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLeft(iAdConfig);
                }
            }
        });
    }

    @Override
    public Object getNativeAdInfo(IAdConfig iAdConfig) {
        if (null == iAdConfig || !isNativeAdLoaded(iAdConfig) || null == iAdConfig.getChannelList())
            return null;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isNativeAdLoaded(iAdConfig.getAdID(strChannel))) {
                return iAdvertisementMgr.getNativeAdInfo(iAdConfig.getAdID(strChannel));
            }
        }

        return null;
    }

    @Override
    public int getBannerAdRequestAndLoadedCount(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return 0;

        int nAdConfigRequestNoLoadedCount = 0;
        if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
            nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
        }

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            nAdConfigRequestNoLoadedCount += iAdvertisementMgr.getBannerAdLoadedCount(iAdConfig.getAdID(strChannel));
        }

        return nAdConfigRequestNoLoadedCount;
    }

    @Override
    public boolean isBannerAdLoaded(IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isBannerAdLoaded(iAdConfig.getAdID(strChannel)))
                return true;
        }

        return false;
    }

    @Override
    public boolean requestBannerAdAsync(final IAdConfig iAdConfig) {
        if (null == iAdConfig || !iAdConfig.isAdExist() || null == iAdConfig.getChannelList())
            return false;

        IAdvertisementMgr iAdvertisementMgr = null;
        final String[] channel = new String[]{IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL};
        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            if (iAdConfig.isSupportChannel(strChannel) && iAdConfig.getRetryCount(strChannel) >= 1
                    && iAdConfig.getCurrentRetryCount(strChannel) < iAdConfig.getRetryCount(strChannel)) {
                iAdvertisementMgr = mapAdMgr.get(strChannel);
                channel[0] = strChannel;
                break;
            }
        }

        if (null != iAdvertisementMgr && IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL != channel[0]) {
            int nCurrentRetryCount = 0;
            for (String strChannel : iAdConfig.getChannelList()) {
                if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                    continue;

                nCurrentRetryCount += iAdConfig.getCurrentRetryCount(strChannel);
            }

            if (0 == nCurrentRetryCount) {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, ++nAdConfigRequestNoLoadedCount);
                } else {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, 1);
                }
            }
        }

        if (null == iAdvertisementMgr || IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
            if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                if (nAdConfigRequestNoLoadedCount > 0) {
                    mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                }
            }

            resetAdConfig(iAdConfig);
            return false;
        }

        return iAdvertisementMgr.requestBannerAdAsync(iAdConfig.getAdID(channel[0]), iAdConfig.getBannerAdSize(channel[0]), new IAdvertisementListener() {
            @Override
            public void onAdLoaded() {
                if (mMapAdConfigRequestNoLoadedCount.containsKey(iAdConfig)) {
                    int nAdConfigRequestNoLoadedCount = mMapAdConfigRequestNoLoadedCount.get(iAdConfig);
                    if (nAdConfigRequestNoLoadedCount > 0) {
                        mMapAdConfigRequestNoLoadedCount.put(iAdConfig, --nAdConfigRequestNoLoadedCount);
                    }
                }

                resetAdConfig(iAdConfig);
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLoaded(iAdConfig);
                }
            }

            @Override
            public void onAdFailed(final int var1) {
                if (IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL == channel[0]) {
                    for (IAdMgrListener listener : getListenerList()) {
                        listener.onAdFailed(iAdConfig, var1);
                    }

                    return;
                }

                List<Integer> listRetryCode = iAdConfig.getRetryCodeList(channel[0]);
                iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getCurrentRetryCount(channel[0]) + 1);
                if (iAdConfig.getCurrentRetryCount(channel[0]) < iAdConfig.getRetryCount(channel[0])) {
                    if (null != listRetryCode && listRetryCode.contains(var1)) {
                        if (iAdConfig.getRetryDelayTime(channel[0]) > 0) {
                            IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
                            iXTimer.start(iAdConfig.getRetryDelayTime(channel[0]), new IXTimerListener() {
                                @Override
                                public void onTimerComplete() {
                                    if (requestBannerAdAsync(iAdConfig))
                                        return;

                                    for (IAdMgrListener listener : getListenerList()) {
                                        listener.onAdFailed(iAdConfig, var1);
                                    }
                                }

                                @Override
                                public void onTimerRepeatComplete() {
                                }
                            });

                            return;
                        }
                    } else {
                        iAdConfig.setCurrentRetryCount(channel[0], iAdConfig.getRetryCount(channel[0]));
                    }
                }

                if (requestBannerAdAsync(iAdConfig))
                    return;

                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdFailed(iAdConfig, var1);
                }
            }

            @Override
            public void onAdOpened() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdOpened(iAdConfig);
                }
            }

            @Override
            public void onAdClosed() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClosed(iAdConfig);
                }
            }

            @Override
            public void onAdClicked() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdClicked(iAdConfig);
                }
            }

            @Override
            public void onAdImpression() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdImpression(iAdConfig);
                }
            }

            @Override
            public void onAdLeft() {
                for (IAdMgrListener listener : getListenerList()) {
                    listener.onAdLeft(iAdConfig);
                }
            }
        });
    }

    @Override
    public View getBannerAdView(IAdConfig iAdConfig) {
        if (null == iAdConfig || !isBannerAdLoaded(iAdConfig) || null == iAdConfig.getChannelList())
            return null;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            IAdvertisementMgr iAdvertisementMgr = mapAdMgr.get(strChannel);
            if (null == iAdvertisementMgr)
                continue;

            if (iAdvertisementMgr.isBannerAdLoaded(iAdConfig.getAdID(strChannel))) {
                return iAdvertisementMgr.getBannerAdView(iAdConfig.getAdID(strChannel));
            }
        }

        return null;
    }

    private void resetAdConfig(IAdConfig iAdConfig) {
        if (null == iAdConfig)
            return;

        for (String strChannel : iAdConfig.getChannelList()) {
            if (TextUtils.isEmpty(strChannel) || !mapAdMgr.containsKey(strChannel))
                continue;

            iAdConfig.setCurrentRetryCount(strChannel, 0);
        }
    }
}
