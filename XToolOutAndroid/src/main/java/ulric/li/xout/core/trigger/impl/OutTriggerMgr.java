package ulric.li.xout.core.trigger.impl;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.intf.IAdMgrListener;
import ulric.li.tool.intf.IWakeLockTool;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.communication.IOutComponent;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.scene.impl.OutSceneLock;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneListener;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.core.status.IAppStatusMgr;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgr;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgrListener;
import ulric.li.xout.main.base.OutBaseActivity;
import ulric.li.xout.utils.ScreenUtils;
import ulric.li.xout.utils.UtilsOutAd;
import ulric.li.xui.utils.UtilsSize;

/**
 * 场景触发逻辑控制
 * Created by WangYu on 2018/7/16.
 */
public class OutTriggerMgr extends XObserverAutoRemove<IOutTriggerMgrListener> implements IOutTriggerMgr {
    private static long mChargingTime = 0;//记录充电开始的时间
    private final IWakeLockTool mWakeLockTool;
    private IAdMgr mIAdMgr = null;
    private IOutConfig mIOutConfig = null;
    private IAppStatusMgr mIAppStatusMgr = null;
    private JSONObject mJsonObject = null;
    private Map<String, JSONObject> mMapSceneData;
    private Map<String, Integer> mMapAdRequestCount;//用于记录每个场景发出广告请求的个数
    private IOutComponent mIOutComponent;

    private IAdMgrListener mIAdMgrListener = new IAdMgrListener() {
        @Override
        public void onAdLoaded(IAdConfig iAdConfig) {
            checkAdComplete(iAdConfig);
        }

        @Override
        public void onAdFailed(IAdConfig iAdConfig, int var1) {
            checkAdComplete(iAdConfig);
        }
    };


    public OutTriggerMgr() {
        mMapAdRequestCount = new HashMap<>();
        mMapSceneData = new HashMap<>();
        mIAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        mIAdMgr.addListener(mIAdMgrListener);
        mIOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        mIAppStatusMgr = (IAppStatusMgr) XOutFactory.getInstance().createInstance(IAppStatusMgr.class);
        mWakeLockTool = (IWakeLockTool) XLibFactory.getInstance().createInstance(IWakeLockTool.class);
        mIOutComponent = (IOutComponent) XOutFactory.getInstance().createInstance(IOutComponent.class);
    }

    /**
     * 触发主动页面
     */
    @Override
    public void triggerActivePage(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return;

        // 判断开关
        if (!isCanTriggerPage(strScene, true, null))
            return;

        // 主动页面之间的保护时间
        if (mIOutConfig.isInActiveSceneProtectTime()) {
            sendBreakLog(strScene, "fail", "active_scene_in_protect_time");
            return;
        }

//        if (isShowNotification()) {
//        Intent intent = new Intent(XProfitFactory.getApplication(), CleanerActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(XProfitFactory.getApplication());
//        taskStackBuilder.addParentStack(CleanerActivity.class);
//        taskStackBuilder.addNextIntent(intent);
//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


//        NotificationCompat.Builder notification = new NotificationCompat.Builder(XProfitFactory.getApplication())
//                .setSmallIcon(R.drawable.battery_optimize_icon)
//                .setContentTitle("cute!!!")
//                .setContentIntent(pendingIntent)
//                .setDefaults(NotificationCompat.DEFAULT_SOUND)
//                .setContentText("test!!!")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager)
//                XProfitFactory.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
//        assert notificationManager != null;
//        notificationManager.notify(10000, notification.build());

//        } else {
        // 请求广告
        requestOutSceneAd(strScene);
//        }

    }

    /**
     * 触发被动页面
     */
    @Override
    public void triggerPassivePage(String strScene, JSONObject jsonObjectData) {
        if (TextUtils.isEmpty(strScene))
            return;

        // 判断开关
        if (!isCanTriggerPage(strScene, !IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE.equals(strScene), jsonObjectData))
            return;

//        //保存传递过来的数据，可能在页面里会用到
//        if (null != jsonObjectData) {
//            mMapSceneData.put(strScene, jsonObjectData);
//        }

        switch (strScene) {
            case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE://充电
                mChargingTime = System.currentTimeMillis();
                break;
            case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://锁屏
                if (jsonObjectData != null) {
                    String condition = UtilsJson.JsonUnserialization(jsonObjectData, OutSceneLock.VALUE_STRING_STATUS_KEY, "");
                    if ("screen_off".equals(condition)
                            && mWakeLockTool.setWakeLock(PowerManager.PARTIAL_WAKE_LOCK, condition)) {
                        UtilsLog.logI("UtilsProfitLog", "wake_lock");
                        mWakeLockTool.acquire(20000);
                    }
                }
                break;
        }

        // 请求广告
        requestOutSceneAd(strScene);

//        Intent intent = new Intent(XProfitFactory.getApplication(), CleanerActivity.class);
//        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(XProfitFactory.getApplication());
//        taskStackBuilder.addParentStack(CleanerActivity.class);
//        taskStackBuilder.addNextIntent(intent);
//        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(XProfitFactory.getApplication())
//                .setSmallIcon(R.drawable.battery_optimize_icon)
//                .setContentTitle("cute!!!")
//                .setContentIntent(pendingIntent)
//                .setDefaults(NotificationCompat.DEFAULT_SOUND)
//                .setContentText("test!!!")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager)
//                XProfitFactory.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
//        assert notificationManager != null;
//        notificationManager.notify(10000, notification.build());
    }

    @Override
    public long getChargeTime() {
        if (mChargingTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - mChargingTime;
    }

    private boolean isShowNotification() {
        if (mIOutConfig.isShowNotification()) {
            return true;
        }
        return false;
    }

    /**
     * 是否允许触发
     *
     * @param strScene
     * @return
     */
    private boolean isCanTriggerPage(String strScene, boolean isCheckScreenState, JSONObject condition) {
        if (TextUtils.isEmpty(strScene))
            return false;

        // 判断总开关
        if (!mIOutConfig.isOutEnable()) {
            sendBreakLog(strScene, "fail", "out_enable_false");
            return false;
        }

        Context context = XLibFactory.getApplication();
        //增加判断条件UtilsSize.getScreenHeight(context) <= 600
        if ("google".equalsIgnoreCase(UtilsEnv.getAndroidBrand()) && (UtilsSize.getScreenWidth(context) <= 300 ||
                 UtilsSize.getScreenHeight(context) <= 600)) {
            return false;
        }

        // 判断潜伏期
        if (mIOutConfig.isInLatentTime()) {
            sendBreakLog(strScene, "fail", "latent_time");
            return false;
        }

        // 判断屏幕是否关闭或者没有解锁
        if (isCheckScreenState) {
            if (!ScreenUtils.isScreenOn() || ScreenUtils.isLocked()) {
                sendBreakLog(strScene, "fail", "screen_off");
                return false;
            }
        }

        // 判断当前应用是否在前台
        if (mIAppStatusMgr.isAppForeground()) {
            sendBreakLog(strScene, "fail", "app_foreground");
            return false;
        }

        // 判断场景开关
        if (!mIOutConfig.isOutSceneEnable(strScene)) {
            sendBreakLog(strScene, "fail", "scene_enable_false");
            return false;
        }

        // 判断场景广告开关
        if (!mIOutConfig.isOutSceneAdEnable(strScene)) {
            sendBreakLog(strScene, "fail", "scene_ad_false");
            return false;
        }
        IOutSceneMgr mIOutSceneMgr = (IOutSceneMgr) XOutFactory.getInstance().createInstance(IOutSceneMgr.class);
        IOutScene iOutScene = mIOutSceneMgr.getOutScene(strScene, true, true);
        if (null == iOutScene) {
            sendBreakLog(strScene, "fail", "no_support");
            return false;
        }

        // 判断场景触发条件
        if (!iOutScene.isNeedTriggered(condition)) {
            sendBreakLog(strScene, "fail", "no_condition");
            return false;
        }

        return true;
    }

    private void sendBreakLog(String strScene, String result, String reason) {
        mJsonObject = new JSONObject();
        UtilsJson.JsonSerialization(mJsonObject, "scene", strScene);
        UtilsJson.JsonSerialization(mJsonObject, result, reason);
        UtilsLog.statisticsLog("out", "trigger", mJsonObject);
    }

    /**
     * 请求体外场景的广告
     *
     * @param strScene
     */
    private void requestOutSceneAd(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return;
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "scene", strScene);
        UtilsJson.JsonSerialization(jsonObject, "action", "request_ad");
        UtilsLog.statisticsLog("out", "trigger", jsonObject);

        IAdConfig nativeAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getNativeAdKey(strScene));
        IAdConfig interstitialAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getInterstitialAdKey(strScene));
        IAdConfig bannerAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getBannerAdKey(strScene));
        if (mIAdMgr.isNativeAdLoaded(nativeAdConfig)
                && mIAdMgr.isInterstitialAdLoaded(interstitialAdConfig)
//                && mIAdMgr.isBannerAdLoaded(bannerAdConfig)
                ) {
            //插屏和原生都已经存在，直接开启页面
            startOutPage(strScene);
        } else {
            //否则 请求广告然后计数，都返回结果之后决定是否要开启页面
            mMapAdRequestCount.put(strScene, 0);
            if (!mIAdMgr.isNativeAdLoaded(nativeAdConfig)
                    && UtilsOutAd.requestAd(nativeAdConfig, strScene, strScene)) {
                int requestCount = mMapAdRequestCount.get(strScene);
                mMapAdRequestCount.put(strScene, ++requestCount);
            }
            if (!mIAdMgr.isInterstitialAdLoaded(interstitialAdConfig)
                    && UtilsOutAd.requestAd(interstitialAdConfig, strScene, strScene)) {
                int requestCount = mMapAdRequestCount.get(strScene);
                mMapAdRequestCount.put(strScene, ++requestCount);
            }
            if (!mIAdMgr.isBannerAdLoaded(bannerAdConfig)
                    && UtilsOutAd.requestAd(bannerAdConfig, strScene, strScene)) {
                int requestCount = mMapAdRequestCount.get(strScene);
                mMapAdRequestCount.put(strScene, ++requestCount);
            }
        }
    }

    private void checkAdComplete(IAdConfig iAdConfig) {
        if (iAdConfig == null || TextUtils.isEmpty(iAdConfig.getAdKey()))
            return;

        String scene = mIOutComponent.getSceneName(iAdConfig.getAdKey());
        if (TextUtils.isEmpty(scene)) {
            return;
        }
        int requestCount = -1;
        try {
            requestCount = mMapAdRequestCount.get(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (requestCount == -1) {
            return;
        }
        mMapAdRequestCount.put(scene, --requestCount);
        if (mMapAdRequestCount.get(scene) > 0) {
            return;
        }

        IAdConfig nativeAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getNativeAdKey(scene));
        IAdConfig interstitialAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getInterstitialAdKey(scene));
        IAdConfig bannerAdConfig = mIAdMgr.getAdConfig(mIOutComponent.getBannerAdKey(scene));
        if (!mIAdMgr.isNativeAdLoaded(nativeAdConfig)
                && !mIAdMgr.isInterstitialAdLoaded(interstitialAdConfig)
                && !mIAdMgr.isBannerAdLoaded(bannerAdConfig)) {
            sendBreakLog(scene, "fail", "all_ad_failed");
            return;
        }

        startOutPage(scene);
    }


    /**
     * 根据场景开启页面
     *
     * @param strScene
     */
    private void startOutPage(String strScene) {
        if (TextUtils.isEmpty(strScene))
            return;

        //更新触发场景的时间和次数
        IOutSceneMgr mIOutSceneMgr = (IOutSceneMgr) XOutFactory.getInstance().createInstance(IOutSceneMgr.class);
        IOutScene iOutScene = mIOutSceneMgr.getOutScene(strScene, true, true);
        if (iOutScene != null) {
            UtilsLog.logI("wangyu", "更新时间和次数：" + iOutScene.getType());
            iOutScene.updateTriggered();
            //执行清理
            iOutScene.executeAsync(new IOutSceneListener() {
                @Override
                public void onExecuteAsyncComplete(IOutScene iOutScene) {

                }
            });
        }

        sendBreakLog(strScene, "action", "open");


        Class<?> outPageClass = mIOutComponent.getOutPageClass(strScene);
        if (outPageClass != null) {
            OutBaseActivity.start(XOutFactory.getApplication(), strScene, outPageClass);
        }

    }


}
