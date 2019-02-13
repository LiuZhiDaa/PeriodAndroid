package ulric.li.ad.impl;

import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdvertisementListener;
import ulric.li.ad.intf.IAdvertisementMgr;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsLog;

public class AdMobAdvertisementMgr implements IAdvertisementMgr {
    private Map<String, List<Object>> mMapAd = null;

    public AdMobAdvertisementMgr() {
        _init();
    }

    private void _init() {
        mMapAd = new HashMap<>();
    }

    @Override
    public boolean isInterstitialAdLoaded(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return false;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return false;

        return listAd.size() > 0;
    }

    @Override
    public int getInterstitialAdLoadedCount(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return 0;

        List<Object> listAd = mMapAd.get(strAdUnitID);
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

        UtilsLog.statisticsLog("ad", "admob", jsonObject);

        final InterstitialAd interstitialAd = new InterstitialAd(XProfitFactory.getApplication());
        interstitialAd.setAdUnitId(strAdUnitID);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                List<Object> listAd = mMapAd.get(strAdUnitID);
                if (null == listAd) {
                    listAd = new ArrayList<>();
                    mMapAd.put(strAdUnitID, listAd);
                }

                listAd.add(interstitialAd);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLoaded();
                }
            }

            public void onAdFailedToLoad(int var1) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "failed");
                    jsonObject.put("code", var1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdFailed(var1);
                }
            }

            public void onAdOpened() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "opened");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdOpened();
                }
            }

            public void onAdClosed() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClosed();
                }
            }

            public void onAdClicked() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "clicked");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClicked();
                }
            }

            public void onAdImpression() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "impression");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdImpression();
                }
            }

            public void onAdLeftApplication() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "interstitial");
                    jsonObject.put("action", "left");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLeft();
                }
            }
        });
        try {
            interstitialAd.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
        return true;
    }

    @Override
    public void showInterstitialAd(String strAdUnitID) {
        if (!isInterstitialAdLoaded(strAdUnitID))
            return;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        ((InterstitialAd) listAd.get(0)).show();
        listAd.remove(0);
    }

    @Override
    public boolean isNativeAdLoaded(String strAdUnitID) {
       return isBannerAdLoaded(strAdUnitID);
    }

    @Override
    public int getNativeAdLoadedCount(String strAdUnitID) {
        return getBannerAdLoadedCount(strAdUnitID);
    }

    @Override
    public boolean requestNativeAdAsync(final String strAdUnitID,final IAdvertisementListener iAdvertisementListener) {
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

        UtilsLog.statisticsLog("ad", "admob", jsonObject);

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();
        AdLoader adLoader = new AdLoader.Builder(XProfitFactory.getApplication(), strAdUnitID)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "loaded");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        List<Object> listAd = mMapAd.get(strAdUnitID);
                        if (null == listAd) {
                            listAd = new ArrayList<>();
                            mMapAd.put(strAdUnitID, listAd);
                        }

                        listAd.add(unifiedNativeAd);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdLoaded();
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "failed");
                            jsonObject.put("code", errorCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdFailed(errorCode);
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "closed");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdClosed();
                        }
                    }

                    @Override
                    public void onAdLeftApplication() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "left");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdLeft();
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "opened");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdOpened();
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdClicked() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "clicked");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdClicked();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id", strAdUnitID);
                            jsonObject.put("content_id", strContentID);
                            jsonObject.put("type", "native");
                            jsonObject.put("action", "impression");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        UtilsLog.statisticsLog("ad", "admob", jsonObject);

                        if (null != iAdvertisementListener) {
                            iAdvertisementListener.onAdImpression();
                        }
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .withNativeAdOptions(adOptions)
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
        return true;
    }

    @Override
    public Object getNativeAdInfo(String strAdUnitID) {
        if (!isBannerAdLoaded(strAdUnitID))
            return null;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        Object ad = listAd.get(0);
        listAd.remove(0);
        return ad;
    }

    @Override
    public boolean isBannerAdLoaded(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return false;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return false;

        return listAd.size() > 0;
    }

    @Override
    public int getBannerAdLoadedCount(String strAdUnitID) {
        if (TextUtils.isEmpty(strAdUnitID) || !mMapAd.containsKey(strAdUnitID))
            return 0;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        if (null == listAd)
            return 0;

        return listAd.size();
    }

    @Override
    public boolean requestBannerAdAsync(final String strAdUnitID, final Object objectAdSize, final IAdvertisementListener iAdvertisementListener) {
        if (TextUtils.isEmpty(strAdUnitID) || null == objectAdSize)
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

        UtilsLog.statisticsLog("ad", "admob", jsonObject);

        final AdView adView = new AdView(XProfitFactory.getApplication());
        adView.setAdUnitId(strAdUnitID);
        adView.setAdSize(adSize);
        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
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

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                List<Object> listAd = mMapAd.get(strAdUnitID);
                if (null == listAd) {
                    listAd = new ArrayList<>();
                    mMapAd.put(strAdUnitID, listAd);
                }

                listAd.add(adView);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLoaded();
                }
            }

            public void onAdFailedToLoad(int var1) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "failed");
                    jsonObject.put("code", var1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdFailed(var1);
                }
            }

            public void onAdOpened() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "opened");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdOpened();
                }
            }

            public void onAdClosed() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClosed();
                }
            }

            public void onAdClicked() {
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

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdClicked();
                }
            }

            public void onAdImpression() {
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

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdImpression();
                }
            }

            public void onAdLeftApplication() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", strAdUnitID);
                    jsonObject.put("content_id", strContentID);
                    jsonObject.put("type", "banner");
                    jsonObject.put("size", String.format(Locale.ENGLISH, "%s*%s", adSize.getWidth(), adSize.getHeight()));
                    jsonObject.put("action", "left");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("ad", "admob", jsonObject);

                if (null != iAdvertisementListener) {
                    iAdvertisementListener.onAdLeft();
                }
            }
        });
        try {
            adView.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
        return true;
    }

    @Override
    public View getBannerAdView(String strAdUnitID) {
        if (!isBannerAdLoaded(strAdUnitID))
            return null;

        List<Object> listAd = mMapAd.get(strAdUnitID);
        Object ad = listAd.get(0);
        listAd.remove(0);
        return (AdView) ad;
    }
}
