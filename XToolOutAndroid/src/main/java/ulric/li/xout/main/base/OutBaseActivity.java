package ulric.li.xout.main.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.ads.AdView;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.intf.IAdMgrListener;
import ulric.li.ad.intf.IFastAdInterface;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.communication.IOutComponent;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.config.intf.IOutSceneConfig;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.utils.ScreenUtils;
import ulric.li.xout.utils.UtilsOutAd;

/**
 * Created by WangYu on 2018/6/29.
 */
public abstract class OutBaseActivity extends AppCompatActivity implements IFastAdInterface {
    protected IAdMgr mIAdMgr;
    private Map<IAdConfig, Object> mAdMap;
    private String mScene = "";
    private IOutComponent mIOutComponent;

    protected static final String VALUE_STRING_SCENE_KEY = "scene_key";

    public static void start(Context context, String sceneType, Class<?> cls) {
        if (context != null) {
            Intent intent = new Intent(context, cls);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(VALUE_STRING_SCENE_KEY, sceneType);
            context.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIOutComponent = (IOutComponent) XOutFactory.getInstance().createInstance(IOutComponent.class);
        mIAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        mIAdMgr.addListener(mAdMgrListener);
        mAdMap = new HashMap<>();
        if (savedInstanceState != null) {
            finish();
            return;
        }

        registerHomeReceiver();
        getIntentData();
        UtilsLog.statisticsLog("out", getSceneType() + "_show", null);
        setContentView(getLayoutRes());
        initView();
        showNativeAd(false);
        showBannerAd(false);

        if (ScreenUtils.isScreenOn()) {
            UtilsOutAd.showInterstitialAd(getInterstitialAdConfig(), getSceneType());
        }
    }

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract void initView();

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null)
            return;
        String sceneType = intent.getStringExtra(VALUE_STRING_SCENE_KEY);
        if (!TextUtils.isEmpty(sceneType)) {
            mScene = sceneType;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UtilsOutAd.showInterstitialAd(getInterstitialAdConfig(), getSceneType());
    }

    /**
     * 注册home键监听
     */
    private void registerHomeReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorRes
     */
    protected void setStatusBarColor(@ColorRes int colorRes) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(colorRes));
    }

    /**
     * 获取当前场景，是start activity的时候从intent里面传过来的
     *
     * @return
     */
    protected String getSceneType() {
        return mScene;
    }

    @Override
    public IAdConfig getNativeAdConfig() {
        return mIAdMgr.getAdConfig(mIOutComponent.getNativeAdKey(getSceneType()));
    }

    @Override
    public String getNativeAdRequestScene() {
        return getSceneType();
    }

    @Override
    public IAdConfig getInterstitialAdConfig() {
        return mIAdMgr.getAdConfig(mIOutComponent.getInterstitialAdKey(getSceneType()));
    }

    @Override
    public String getInterstitialAdShowScene() {
        return getSceneType();
    }

    @Override
    public String getInterstitialAdRequestScene() {
        return getSceneType();
    }

    @Override
    public ViewGroup getNativeAdLayout() {
        return null;
    }

    @Override
    public boolean showNativeAd(boolean withAnimation) {
        if (mIAdMgr.isNativeAdLoaded(getNativeAdConfig())) {
            Object nativeAd = mAdMap.get(getNativeAdConfig());
            if (nativeAd instanceof NativeAd) {
                ((NativeAd) nativeAd).destroy();
                mAdMap.put(getNativeAdConfig(), null);
            } else if (nativeAd instanceof UnifiedNativeAd) {
                ((UnifiedNativeAd) nativeAd).destroy();
                mAdMap.put(getNativeAdConfig(), null);
            }
            Object newNativeAd = UtilsOutAd.showAdView(getNativeAdLayout(), getNativeAdConfig(), getIOutSceneConfig(), withAnimation);
            mAdMap.put(getNativeAdConfig(), newNativeAd);
            return newNativeAd != null;
        } else {
            UtilsLog.statisticsLog("out", "native_ad_no_loaded", null);
            return false;
        }
    }

    @Override
    public IAdConfig getBannerAdConfig() {
        return mIAdMgr.getAdConfig(mIOutComponent.getBannerAdKey(getSceneType()));
    }

    @Override
    public ViewGroup getBannerAdLayout() {
        return null;
    }

    @Override
    public String getBannerAdRequestScene() {
        return getSceneType();
    }

    @Override
    public boolean showBannerAd(boolean withAnimation) {
        if (mIAdMgr.isBannerAdLoaded(getBannerAdConfig())) {
            Object bannerAd = mAdMap.get(getBannerAdConfig());
            if (bannerAd != null) {
                if (bannerAd instanceof AdView) {
                    ((AdView) bannerAd).destroy();
                } else if (bannerAd instanceof com.google.android.gms.ads.AdView) {
                    ((com.google.android.gms.ads.AdView) bannerAd).destroy();
                }
                mAdMap.put(getBannerAdConfig(), null);
            }
            bannerAd = UtilsOutAd.showAdView(getBannerAdLayout(), getBannerAdConfig(), getIOutSceneConfig(), withAnimation);
            mAdMap.put(getBannerAdConfig(), bannerAd);
            return bannerAd != null;
        } else {
            UtilsLog.statisticsLog("out", "banner_ad_no_loaded", null);
            return false;
        }
    }


    private IOutSceneConfig getIOutSceneConfig() {
        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        return iOutConfig.getOutSceneConfig(getSceneType());
    }

    private IAdMgrListener mAdMgrListener = new IAdMgrListener() {
        @Override
        public void onAdLoaded(IAdConfig iAdConfig) {
            super.onAdLoaded(iAdConfig);
            if (iAdConfig == null) {
                return;
            }
            if (getNativeAdConfig() != null && iAdConfig.getAdKey().equals(getNativeAdConfig().getAdKey())) {
                showNativeAd(true);
            } else if (getBannerAdConfig() != null && iAdConfig.getAdKey().equals(getBannerAdConfig().getAdKey())) {
                showBannerAd(true);
            }
        }

        @Override
        public void onAdImpression(IAdConfig iAdConfig) {
            super.onAdImpression(iAdConfig);
            if (iAdConfig == null) {
                return;
            }
        }
    };

    String SYSTEM_DIALOG_REASON_KEY = "reason";
    String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.equals(reason, SYSTEM_DIALOG_REASON_HOME_KEY)
                        || TextUtils.equals(reason, SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    printExitLog(reason);
                    UtilsOutAd.showInterstitialAd(getInterstitialAdConfig(), getSceneType());
                    finish();
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UtilsOutAd.showInterstitialAd(getInterstitialAdConfig(), getSceneType());
        if (mIAdMgr != null) {
            mIAdMgr.removeListener(mAdMgrListener);
        }

        if (IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE.equals(getSceneType())
                || IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE.equals(getSceneType())
                || IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE.equals(getSceneType())
                || IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE.equals(getSceneType())) {
            //如果是主动页面，开启的时候记录一下时间
            IOutConfig mIOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
            mIOutConfig.recordActiveSceneTriggerTime();
        }

        UtilsOutAd.releaseAdMap(mAdMap);
        UtilsLog.sendLog();
    }

    protected void printExitLog(String reason) {
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "reason", reason);
        UtilsLog.statisticsLog("out", "page_finish", jsonObject);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        printExitLog("back_press");
    }
}
