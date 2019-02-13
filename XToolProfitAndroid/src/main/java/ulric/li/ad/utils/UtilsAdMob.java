package ulric.li.ad.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.MobileAds;

public class UtilsAdMob {
    public static final String VALUE_STRING_ADMOB_APP_TEST_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String VALUE_STRING_ADMOB_BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String VALUE_STRING_ADMOB_INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String VALUE_STRING_ADMOB_NATIVE_EXPRESS_SMALL_TEST_ID = "ca-app-pub-3940256099942544/2793859312";
    public static final String VALUE_STRING_ADMOB_NATIVE_EXPRESS_LARGE_TEST_ID = "ca-app-pub-3940256099942544/2177258514";
    public static final String VALUE_STRING_ADMOB_NATIVE_ADVANCED_TEST_ID = "ca-app-pub-3940256099942544/2247696110";
    public static final String VALUE_STRING_ADMOB_REWARDED_VIDEO_TEST_ID = "ca-app-pub-3940256099942544/5224354917";

    public static final String VALUE_STRING_BANNER_SIZE = "BANNER";
    public static final String VALUE_STRING_LARGE_BANNER_SIZE = "LARGE_BANNER";
    public static final String VALUE_STRING_MEDIUM_RECTANGLE_SIZE = "MEDIUM_RECTANGLE";
    public static final String VALUE_STRING_FULL_BANNER_SIZE = "FULL_BANNER";
    public static final String VALUE_STRING_LEADERBOARD_SIZE = "LEADERBOARD";
    public static final String VALUE_STRING_SMART_BANNER_SIZE = "SMART_BANNER";

    public static void init(Context context, String strAdMobAppID) {
        if (null == context || TextUtils.isEmpty(strAdMobAppID))
            return;

        MobileAds.initialize(context, strAdMobAppID);
    }
}
