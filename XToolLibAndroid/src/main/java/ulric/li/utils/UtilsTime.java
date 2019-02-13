package ulric.li.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsTime {
    public static final long VALUE_LONG_TIME_ONE_SECOND = 1000;
    public static final long VALUE_LONG_TIME_ONE_MINUTE = VALUE_LONG_TIME_ONE_SECOND * 60;
    public static final long VALUE_LONG_TIME_ONE_HOUR = VALUE_LONG_TIME_ONE_MINUTE * 60;
    public static final long VALUE_LONG_TIME_ONE_DAY = VALUE_LONG_TIME_ONE_HOUR * 24;

    public static String getTimeString(long lTime) {
        String strUnit = null;
        double dTime = 0;
        boolean bIsNeedDecimal = true;
        if (lTime >= VALUE_LONG_TIME_ONE_DAY) {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_DAY;
            strUnit = "days";
            bIsNeedDecimal = true;
        } else if (lTime >= VALUE_LONG_TIME_ONE_HOUR) {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_HOUR;
            strUnit = "hours";
            bIsNeedDecimal = true;
        } else {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_MINUTE;
            strUnit = "mins";
            bIsNeedDecimal = false;
        }

        Locale localeDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        DecimalFormat formatter = new DecimalFormat(bIsNeedDecimal ? "#.#" : "#");
        String strResult = formatter.format(dTime) + strUnit;

        Locale.setDefault(localeDefault);
        return strResult;
    }

    public static String[] getTimeStringArray(long lTime) {
        String[] strarray = new String[2];
        double dTime = 0;
        boolean bIsNeedDecimal = true;
        if (lTime >= VALUE_LONG_TIME_ONE_DAY) {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_DAY;
            strarray[1] = "days";
            bIsNeedDecimal = true;
        } else if (lTime >= VALUE_LONG_TIME_ONE_HOUR) {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_HOUR;
            strarray[1] = "hours";
            bIsNeedDecimal = true;
        } else {
            dTime = (double) lTime / VALUE_LONG_TIME_ONE_MINUTE;
            strarray[1] = "mins";
            bIsNeedDecimal = false;
        }

        Locale localeDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        DecimalFormat formatter = new DecimalFormat(bIsNeedDecimal ? "#.#" : "#");
        strarray[0] = formatter.format(dTime);

        Locale.setDefault(localeDefault);
        return strarray;
    }

    public static String getTimeHhMmSs(long second) {
        if (second < VALUE_LONG_TIME_ONE_SECOND) {
            return "00:00:00";
        }

        long hours = second / VALUE_LONG_TIME_ONE_HOUR;
        second = second % VALUE_LONG_TIME_ONE_HOUR;
        long minutes = second / VALUE_LONG_TIME_ONE_MINUTE;
        second = (second % VALUE_LONG_TIME_ONE_MINUTE) / VALUE_LONG_TIME_ONE_SECOND;

        String hoursStr, minuteStr, secondStr;
        if (hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = String.valueOf(hours);
        }

        if (minutes < 10) {
            minuteStr = "0" + minutes;
        } else {
            minuteStr = String.valueOf(minutes);
        }

        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = String.valueOf(second);
        }

        return hoursStr + ":" + minuteStr + ":" + secondStr;
    }

    public static String getDateStringYyyyMmDdHhMmSs(long lTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(lTime));
    }

    public static String getDateStringYyyyMmDd(long lTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date(lTime));
    }

    public static String getDateStringHh(long lTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        return simpleDateFormat.format(new Date(lTime));
    }
}
