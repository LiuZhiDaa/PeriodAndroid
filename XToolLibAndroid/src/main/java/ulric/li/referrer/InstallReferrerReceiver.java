package ulric.li.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.facebook.applinks.AppLinkData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsLog;

public class InstallReferrerReceiver extends BroadcastReceiver {
    private static String sReferrerURL = null;

    public static void init(Context context, String strReferrerURL) {
        if (null == context)
            return;

        sReferrerURL = strReferrerURL;

        // 本地如果有referrer 写入日志缓存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String strReferrer = sp.getString("referrer", "");
        if (TextUtils.isEmpty(strReferrer)) {
            detectFacebookReferrer(context);
        } else {
            UtilsLog.addUserInfo("referrer", strReferrer);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String strReferrer = intent.getStringExtra("referrer");
        if (TextUtils.isEmpty(strReferrer))
            return;

        analyzeReferrer(context, strReferrer);
    }

    private static void detectFacebookReferrer(final Context context) {
        if (null == context)
            return;

        try {
            AppLinkData.fetchDeferredAppLinkData(context, appLinkData -> {
                if (null == appLinkData
                        || null == appLinkData.getTargetUri()
                        || TextUtils.isEmpty(appLinkData.getTargetUri().toString()))
                    return;

                String strReferrer = "utm_source=facebook&utm_medium=" + appLinkData.getTargetUri().toString();
                analyzeReferrer(context, strReferrer);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean analyzeReferrer(Context context, String strReferrer) {
        if (null == context || TextUtils.isEmpty(strReferrer))
            return false;

        // Referrer解码
        strReferrer = UtilsEncrypt.urlDecode(strReferrer);
        if (TextUtils.isEmpty(strReferrer)||strReferrer.contains("not set"))
            return false;

        // 将referrer写入本地
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("referrer", strReferrer).apply();

        // 写入日志缓存
        UtilsLog.addUserInfo("referrer", strReferrer);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("referrer", strReferrer);
            jsonObject.put("mid", UtilsEnv.getPhoneID(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 打点统计referrer
        UtilsLog.statisticsLog("alive", "referrer", jsonObject);

        // 服务器回传referrer
        if (!TextUtils.isEmpty(sReferrerURL)) {
            IHttpTool iHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
            Map<String, String> mapParam = new HashMap<>();
            mapParam.put("data", jsonObject.toString());
            iHttpTool.requestToBufferByPostAsync(sReferrerURL, mapParam, null, null, null);
        }

        return true;
    }
}
