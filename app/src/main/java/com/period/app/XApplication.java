package com.period.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;


import com.period.app.bean.gen.DaoMaster;
import com.period.app.bean.gen.DaoSession;
import com.period.app.bean.gen.DatePhysiologyBeanDao;
import com.period.app.core.clock.CompatAlarmManager;
import com.period.app.core.reminder.IReminderMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.logic.alive.CrashTool;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolListener;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.utils.UtilsAlive;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;

public class XApplication extends Application {

    private static XApplication mInstance;
    private DatePhysiologyBeanDao mDatePhysiologyBeanDao;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        // 全局异常钩子
        CrashTool.init();
        // 初始化配置
        XConfig.init(this);

        UtilsAlive.init(this);
        // 逻辑初始化
        initLogic();
        initDbHelp();
        CompatAlarmManager.getInstance().registerReceiver(this);

    }

    public static XApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void initLogic() {
        initCountry();
    }

    private void initCountry() {
        String strCountry = UtilsEnv.getCountry();
        if (TextUtils.isEmpty(strCountry)) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            strCountry = sp.getString("country_code", "");
            if (TextUtils.isEmpty(strCountry)) {
                IHttpTool iHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
                iHttpTool.requestToBufferByPostAsync(UtilsNetwork.getURL(XConfig.VALUE_STRING_COUNTRY_URL), null, null, null, new IHttpToolListener() {
                    @Override
                    public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult) {
                        if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer())
                            return;

                        int nCode = UtilsNetwork.VALUE_INT_FAIL_CODE;
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(new String(iHttpToolResult.getBuffer()));
                            nCode = UtilsJson.JsonUnserialization(jsonObject, "code", nCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String strCountry = null;
                        if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode) {
                            try {
                                strCountry = jsonObject.getString("country_code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (!TextUtils.isEmpty(strCountry)) {
                            sp.edit().putString("country_code", strCountry).apply();
                            UtilsLog.addUserInfo("country_code", strCountry);
                            UtilsEnv.setCountry(strCountry);
                        }
                    }
                });

                return;
            }
        }

        UtilsLog.addUserInfo("country_code", strCountry);
        UtilsEnv.setCountry(strCountry);
    }
    /*初始化数据库相关*/
    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "period-db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        mDatePhysiologyBeanDao = daoSession.getDatePhysiologyBeanDao();
    }

    public DatePhysiologyBeanDao getDatePhysiologyBeanDao() {
        return mDatePhysiologyBeanDao;
    }
}
