package ulric.li.tool.impl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IDeviceSwitchTool;

public class DeviceSwitchTool implements IDeviceSwitchTool {
    private Context mContext = null;

    public DeviceSwitchTool() {
        mContext = XLibFactory.getApplication();
        _init();
    }

    private void _init() {
    }

    @Override
    public int getBrightnessMode() {
        try {
            return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return VALUE_INT_SCREEN_BRIGHTNESS_MODE_MANUAL;
    }

    @Override
    public void setBrightnessMode(int nBrightnessMode) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, nBrightnessMode);
    }

    @Override
    public int getBacklightBrightness() {
        try {
            return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 255;
    }

    @Override
    public void setBacklightBrightness(int nValue) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, nValue);
    }

    @Override
    public int getScreenOffTimeout() {
        try {
            return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void setScreenOffTimeout(int nTime) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, nTime);
    }

    @Override
    public int getRingerMode() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode();
    }

    @Override
    public void setRingerMode(int nRingerMode) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(nRingerMode);
    }

    @Override
    public boolean isWifiEnable() {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    @Override
    public void setWifiEnable(boolean bIsEnable) {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(bIsEnable);
    }

    @Override
    public boolean isBluetoothEnable() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        return ba.isEnabled();
    }

    @Override
    public void setBluetoothEnable(boolean bIsEnable) {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (bIsEnable)
            ba.enable();
        else
            ba.disable();
    }

    @Override
    public boolean isRotationEnable() {
        try {
            return 1 == Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void setRotationEnable(boolean bIsEnable) {
        Settings.System.putInt(mContext.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, bIsEnable ? 1 : 0);
    }

    @Override
    public boolean isGPSEnable() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void openGPSSettings(Context context) {
        if (null == context)
            return;

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        if (!Activity.class.isAssignableFrom(context.getClass())) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }
}
