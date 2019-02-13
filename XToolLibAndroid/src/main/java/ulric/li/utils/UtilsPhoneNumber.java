package ulric.li.utils;

import android.text.TextUtils;

public class UtilsPhoneNumber {
    public static final String VALUE_STRING_INDIA_COUNTRY_CODE = "+91";
    public static final String VALUE_STRING_CHINA_COUNTRY_CODE = "+86";

    public static String getDefaultCountryCode() {
        return VALUE_STRING_INDIA_COUNTRY_CODE;
    }

    public static boolean isCorrectPhoneNumber(String strCountryCode, String strPhoneNumber) {
        if (TextUtils.isEmpty(strCountryCode) || TextUtils.isEmpty(strPhoneNumber))
            return false;

        if (!strCountryCode.startsWith("+")) {
            strCountryCode = "+" + strCountryCode;
        }

        if (strCountryCode.equals(VALUE_STRING_INDIA_COUNTRY_CODE))
            return 10 == strPhoneNumber.length();
        else if (strCountryCode.equals(VALUE_STRING_CHINA_COUNTRY_CODE))
            return 11 == strPhoneNumber.length();

        return false;
    }

    public static boolean isCorrectVerifyCode(String strVerifyCode) {
        if (TextUtils.isEmpty(strVerifyCode))
            return false;

        return 4 == strVerifyCode.length();
    }

    public static String addCountryCodeToPhoneNumber(String strCountryCode, String strPhoneNumber) {
        if (TextUtils.isEmpty(strCountryCode) || TextUtils.isEmpty(strPhoneNumber))
            return null;

        if (strPhoneNumber.startsWith("+"))
            return strPhoneNumber;
        else if (strPhoneNumber.startsWith(strCountryCode))
            return "+" + strPhoneNumber;

        if (!strCountryCode.startsWith("+")) {
            strCountryCode = "+" + strCountryCode;
        }

        return strCountryCode + strPhoneNumber;
    }
}
