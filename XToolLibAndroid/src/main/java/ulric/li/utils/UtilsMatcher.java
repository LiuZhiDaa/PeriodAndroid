package ulric.li.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsMatcher {
    public static boolean isValidEmail(String strEmail) {
        if (TextUtils.isEmpty(strEmail))
            return true;

        String strPattern = "[a-zA-Z0-9_\\.\\-]+@[a-zA-Z0-9_]+(\\.[a-zA-Z]+)+";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strEmail);
        return matcher.matches();
    }

    public static boolean isValidName(String strText, boolean bIsAllowSpace, boolean bIsAllowUnderline, boolean bIsAllowDot, int nMinSize, int nMaxSize) {
        if (TextUtils.isEmpty(strText))
            return false;

        String strExtra = "";
        if (bIsAllowSpace)
            strExtra += "\\s";

        if (bIsAllowUnderline)
            strExtra += "_";

        if (bIsAllowDot)
            strExtra += "\\.";

        String strCount = "{" + nMinSize + "," + nMaxSize + "}";
        String strPattern = "[" + "a-zA-Z0-9" + strExtra + "]" + strCount;
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strText);
        return matcher.matches();
    }

    public static boolean isValidNumber(String strText, int nMinSize, int nMaxSize) {
        if (TextUtils.isEmpty(strText))
            return false;

        String strCount = "{" + nMinSize + "," + nMaxSize + "}";
        String strPattern = "[0-9]" + strCount;
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strText);
        return matcher.matches();
    }
}
