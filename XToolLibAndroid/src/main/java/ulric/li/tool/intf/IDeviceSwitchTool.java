package ulric.li.tool.intf;

import android.content.Context;

import ulric.li.xlib.intf.IXManager;

public interface IDeviceSwitchTool extends IXManager {
    int getBrightnessMode();

    void setBrightnessMode(int nBrightnessMode);

    int getBacklightBrightness();

    void setBacklightBrightness(int nValue);

    int getScreenOffTimeout();

    void setScreenOffTimeout(int nTime);

    int getRingerMode();

    void setRingerMode(int nRingerMode);

    boolean isWifiEnable();

    void setWifiEnable(boolean bIsEnable);

    boolean isBluetoothEnable();

    void setBluetoothEnable(boolean bIsEnable);

    boolean isRotationEnable();

    void setRotationEnable(boolean bIsEnable);

    boolean isGPSEnable();

    void openGPSSettings(Context context);

    int VALUE_INT_SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
    int VALUE_INT_SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
}
