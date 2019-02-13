package ulric.li.xout.core.scene.impl;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.utils.UtilsApp;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;
import ulric.li.utils.UtilsTime;
import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;
import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.core.communication.IOutComponent;
import ulric.li.xout.core.config.impl.OutConfig;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.core.scene.intf.IOutSceneMgrListener;
import ulric.li.xout.core.status.IAppStatusMgr;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgr;
import ulric.li.xout.receiver.OutBroadcastReceiver;
import ulric.li.xout.utils.ScreenUtils;
import ulric.li.xout.utils.Timer3;


public class OutSceneMgr extends XObserverAutoRemove<IOutSceneMgrListener> implements IOutSceneMgr {
    private IXThreadPool mIXThreadPool = null;
    private IXTimer mIXTimer = null;
    private IOutConfig mIOutConfig = null;
    private boolean mInit = false;
    //    private boolean mIsListened = false;
    private String VALUE_STRING_CONFIG_FILE = "config.dat";
    private long mCurrentLoopTime = 60000;
    private OutBroadcastReceiver mReceiver;
    private TelephonyManager mTelephonyManager;//监听电话状态取得未接来电
    private String mLastHashCode = "";

    public OutSceneMgr() {
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIXTimer = new Timer3();
        mIOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        //如果是被唤醒的情况下，直接读取存储的配置文件
        JSONObject jsonObject = UtilsJson.loadJsonFromFileWithDecrypt(XOutFactory.getApplication(), VALUE_STRING_CONFIG_FILE);
        loadConfig(jsonObject);
    }

    @Override
    public boolean initAsync() {
        if (mInit)
            return false;

        mInit = true;
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
            }

            @Override
            public void onTaskComplete() {
                for (IOutSceneMgrListener listener : getListenerList())
                    listener.onSceneMgrInitAsyncComplete();
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    @Override
    public boolean startListen() {
        UtilsLog.statisticsLog("out", "start_listen", null);
        //开启循环
        startActiveSceneLoop();
        //注册广播
        listenBroadcast();
        //监听来电
        listenCallState();
        return true;
    }

    @Override
    public void stopListen() {
        mIXTimer.stop();
        if (mReceiver != null) {
            XOutFactory.getApplication().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (phoneStateListener != null && mTelephonyManager != null) {
            mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            phoneStateListener = null;
            mTelephonyManager = null;
        }
    }

    private void listenCallState() {
        try {
            mTelephonyManager = (TelephonyManager) XOutFactory.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephonyManager != null) {
                mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }
    }

    private void listenBroadcast() {
        try {
            if (mReceiver == null) {
                mReceiver = new OutBroadcastReceiver();
            }
            IntentFilter filter = new IntentFilter();
            for (String action : OutBroadcastReceiver.getCommonFilterAction()) {
                filter.addAction(action);
            }
            XOutFactory.getApplication().registerReceiver(mReceiver, filter);
            filter = new IntentFilter();
            for (String action : OutBroadcastReceiver.getPackageFilterAction()) {
                filter.addAction(action);
            }
            filter.addDataScheme("package");
            XOutFactory.getApplication().registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }

    }

    /**
     * 来电监听
     */
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        private int mLastState = TelephonyManager.CALL_STATE_IDLE; // 最后的状态

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (mLastState == TelephonyManager.CALL_STATE_OFFHOOK
                    && state == TelephonyManager.CALL_STATE_IDLE) {
                IXTimer ixTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
                ixTimer.start(2000, new IXTimerListener() {
                    @Override
                    public void onTimerComplete() {
                        //挂断电话
                        IOutTriggerMgr mIOutTriggerMgr = (IOutTriggerMgr) XOutFactory.getInstance().createInstance(IOutTriggerMgr.class);
                        mIOutTriggerMgr.triggerPassivePage(IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE, null);
                    }

                    @Override
                    public void onTimerRepeatComplete() {

                    }
                });
            }
            UtilsLog.logI("UtilsProfitLog", state + "");
            mLastState = state;
        }
    };


    @Override
    public void startActiveSceneLoop() {
        mCurrentLoopTime = mIOutConfig.getOutSceneLoopTime();
        mIXTimer.stop();
        mIXTimer.startRepeat(0, mCurrentLoopTime, new IXTimerListener() {
            @Override
            public void onTimerComplete() {

            }

            @Override
            public void onTimerRepeatComplete() {
                do {
                    UtilsLog.logI("UtilsProfitLog", "loop:" + mCurrentLoopTime);
                    UtilsLog.statisticsLog("alive", "out", null);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(XOutFactory.getApplication());
                    sharedPreferences.edit().putLong("out_loop_time", System.currentTimeMillis()).apply();

                    // 判断潜伏期
                    if (mIOutConfig.isInLatentTime()) {
                        sendBreakLog("in_latent_time");
                        break;
                    }

                    // 判断当前应用是否在前台
                    IAppStatusMgr iAppStatusMgr = (IAppStatusMgr) XOutFactory.getInstance().createInstance(IAppStatusMgr.class);
                    if (iAppStatusMgr.isApplicationForeground()) {
                        sendBreakLog("app_foreground");
                        break;
                    }

                    // 判断屏幕是否关闭或者没有解锁
                    if (!ScreenUtils.isScreenOn() || ScreenUtils.isLocked()) {
                        sendBreakLog("screen_off");
                        break;
                    }

                    // 检测主动场景
                    final List<IOutScene> listOutScene = new ArrayList<>();
                    mIXThreadPool.addTask(new IXThreadPoolListener() {
                        @Override
                        public void onTaskRun() {
                            IOutScene iOutScene = detectActiveScene();
                            if (null == iOutScene) {
                                sendBreakLog("no_triggered_scene");
                                return;
                            }

                            listOutScene.add(iOutScene);
                        }

                        @Override
                        public void onTaskComplete() {
                            if (!listOutScene.isEmpty()) {
                                IOutTriggerMgr mIOutTriggerMgr = (IOutTriggerMgr) XOutFactory.getInstance().createInstance(IOutTriggerMgr.class);
                                mIOutTriggerMgr.triggerActivePage(listOutScene.get(0).getType());
                            }
                        }

                        @Override
                        public void onMessage(Message msg) {

                        }
                    });
                } while (false);
                //拉取配置
                requestConfigAsync();
            }
        });

    }

    /**
     * 按照配置的顺序挑选符合条件的主动场景
     *
     * @return
     */
    private IOutScene detectActiveScene() {
        IOutConfig iOutConfig = (IOutConfig) XOutFactory.getInstance().createInstance(IOutConfig.class);
        if (!iOutConfig.isOutEnable())
            return null;

        List<String> listOutScene = iOutConfig.getOutSceneList();
        if (null == listOutScene)
            return null;

        try {
            for (String strOutScene : listOutScene) {
                if (TextUtils.isEmpty(strOutScene))
                    continue;

                if (!iOutConfig.isOutSceneEnable(strOutScene))
                    continue;

                IOutScene iOutScene = getOutScene(strOutScene, true, false);
                if (null == iOutScene)
                    continue;

                if (!iOutScene.isNeedTriggered(null))
                    continue;

                return iOutScene;
            }
        } catch (Exception e) {
            UtilsLog.crashLog(e);
        }

        return null;
    }

    @Override
    public IOutScene getOutScene(String strSceneType, boolean bIsNeedActiveScene, boolean bIsNeedPassiveScene) {
        if (TextUtils.isEmpty(strSceneType))
            return null;

        if (bIsNeedActiveScene) {
            switch (strSceneType) {
                case IOutSceneMgr.VALUE_STRING_BOOST_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneBoost.class);
                case IOutSceneMgr.VALUE_STRING_CLEAN_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneClean.class);
                case IOutSceneMgr.VALUE_STRING_COOL_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneCool.class);
                case IOutSceneMgr.VALUE_STRING_NETWORK_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneNetwork.class);
                case IOutSceneMgr.VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneOptimizeBattery.class);
            }
        }

        if (bIsNeedPassiveScene) {
            switch (strSceneType) {
                case IOutSceneMgr.VALUE_STRING_CHARGE_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneCharge.class);
                case IOutSceneMgr.VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneChargeComplete.class);
                case IOutSceneMgr.VALUE_STRING_CALL_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneCall.class);
                case IOutSceneMgr.VALUE_STRING_UNINSTALL_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneUninstall.class);
                case IOutSceneMgr.VALUE_STRING_UPDATE_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneUpdate.class);
                case IOutSceneMgr.VALUE_STRING_ANTIVIRUS_SCENE_TYPE:
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneAntivirus.class);
                case IOutSceneMgr.VALUE_STRING_WIFI_SCENE_TYPE://wifi
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneWifi.class);
                case IOutSceneMgr.VALUE_STRING_LOCK_SCENE_TYPE://lock
                    return (IOutScene) XOutFactory.getInstance().createInstance(IOutScene.class, OutSceneLock.class);
            }
        }

        return null;
    }

    public boolean requestConfigAsync() {
        final IXThreadPool mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        final IHttpTool mIHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
        final int[] nCode = new int[]{UtilsNetwork.VALUE_INT_FAIL_CODE};
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                JSONObject jsonObjectParam = new JSONObject();
                UtilsJson.JsonSerialization(jsonObjectParam, "country_code", UtilsEnv.getCountry());
                UtilsJson.JsonSerialization(jsonObjectParam, "time", UtilsTime.getDateStringHh(System.currentTimeMillis()));
                UtilsJson.JsonSerialization(jsonObjectParam, "app_version", String.valueOf(UtilsApp.getMyAppVersionCode(XOutFactory.getApplication())));

                Map<String, String> mapParam = new HashMap<>();
                mapParam.put("data", jsonObjectParam.toString());
                long requestTime = System.currentTimeMillis();
                IOutComponent iOutComponent = (IOutComponent) XOutFactory.getInstance().createInstance(IOutComponent.class);
                IHttpToolResult iHttpToolResult = mIHttpTool.requestToBufferByPostSync(UtilsNetwork.getURL(iOutComponent.getConfigUrl()), mapParam, null);
                if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                    JSONObject jsonObjectDebug = new JSONObject();
                    try {
                        jsonObjectDebug.put("success", false);
                        jsonObjectDebug.put("exception", iHttpToolResult.getException());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    UtilsLog.statisticsLog("out", "request_config", jsonObjectDebug);
                    return;
                }

                JSONObject jsonObject = null;
                JSONObject jsonObjectData = null;
                String resultStr = null;
                try {
                    resultStr = new String(iHttpToolResult.getBuffer());
                    jsonObject = new JSONObject(resultStr);
                    nCode[0] = UtilsJson.JsonUnserialization(jsonObject, "code", nCode[0]);
                    jsonObjectData = jsonObject.getJSONObject("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode[0]) {
                    if (resultStr != null && !resultStr.equals(mLastHashCode)) {
                        mLastHashCode = resultStr;
                        UtilsLog.statisticsLog("out_config_result", requestTime + "", jsonObjectData);
                    }
                    loadConfig(jsonObjectData);
                    UtilsJson.saveJsonToFileWithEncrypt(XOutFactory.getApplication(), VALUE_STRING_CONFIG_FILE, jsonObjectData);
                }

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", iHttpToolResult.isSuccess());
                    jsonObjectDebug.put("exception", iHttpToolResult.getException());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("out", "request_config", jsonObjectDebug);
            }

            @Override
            public void onTaskComplete() {
                if (mCurrentLoopTime != mIOutConfig.getOutSceneLoopTime()) {
                    //配置的循环时间变化的时候重新开启循环
                    startActiveSceneLoop();
                }
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    private boolean loadConfig(JSONObject jsonObject) {
        if (null == jsonObject)
            return false;
        IAdMgr mIAdMgr = (IAdMgr) XProfitFactory.getInstance().createInstance(IAdMgr.class);
        try {

            if (jsonObject.has("ad")) {
                mIAdMgr.loadConfig(jsonObject.getJSONObject("ad"));
            }

            if (jsonObject.has("out")) {
                UtilsJson.JsonUnserialization(jsonObject, "out", IOutConfig.class, OutConfig.class);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void sendBreakLog(String reason) {
        JSONObject mJsonObject = new JSONObject();
        UtilsJson.JsonSerialization(mJsonObject, "fail", reason);
        UtilsLog.statisticsLog("out", "loop_break", mJsonObject);
    }
}
