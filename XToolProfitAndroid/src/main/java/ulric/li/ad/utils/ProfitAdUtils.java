package ulric.li.ad.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.view.AdParentLayout;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xprofit.R;

/**
 * Created by WangYu on 2018/9/6.
 */
public class ProfitAdUtils {
    public static IAdConfig mCurrentInterstitialAdConfig = null;

    public static boolean requestAd(IAdConfig iAdConfig) {
        IAdMgr iAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);

        int nAdRequestAndLoadedCount = 0;
        if (IAdConfig.VALUE_STRING_NATIVE_AD_TYPE.equals(iAdConfig.getAdType())) {
            nAdRequestAndLoadedCount = iAdMgr.getNativeAdRequestAndLoadedCount(iAdConfig);
        } else if (IAdConfig.VALUE_STRING_INTERSTITIAL_AD_TYPE.equals(iAdConfig.getAdType())) {
            nAdRequestAndLoadedCount = iAdMgr.getInterstitialAdRequestAndLoadedCount(iAdConfig);
        } else if (IAdConfig.VALUE_STRING_BANNER_AD_TYPE.equals(iAdConfig.getAdType())) {
            nAdRequestAndLoadedCount = iAdMgr.getBannerAdRequestAndLoadedCount(iAdConfig);
        } else {
            return false;
        }

        if (nAdRequestAndLoadedCount < iAdConfig.getCacheCount()) {
            for (int nIndex = nAdRequestAndLoadedCount; nIndex < iAdConfig.getCacheCount(); nIndex++) {
                if (IAdConfig.VALUE_STRING_NATIVE_AD_TYPE.equals(iAdConfig.getAdType())) {
                    if (iAdMgr.requestNativeAdAsync(iAdConfig)) {
                        nAdRequestAndLoadedCount++;
                    }
                } else if (IAdConfig.VALUE_STRING_INTERSTITIAL_AD_TYPE.equals(iAdConfig.getAdType())) {
                    if (iAdMgr.requestInterstitialAdAsync(iAdConfig)) {
                        nAdRequestAndLoadedCount++;
                    }
                } else if (IAdConfig.VALUE_STRING_BANNER_AD_TYPE.equals(iAdConfig.getAdType())) {
                    if (iAdMgr.requestBannerAdAsync(iAdConfig)) {
                        nAdRequestAndLoadedCount++;
                    }
                }
            }
        }

        return nAdRequestAndLoadedCount > 0;
    }

    public static View getNativeAdView(Object objAd) {
        if (objAd instanceof NativeAd) {
            return ProfitAdUtils.getDefaultFacebookNativeAdView((NativeAd) objAd);
        } else if (objAd instanceof UnifiedNativeAd) {
            return ProfitAdUtils.getDefaultAdmobNativeAdView((UnifiedNativeAd) objAd);
        }
//        else if (objAd instanceof com.mopub.nativeads.NativeAd) {
//            return ProfitAdUtils.getDefaultMoPubNativeAdView((com.mopub.nativeads.NativeAd) objAd);
//        }
        return null;
    }

    public static void showAdView(final ViewGroup container, final IAdConfig iAdConfig, final View adView, int bottomMargin, boolean withAnimation) {
        if (adView != null) {
            ViewGroup parent = (ViewGroup) adView.getParent();
            if (parent != null) {
                parent.removeView(adView);
            }
            final AdParentLayout frameLayout = new AdParentLayout(XProfitFactory.getApplication());
            final FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            frameParams.bottomMargin = bottomMargin;
            adView.setLayoutParams(frameParams);
            frameLayout.addView(adView);
            String channel = getViewAdChannel(adView);
            boolean isMask = iAdConfig.isNeedMask(channel);
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "mask", isMask);
            UtilsJson.JsonSerialization(jsonObject, "id", iAdConfig.getAdID(channel));
            UtilsJson.JsonSerialization(jsonObject, "key", iAdConfig.getAdKey());
            UtilsLog.statisticsLog("ad", "mask", jsonObject);
            frameLayout.setInterceptTouchEvent(isMask);
            container.removeAllViews();
            container.addView(frameLayout);
            UtilsLog.logI("ad", "ad_show");
            if (withAnimation) {
                container.post(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator adAnimation = ObjectAnimator.ofFloat(container, "translationX", container.getWidth(), 0);
                        adAnimation.setInterpolator(new DecelerateInterpolator());
                        adAnimation.setDuration(300);
                        adAnimation.start();
                    }
                });
            }
        } else {
            UtilsLog.logI("ad", "ad_view_is_null");
        }
    }

    public static boolean showInterstitialAd(IAdConfig iAdConfig) {
        if (null == iAdConfig || !isInterstitialAdLoaded(iAdConfig))
            return false;

        IAdMgr iAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        if (iAdMgr.isInterstitialAdLoaded(iAdConfig)) {
            ProfitAdUtils.mCurrentInterstitialAdConfig = iAdConfig;
            iAdMgr.showInterstitialAd(iAdConfig);
            return true;
        }

        return false;
    }

    public static String getViewAdChannel(View view) {
        if (view == null) {
            return IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL;
        }
        String channel = IAdConfig.VALUE_STRING_FACEBOOK_AD_CHANNEL;//默认是原生类型，因为他没法判断
        if (view instanceof com.facebook.ads.AdView) {
            channel = IAdConfig.VALUE_STRING_FACEBOOK_AD_CHANNEL;
        } else if (view instanceof AdView) {
            channel = IAdConfig.VALUE_STRING_ADMOB_AD_CHANNEL;
        }
        return channel;
    }

    public static String getInterstitialAdChannel(Activity activity) {
        String channel = IAdConfig.VALUE_STRING_UNKNOWN_AD_CHANNEL;
        if (activity instanceof com.facebook.ads.AudienceNetworkActivity) {
            channel = IAdConfig.VALUE_STRING_FACEBOOK_AD_CHANNEL;
        } else if (activity instanceof com.google.android.gms.ads.AdActivity) {
            channel = IAdConfig.VALUE_STRING_ADMOB_AD_CHANNEL;
        }
        return channel;
    }

    public static boolean isInterstitialAdLoaded(IAdConfig iAdConfig) {
        if (null == iAdConfig)
            return false;

        IAdMgr iAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        return iAdMgr.isInterstitialAdLoaded(iAdConfig);
    }


    public static void releaseAdMap(Map<IAdConfig, Object> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<IAdConfig, Object> entry : map.entrySet()) {
            Object ad = entry.getValue();
            destroyAd(ad);
        }
        map.clear();
    }

    public static void destroyAd(Object ad) {
        if (ad == null) {
            return;
        }
        if (ad instanceof NativeAd) {
            ((NativeAd) ad).destroy();
        } else if (ad instanceof com.facebook.ads.AdView) {
            ((com.facebook.ads.AdView) ad).destroy();
        } else if (ad instanceof com.google.android.gms.ads.AdView) {
            ((com.google.android.gms.ads.AdView) ad).destroy();
        } else if (ad instanceof UnifiedNativeAd) {
            ((UnifiedNativeAd) ad).destroy();
        }
    }

    public static View getDefaultAdmobNativeAdView(UnifiedNativeAd nativeAd) {
        UnifiedNativeAdView adView = (UnifiedNativeAdView) View.inflate(XProfitFactory.getApplication(), R.layout.layout_admob_native_ad, null);
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

//        if (nativeAd.getPrice() == null) {
//            adView.getPriceView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getPriceView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//        }

//        if (nativeAd.getStore() == null) {
//            adView.getStoreView().setVisibility(View.INVISIBLE);
//        } else {
//            adView.getStoreView().setVisibility(View.VISIBLE);
//            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//        }
//
//        if (nativeAd.getStarRating() == null) {
//            adView.getStarRatingView().setVisibility(View.INVISIBLE);
//        } else {
//            ((RatingBar) adView.getStarRatingView())
//                    .setRating(nativeAd.getStarRating().floatValue());
//            adView.getStarRatingView().setVisibility(View.VISIBLE);
//        }
//
//        if (nativeAd.getAdvertiser() == null) {
//            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
//        } else {
//            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//            adView.getAdvertiserView().setVisibility(View.VISIBLE);
//        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
        return adView;
    }

    public static View getDefaultFacebookNativeAdView(NativeAd nativeAd) {
        if (null == nativeAd) {
            return null;
        }

//        View render = NativeAdView.render(XProfitFactory.getApplication(), nativeAd, NativeAdView.Type.HEIGHT_300);
//        return render;
        nativeAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(XProfitFactory.getApplication());
        View vAd = inflater.inflate(R.layout.layout_facebook_native_ad, null);

        LinearLayout adChoicesContainer = vAd.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(XProfitFactory.getApplication(), nativeAd, true);
        adChoicesContainer.addView(adChoicesView);

        AdIconView nativeAdIcon = vAd.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = vAd.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = vAd.findViewById(R.id.native_ad_media);
        TextView nativeAdBody = vAd.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = vAd.findViewById(R.id.native_ad_call_to_action);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

        nativeAd.registerViewForInteraction(
                vAd,
                nativeAdMedia,
                nativeAdIcon,
                Arrays.asList(nativeAdMedia, nativeAdIcon, nativeAdTitle, nativeAdCallToAction));

        return vAd;
    }
}
