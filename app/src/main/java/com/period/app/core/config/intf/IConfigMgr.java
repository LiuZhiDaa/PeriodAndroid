package com.period.app.core.config.intf;

import com.period.app.Constants;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IConfigMgr extends IXManager, IXObserver<IConfigMgrListener> {
    boolean detectLocalInfoAsync();

    boolean requestConfigAsync();

    String VALUE_STRING_CONFIG_AD_KEY_INTERSTITIAL_LAUNCHER = Constants.VALUE_STRING_APP_NAME +"_interstitial_launcher";
    String VALUE_STRING_CONFIG_AD_KEY_INTERSTITIAL_MAIN = Constants.VALUE_STRING_APP_NAME +"_interstitial_main";
    String VALUE_STRING_CONFIG_AD_KEY_NATIVE_MAIN_CALENDAR = Constants.VALUE_STRING_APP_NAME +"_native_calendar";
}
