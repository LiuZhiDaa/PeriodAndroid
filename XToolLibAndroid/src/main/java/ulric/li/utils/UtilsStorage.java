package ulric.li.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UtilsStorage {
    public static String getInternalStorageDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    public static long getInternalStorageAvailableSize() {
        StatFs stat = new StatFs(getInternalStorageDirectory());
        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }

    public static long getInternalStorageTotalSize() {
        StatFs stat = new StatFs(getInternalStorageDirectory());
        return stat.getBlockSizeLong() * stat.getBlockCountLong();
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static long getExternalStorageAvailableSize() {
        if (!isExternalStorageAvailable())
            return 0;

        StatFs stat = new StatFs(getExternalStorageDirectory());
        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }

    public static long getExternalStorageTotalSize() {
        if (!isExternalStorageAvailable())
            return 0;

        StatFs stat = new StatFs(getExternalStorageDirectory());
        return stat.getBlockSizeLong() * stat.getBlockCountLong();
    }

    public static boolean isExternalSDCardAvailable(Context context) {
        if (null == context)
            return false;

        String[] arraystrPath = getAllMountPointPath(context);
        return null != arraystrPath && arraystrPath.length > 1;
    }

    public static long getStorageAvailableSize(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return 0;

        StatFs stat = new StatFs(strPath);
        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }

    public static long getStorageTotalSize(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return 0;

        StatFs stat = new StatFs(strPath);
        return stat.getBlockSizeLong() * stat.getBlockCountLong();
    }

    public static long getAllStorageAvailableSize(Context context) {
        if (null == context)
            return 0;

        String[] arraystrPath = getAllMountPointPath(context);
        if (null == arraystrPath || 0 == arraystrPath.length)
            return getInternalStorageAvailableSize();

        long lAvailableSize = 0;
        for (String strPath : arraystrPath) {
            if (TextUtils.isEmpty(strPath) || !UtilsFile.isExists(strPath))
                continue;

            StatFs stat = new StatFs(strPath);
            lAvailableSize += stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

        }

        return lAvailableSize;
    }

    public static long getAllStorageTotalSize(Context context) {
        if (null == context)
            return 0;

        String[] arraystrPath = getAllMountPointPath(context);
        if (null == arraystrPath || 0 == arraystrPath.length)
            return getInternalStorageTotalSize();

        long lTotalSize = 0;
        for (String strPath : arraystrPath) {
            if (TextUtils.isEmpty(strPath) || !UtilsFile.isExists(strPath))
                continue;

            StatFs stat = new StatFs(strPath);
            lTotalSize += stat.getBlockSizeLong() * stat.getBlockCountLong();
        }

        return lTotalSize;
    }

    public static boolean isOnExternalSDCard(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return false;

        return !strPath.startsWith(getExternalStorageDirectory());
    }

    public static boolean isMountPointPathWritable(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return false;

        String strMountPointPath = getMountPointPath(context, strPath);
        if (TextUtils.isEmpty(strMountPointPath))
            return false;

        File file = null;
        for (int nIndex = 0; ; nIndex++) {
            String strFileName = "XFMTempFile" + nIndex;
            String str = UtilsFile.makePath(strMountPointPath, strFileName);
            if (!UtilsFile.isExists(str)) {
                file = new File(str);
                break;
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            try {
                fos.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        boolean bResult = file.canWrite();
        file.delete();
        return bResult;
    }

    public static boolean isMountPointPath(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return false;

        String strStorage = getMountPointPath(context, strPath);
        if (TextUtils.isEmpty(strStorage))
            return false;

        return strStorage.equals(strPath);
    }

    public static String getMountPointPath(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return null;

        String[] arraystrPath = getAllMountPointPath(context);
        if (null == arraystrPath || 0 == arraystrPath.length)
            return null;

        for (String str : arraystrPath) {
            if (TextUtils.isEmpty(str))
                continue;

            if (strPath.startsWith(str))
                return str;
        }

        return null;
    }

    @SuppressLint("NewApi")
    public static String[] getAllMountPointPath(Context context) {
        if (null == context)
            return null;

        String[] strarrayPath = null;
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class<?>[] paramClasses = {};
            Method getVolumePaths = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePaths.setAccessible(true);
            Object[] params = {};
            strarrayPath = (String[]) getVolumePaths.invoke(sm, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> listPath = new ArrayList<String>();
        if (null != strarrayPath && strarrayPath.length > 0) {
            for (String strPath : strarrayPath) {
                if (TextUtils.isEmpty(strPath))
                    continue;

                File file = new File(strPath);
                if (!file.isDirectory())
                    continue;

                String[] strarrayList = file.list();
                if (null == strarrayList || 0 == strarrayList.length)
                    continue;

                listPath.add(strPath);
            }
        }

        if (listPath.isEmpty())
            return null;

        return listPath.toArray(new String[listPath.size()]);
    }

    public static String getSizeString(long lSize, boolean bHasByte) {
        Locale localeDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        int nUnit = 1000;
        int nExp = Math.max(0, Math.min((int) (Math.log(lSize) / Math.log(nUnit)), 3));
        float fBytesUnit = (float) (lSize / Math.pow(nUnit, nExp));
        DecimalFormat formatter = new DecimalFormat(fBytesUnit > 100 ? "#" : "#.##");
        String strSize = formatter.format(fBytesUnit);

        Locale.setDefault(localeDefault);

        String strUnit = null;
        switch (nExp) {
            case 0:
                strUnit = "B";
                break;
            case 1:
                strUnit = bHasByte ? "KB" : "K";
                break;
            case 2:
                strUnit = bHasByte ? "MB" : "M";
                break;
            default:
                strUnit = bHasByte ? "GB" : "G";
                break;
        }

        return strSize + " " + strUnit;
    }

    public static String[] getSizeStringArray(long lSize, boolean bHasByte) {
        String[] strarray = new String[2];

        Locale localeDefault = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        int nUnit = 1000;
        int nExp = Math.max(0, Math.min((int) (Math.log(lSize) / Math.log(nUnit)), 3));
        float fBytesUnit = (float) (lSize / Math.pow(nUnit, nExp));
        DecimalFormat formatter = new DecimalFormat(fBytesUnit > 100 ? "#" : "#.##");
        strarray[0] = formatter.format(fBytesUnit);

        Locale.setDefault(localeDefault);

        switch (nExp) {
            case 0:
                strarray[1] = "B";
                break;
            case 1:
                strarray[1] = bHasByte ? "KB" : "K";
                break;
            case 2:
                strarray[1] = bHasByte ? "MB" : "M";
                break;
            default:
                strarray[1] = bHasByte ? "GB" : "G";
                break;
        }

        return strarray;
    }
}
