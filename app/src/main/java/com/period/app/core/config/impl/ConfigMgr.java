package com.period.app.core.config.impl;

import android.content.Context;
import android.os.Message;


import com.period.app.XConfig;
import com.period.app.core.XCoreFactory;
import com.period.app.core.config.intf.ICloudConfig;
import com.period.app.core.config.intf.IConfigMgr;
import com.period.app.core.config.intf.IConfigMgrListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XLibFactory;

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


public class ConfigMgr extends XObserverAutoRemove<IConfigMgrListener> implements IConfigMgr {
    private Context mContext = null;
    private IXThreadPool mIXThreadPool = null;
    private IHttpTool mIHttpTool = null;



    private String VALUE_STRING_CONFIG_FILE = "config.dat";

    public ConfigMgr() {
        mContext = XCoreFactory.getApplication();
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);

    }

    @Override
    public boolean detectLocalInfoAsync() {
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {

            }

            @Override
            public void onTaskComplete() {
                for (IConfigMgrListener listener : getListenerList())
                    listener.onDetectLocalInfoAsyncComplete();
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }

    @Override
    public boolean requestConfigAsync() {
        final int[] nCode = new int[]{UtilsNetwork.VALUE_INT_FAIL_CODE};
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                JSONObject jsonObjectParam = new JSONObject();
                UtilsJson.JsonSerialization(jsonObjectParam, "country_code", UtilsEnv.getCountry());
                UtilsJson.JsonSerialization(jsonObjectParam, "time", UtilsTime.getDateStringHh(System.currentTimeMillis()));
                UtilsJson.JsonSerialization(jsonObjectParam, "app_version", String.valueOf(UtilsApp.getMyAppVersionCode(mContext)));

                Map<String, String> mapParam = new HashMap<>();
                mapParam.put("data", jsonObjectParam.toString());
                IHttpToolResult iHttpToolResult = mIHttpTool.requestToBufferByPostSync(UtilsNetwork.getURL(XConfig.VALUE_STRING_CONFIG_URL), mapParam, null);
                if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                    JSONObject jsonObjectDebug = new JSONObject();
                    try {
                        jsonObjectDebug.put("success", false);
                        jsonObjectDebug.put("exception", iHttpToolResult.getException());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    UtilsLog.statisticsLog("debug", "request_config", jsonObjectDebug);
                    return;
                }

                JSONObject jsonObject = null;
                JSONObject jsonObjectData = null;
                try {
//                    Log.i("wangyu", new String(iHttpToolResult.getBuffer()));
                    jsonObject = new JSONObject(new String(iHttpToolResult.getBuffer()));
                    nCode[0] = UtilsJson.JsonUnserialization(jsonObject, "code", nCode[0]);
                    jsonObjectData = jsonObject.getJSONObject("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode[0]) {

                    UtilsJson.saveJsonToFileWithEncrypt(mContext, VALUE_STRING_CONFIG_FILE, jsonObjectData);
                }

                JSONObject jsonObjectDebug = new JSONObject();
                try {
                    jsonObjectDebug.put("success", iHttpToolResult.isSuccess());
                    jsonObjectDebug.put("exception", iHttpToolResult.getException());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UtilsLog.statisticsLog("debug", "request_config", jsonObjectDebug);
            }

            @Override
            public void onTaskComplete() {
                for (IConfigMgrListener listener : getListenerList()) {
                    listener.onRequestConfigAsync(UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode[0]);
                }
            }

            @Override
            public void onMessage(Message msg) {
            }
        });

        return true;
    }


}
