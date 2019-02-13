package ulric.li.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UtilsNetwork {
    public static final int VALUE_INT_FAIL_CODE = -1;
    public static final int VALUE_INT_SUCCESS_CODE = 1;

    public static final String VALUE_STRING_HTTP_TYPE = "http";
    public static final String VALUE_STRING_HTTPS_TYPE = "https";

    private static String sDomainName = "ulric.li";

    private static Map<String,String> mCommonPara=new HashMap<>();

    public static void init(String strDomainName) {
        sDomainName = strDomainName;
    }

    public static String getURL() {
        return VALUE_STRING_HTTP_TYPE + "://" + sDomainName;
    }

    public static String getURL(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        return getURL() + (strURL.startsWith("/") ? strURL : "/" + strURL);
    }

    public static String getSLD(String strName) {
        if (TextUtils.isEmpty(strName))
            return null;

        return VALUE_STRING_HTTP_TYPE + "://" + strName + "." + sDomainName;
    }

    public static String getSLD(String strName, String strURL) {
        if (TextUtils.isEmpty(strName) || TextUtils.isEmpty(strURL))
            return null;

        return getSLD(strName) + (strURL.startsWith("/") ? strURL : "/" + strURL);
    }

    public static String getFileName(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        return strURL.substring(strURL.lastIndexOf("/") + 1).toLowerCase();
    }

    public static final int VALUE_INT_NETWORK_UNKNOWN_TYPE = 0x1000;
    public static final int VALUE_INT_NETWORK_WIFI_TYPE = 0x1001;
    public static final int VALUE_INT_NETWORK_DATA_TYPE = 0x1002;
    public static final int VALUE_INT_NETWORK_2G_TYPE = 0x1003;
    public static final int VALUE_INT_NETWORK_3G_TYPE = 0x1004;
    public static final int VALUE_INT_NETWORK_4G_TYPE = 0x1005;

    public static int getNetworkType(Context context) {
        if (null == context)
            return VALUE_INT_NETWORK_UNKNOWN_TYPE;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm)
            return VALUE_INT_NETWORK_UNKNOWN_TYPE;

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (null == networkInfo)
            return VALUE_INT_NETWORK_UNKNOWN_TYPE;

        int nType = networkInfo.getType();
        int nSubType = networkInfo.getSubtype();
        if (ConnectivityManager.TYPE_WIFI == nType || ConnectivityManager.TYPE_WIMAX == nType || ConnectivityManager.TYPE_ETHERNET == nType) {
            return VALUE_INT_NETWORK_WIFI_TYPE;
        } else if (ConnectivityManager.TYPE_MOBILE == nType) {
            switch (nSubType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return VALUE_INT_NETWORK_2G_TYPE;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return VALUE_INT_NETWORK_3G_TYPE;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return VALUE_INT_NETWORK_4G_TYPE;
            }
        }

        return VALUE_INT_NETWORK_UNKNOWN_TYPE;
    }

    public static boolean isConnectNetwork(Context context) {
        if (null == context)
            return false;

        return VALUE_INT_NETWORK_UNKNOWN_TYPE != getNetworkType(context);
    }

    public static boolean isWifi(Context context) {
        if (null == context)
            return false;

        return VALUE_INT_NETWORK_WIFI_TYPE == getNetworkType(context);
    }

    public static String addHttpToURL(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        if (strURL.startsWith(VALUE_STRING_HTTP_TYPE))
            return strURL;

        return VALUE_STRING_HTTP_TYPE + "://" + strURL;
    }

    public static String addHttpsToURL(String strURL) {
        if (TextUtils.isEmpty(strURL))
            return null;

        if (strURL.startsWith(VALUE_STRING_HTTPS_TYPE))
            return strURL;

        return VALUE_STRING_HTTPS_TYPE + "://" + strURL;
    }

    public static String getTrafficString(long lBytes, boolean bIsRate) {
        Locale localeDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        int nUnit = 1000;
        int nExp = Math.max(0, Math.min((int) (Math.log(lBytes) / Math.log(nUnit)), 3));
        float fBytesUnit = (float) (lBytes / Math.pow(nUnit, nExp));
        DecimalFormat formatter = new DecimalFormat(fBytesUnit > 100 ? "#" : "#.##");
        String strSize = formatter.format(fBytesUnit);

        Locale.setDefault(localeDefault);

        String strUnit = null;
        switch (nExp) {
            case 0:
                strUnit = "B";
                break;
            case 1:
                strUnit = "KB";
                break;
            case 2:
                strUnit = "MB";
                break;
            default:
                strUnit = "GB";
                break;
        }

        if (bIsRate) {
            strUnit += "/s";
        }

        return strSize + " " + strUnit;
    }

    public static void addCommonPara(String key, String value) {
        if (TextUtils.isEmpty(key)||TextUtils.isEmpty(value)) {
            return;
        }
        if (mCommonPara==null) {
            mCommonPara = new HashMap<>();
        }
        mCommonPara.put(key, value);
    }

    public static void setCommonParaMap(Map<String,String> commonParaMap) {
        if (commonParaMap==null) {
            return;
        }
        if (mCommonPara==null) {
            mCommonPara = new HashMap<>();
        }
        mCommonPara.putAll(commonParaMap);
    }

    public static Map<String, String> getCommonPara() {
        return mCommonPara;
    }
}
