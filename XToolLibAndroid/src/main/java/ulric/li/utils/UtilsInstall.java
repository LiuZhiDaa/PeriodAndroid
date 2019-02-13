package ulric.li.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UtilsInstall {
    public static final int VALUE_INT_INSTALL_ERROR_TYPE = 0x1000;
    public static final int VALUE_INT_INSTALL_NEW_TYPE = 0x1001;
    public static final int VALUE_INT_INSTALL_UPDATE_TYPE = 0x1002;
    public static final int VALUE_INT_INSTALL_NORMAL_TYPE = 0x1003;

    private static final int VALUE_INT_DEFAULT_VERSION_CODE = -1;

    private static int sInstallType = VALUE_INT_INSTALL_ERROR_TYPE;

    private static int sAppOldVersionCode = -1;

    public static int getInstallType(Context context) {
        if (null == context)
            return sInstallType;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sAppOldVersionCode = sp.getInt("app_version_code", VALUE_INT_DEFAULT_VERSION_CODE);
        int nAppNewVersionCode = UtilsApp.getMyAppVersionCode(context);
        sp.edit().putInt("app_version_code", nAppNewVersionCode).apply();
        if (VALUE_INT_DEFAULT_VERSION_CODE == sAppOldVersionCode) {
            sInstallType = VALUE_INT_INSTALL_NEW_TYPE;
            return sInstallType;
        }

        if (nAppNewVersionCode > sAppOldVersionCode) {
            sInstallType = VALUE_INT_INSTALL_UPDATE_TYPE;
            return sInstallType;
        } else if (nAppNewVersionCode == sAppOldVersionCode) {
            sInstallType = VALUE_INT_INSTALL_NORMAL_TYPE;
            return sInstallType;
        }

        return sInstallType;
    }

    public static int getInstallType() {
        return sInstallType;
    }

    public static void setInstallType(int nInstallType) {
        sInstallType = nInstallType;
    }

    public static int getAppOldVersionCode() {
        return sAppOldVersionCode;
    }
}
