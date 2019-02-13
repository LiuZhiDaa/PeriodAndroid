package ulric.li.ad.impl;

import android.text.TextUtils;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdvertisementListener;
import ulric.li.ad.intf.IAdvertisementMgr;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsLog;
import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class FacebookAdvertisementMgr implements IAdvertisementMgr {
    private Map<String, List<Ad>> mMapAd = null;

    private static final long VALUE_LONG_AD_DELAY_DESTROY_TIME = 5000;

    public FacebookAdvertisementMgr() {
        _init();
    }

    private void _init() {
        mMapAd = new HashMap<>();
    }

    @Override
    public boolean isInterstitialAdLoaded(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return false;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return false;

        return listAd.size() > 0;
    }

    @Override
    public int getInterstitialAdLoadedCount(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return 0;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return 0;

        return listAd.size();
    }

    @Override
    public boolean requestInterstitialAdAsync(final String strAdUnitID, final IAdvertisementListener iAdvertisementListener) {
        if (TextUtils.isEmpty(strAdUnitID))
            return false;

        final String strContentID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(XProfitFactory.getApplication()) + System.currentTimeMillis());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", strAdUnitID);
            jsonObject.put("content_id", strContentID);
            jsonObject.put("type", "interstitial");
            jsonObject.put("action", "request");
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.statisticsLog("ad", "facebook", jsonObject);

        InterstitialAd interstitialAd = new InterstitialAd(XProfitFactory.getApplication(), strAdUnitID);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                List<Ad> listAd = mMapAd.get(strAdUnitID);
                if (null == listAd) {
                    listAd = new ArrayList<>();
                    mMapAd.put(strAdUnitID, listAd);
                }

                listAd.add(ad);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLoaded();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "failed");
                    jsonObject.put("code", adError.getErrorCode());
                    jsonObject.put("message", adError.getErrorMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdFailed(adError.getErrorCode());
                }
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "opened");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdOpened();
                }
            }

            @Override
            public void onInterstitialDismissed(final Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
                iXTimer.start(VALUE_LONG_AD_DELAY_DESTROY_TIME, new IXTimerListener() {
                    @Override
                    public void onTimerComplete() {
                        ad.destroy();
                    }

                    @Override
                    public void onTimerRepeatComplete() {
                    }
                });

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClosed();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "clicked");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "impression");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdImpression();
                }
            }
        });
        try {
            interstitialAd.loadAd();
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
        return true;
    }

    @Override
    public void showInterstitialAd(String strAdUnitID) {
        if (!isInterstitialAdLoaded(strAdUnitID))
            return;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        ((InterstitialAd) listAd.get(0)).show();
        listAd.remove(0);
    }

    @Override
    public boolean isNativeAdLoaded(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return false;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return false;

        return listAd.size() > 0;
    }

    @Override
    public int getNativeAdLoadedCount(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return 0;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return 0;

        return listAd.size();
    }

    @Override
    public boolean requestNativeAdAsync(final String strAdUnitID, final IAdvertisementListener iAdvertisementListener) {
        if (TextUtils.isEmpty(strAdUnitID))
            return false;

        final String strContentID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(XProfitFactory.getApplication()) + System.currentTimeMillis());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", strAdUnitID);
            jsonObject.put("content_id", strContentID);
            jsonObject.put("type", "native");
            jsonObject.put("action", "request");
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.statisticsLog("ad", "facebook", jsonObject);

        NativeAd nativeAd = new NativeAd(XProfitFactory.getApplication(), strAdUnitID);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "native");
                    jsonObject.put("action", "media_downloaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdMediaLoaded();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "native");
                    jsonObject.put("action", "loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                List<Ad> listAd = mMapAd.get(strAdUnitID);
                if (null == listAd) {
                    listAd = new ArrayList<>();
                    mMapAd.put(strAdUnitID, listAd);
                }

                listAd.add(ad);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLoaded();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "native");
                    jsonObject.put("action", "failed");
                    jsonObject.put("code", adError.getErrorCode());
                    jsonObject.put("message", adError.getErrorMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdFailed(adError.getErrorCode());
                }
            }

            @Override
            public void onAdClicked(final Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "native");
                    jsonObject.put("action", "clicked");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

//                List<Ad> listAd = mMapAd.get(strAdUnitID);
//                if (null != listAd && listAd.contains(ad)) {
//                    listAd.remove(ad);
//                }
//
//                IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
//                iXTimer.start(VALUE_LONG_AD_DELAY_DESTROY_TIME, new IXTimerListener() {
//                    @Override
//                    public void onTimerComplete() {
//                        ad.destroy();
//                    }
//
//                    @Override
//                    public void onTimerRepeatComplete() {
//                    }
//                });

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "native");
                    jsonObject.put("action", "impression");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdImpression();
                }
            }
        });
        try {
            nativeAd.loadAd();
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
        return true;
    }

    @Override
    public Object getNativeAdInfo(String strAdUnitID) {
        if (!isNativeAdLoaded(strAdUnitID))
            return null;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        Ad ad = listAd.get(0);
        listAd.remove(0);
        return ad;
    }

    @Override
    public boolean isBannerAdLoaded(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return false;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return false;

        return listAd.size() > 0;
    }

    @Override
    public int getBannerAdLoadedCount(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return 0;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return 0;

        return listAd.size();
    }

    @Override
    public boolean requestBannerAdAsync(final String strAdUnitID, Object objectAdSize, final IAdvertisementListener iAdvertisementListener) {
        if (TextUtils.isEmpty(strAdUnitID) || null == objectAdSize || !AdSize.class.isAssignableFrom(objectAdSize.getClass()))
            return false;

        final AdSize adSize = (AdSize) objectAdSize;
        final String strContentID = UtilsEncrypt.encryptByMD5(UtilsEnv.getPhoneID(XProfitFactory.getApplication()) + System.currentTimeMillis());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", strAdUnitID);
            jsonObject.put("content_id", strContentID);
            jsonObject.put("type", "banner");
            jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
            jsonObject.put("action", "request");
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.statisticsLog("ad", "facebook", jsonObject);

        AdView adView = new AdView(XProfitFactory.getApplication(), strAdUnitID, (AdSize) objectAdSize);
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "failed");
                    jsonObject.put("code", adError.getErrorCode());
                    jsonObject.put("message", adError.getErrorMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdFailed(adError.getErrorCode());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                List<Ad> listAd = mMapAd.get(strAdUnitID);
                if (null == listAd) {
                    listAd = new ArrayList<>();
                    mMapAd.put(strAdUnitID, listAd);
                }

                listAd.add(ad);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLoaded();
                }
            }

            @Override
            public void onAdClicked(final Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "clicked");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

//                List<Ad> listAd = mMapAd.get(strAdUnitID);
//                if (null != listAd && listAd.contains(ad)) {
//                    listAd.remove(ad);
//                }
//
//                IXTimer iXTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
//                iXTimer.start(VALUE_LONG_AD_DELAY_DESTROY_TIME, new IXTimerListener() {
//                    @Override
//                    public void onTimerComplete() {
//                        ad.destroy();
//                    }
//
//                    @Override
//                    public void onTimerRepeatComplete() {
//                    }
//                });

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "impression");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "facebook", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdImpression();
                }
            }
        });
        try {
            adView.loadAd();
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
        return true;
    }

    @Override
    public View getBannerAdView(String strAdUnitID) {
        if (!isBannerAdLoaded(strAdUnitID))
            return null;

        List<Ad> listAd = mMapAd.get(strAdUnitID);
        Ad ad = listAd.get(0);
        listAd.remove(0);
        return (AdView) ad;
    }
}
