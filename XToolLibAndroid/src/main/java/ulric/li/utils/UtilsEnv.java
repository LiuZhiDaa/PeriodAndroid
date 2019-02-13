package ulric.li.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;

import ulric.li.xlib.BuildConfig;

public class UtilsEnv {
    public static int VALUE_INT_BUFFER_SIZE = 1024;
    private static final String INSTALLATION = "INSTALLATION";
    private static String sChannel = BuildConfig.FLAVOR;
    private static String sPhoneID = null;
    private static String sCountry = null;
    private static boolean sIsDebuggable = false;
    private static String sUUId=null;

    public static void init(Context context, String strChannel) {
        if (null == context)
            return;

        sChannel = strChannel;
        sPhoneID = getDeviceID(context);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sCountry = tm.getSimCountryIso().toUpperCase();

        ApplicationInfo info = context.getApplicationInfo();
        sIsDebuggable = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static int getAndroidSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getAndroidVersionString() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getAndroidManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getAndroidBrand() {
        return android.os.Build.BRAND;
    }

    public static String getAndroidModel() {
        return android.os.Build.MODEL;
    }

    public static String getAndroidDevice() {
        return android.os.Build.DEVICE;
    }

    public static String getCpuABI() {
        return android.os.Build.CPU_ABI;
    }

    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getChannel() {
        return sChannel;
    }

    public static String getPhoneID(Context context) {
        return sPhoneID;
    }

    public static String getCountry() {
        return sCountry;
    }

    public static void setCountry(String strCountry) {
        sCountry = strCountry;
    }

    public static boolean isDebuggable(Context context) {
        return sIsDebuggable;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {
        if (null == context)
            return "unknown";

//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String strPhoneID = tm.getDeviceId();
//        if (!TextUtils.isEmpty(strPhoneID))
//            return strPhoneID;
//
//        strPhoneID = tm.getSimSerialNumber();
//        if (!TextUtils.isEmpty(strPhoneID))
//            return strPhoneID;
        String strPhoneID;
        strPhoneID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        strPhoneID = android.os.Build.SERIAL;
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        strPhoneID = getUUId(context);
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        return "unknown";
    }

    public synchronized static String getUUId(Context context) {
        if (sUUId == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sUUId = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sUUId;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static String getUserSerialNumber(Context context) {
        if (null == context)
            return null;

        Object userManager = context.getSystemService("user");
        if (userManager == null)
            return null;

        try {
            Method methodMyUserHandle = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object objMyUserHandle = methodMyUserHandle.invoke(
                    android.os.Process.class, (Object[]) null);

            Method methodGetSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", objMyUserHandle.getClass());
            long lUserSerial = (Long) methodGetSerialNumberForUser.invoke(userManager, objMyUserHandle);
            return String.valueOf(lUserSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
