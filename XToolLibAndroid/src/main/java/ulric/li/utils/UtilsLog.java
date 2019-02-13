package ulric.li.utils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.ILogTool;

public class UtilsLog {
    private static final String VALUE_STRING_STATISTICS_KEY1_ALIVE = "alive";
    private static final String VALUE_STRING_STATISTICS_KEY1_START = "start";
    private static final String VALUE_STRING_STATISTICS_KEY1_ACTION = "action";

    private static boolean sIsNeedSendLog = false;
    private static boolean sIsNeedLocalLog = false;
    private static String sStatisticsLogURL = null;
    private static String sCrashLogURL = null;
    private static String sStatisticsLogPath = "statistics.dat";
    private static String sCrashLogPath = "crash.dat";
    private static String sSeparator = "---***---";
    private static JSONObject sJsonObjectUserInfo = null;

    public static void init(boolean bIsNeedSendLog, boolean bIsNeedLocalLog,
                            String strStatisticsLogURL, String strCrashLogURL,
                            String strStatisticsLogPath, String strCrashLogPath, String strSeparator) {
        sIsNeedSendLog = bIsNeedSendLog;
        sIsNeedLocalLog = bIsNeedLocalLog;
        sStatisticsLogURL = strStatisticsLogURL;
        sCrashLogURL = strCrashLogURL;

        if (!TextUtils.isEmpty(strStatisticsLogPath)) {
            sStatisticsLogPath = strStatisticsLogPath;
        }

        if (!TextUtils.isEmpty(strCrashLogPath)) {
            sCrashLogPath = strCrashLogPath;
        }

        if (!TextUtils.isEmpty(strSeparator)) {
            sSeparator = strSeparator;
        }
    }

    public static String getStatisticsLogURL() {
        return sStatisticsLogURL;
    }

    public static String getCrashLogURL() {
        return sCrashLogURL;
    }

    public static String getStatisticsLogPath() {
        return sStatisticsLogPath;
    }

    public static String getCrashLogPath() {
        return sCrashLogPath;
    }

    public static String getSeparator() {
        return sSeparator;
    }

    public static void setUserInfo(JSONObject jsonObjectUserInfo) {
        sJsonObjectUserInfo = jsonObjectUserInfo;
    }

    public static <T extends Object> void addUserInfo(String strKey, T tValue) {
        if (TextUtils.isEmpty(strKey) || null == tValue)
            return;

        if (null == sJsonObjectUserInfo) {
            sJsonObjectUserInfo = new JSONObject();
        }

        UtilsJson.JsonSerialization(sJsonObjectUserInfo, strKey, tValue);
    }

    public static void aliveLog(String strKey2, JSONObject jsonObjectContent) {
        statisticsLog(VALUE_STRING_STATISTICS_KEY1_ALIVE, strKey2, jsonObjectContent);
    }

    public static void startLog(String strKey2, JSONObject jsonObjectContent) {
        statisticsLog(VALUE_STRING_STATISTICS_KEY1_START, strKey2, jsonObjectContent);
    }

    public static void actionLog(String strKey2, JSONObject jsonObjectContent) {
        statisticsLog(VALUE_STRING_STATISTICS_KEY1_ACTION, strKey2, jsonObjectContent);
    }

    public static void statisticsLog(String strKey1, String strKey2, JSONObject jsonObjectContent) {
        if (sIsNeedSendLog) {
            ILogTool iLogTool = (ILogTool) XLibFactory.getInstance().createInstance(ILogTool.class);
            iLogTool.statisticsLog(strKey1, strKey2, jsonObjectContent, sJsonObjectUserInfo);
        }

        logD("UtilsLog", "[statistics]\n" + "key1:" + strKey1 + "\n" +
                "key2:" + (TextUtils.isEmpty(strKey2) ? "null" : strKey2) + "\n" +
                "content:" + (null == jsonObjectContent ? "null" : jsonObjectContent.toString()) + "\n" +
                "user:" + (null == sJsonObjectUserInfo ? "null" : sJsonObjectUserInfo.toString()));
    }

    public static void crashLog(Throwable throwable) {
        crashLog("",throwable);
    }

    public static void crashLog(String tag, Throwable throwable) {
        if (sIsNeedSendLog) {
            ILogTool iLogTool = (ILogTool) XLibFactory.getInstance().createInstance(ILogTool.class);
            iLogTool.crashLog(throwable,tag);
        }

        logD("UtilsLog", "[crash]\n" + "content:" + throwable);
    }

    public static void sendLog() {
        ILogTool iLogTool = (ILogTool) XLibFactory.getInstance().createInstance(ILogTool.class);
        iLogTool.sendLog();

        logD("UtilsLog", "send log");
    }

    public static void logD(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.d(strTag, strLog);
    }

    public static void logI(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.i(strTag, strLog);
    }

    public static void logE(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.e(strTag, strLog);
    }

    public static void logV(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.v(strTag, strLog);
    }

    public static void logW(String strTag, String strLog) {
        if (!sIsNeedLocalLog)
            return;

        Log.w(strTag, strLog);
    }
}
