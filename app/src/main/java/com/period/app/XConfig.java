package com.period.app;

import android.app.Application;


import com.period.app.core.XCoreFactory;


import ulric.li.XLibFactory;


import ulric.li.referrer.InstallReferrerReceiver;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;

import ulric.li.xui.utils.UtilsImageLoader;



public class XConfig {
    private static final boolean VALUE_BOOLEAN_IS_NEED_SEND_LOG = !BuildConfig.DEBUG;
    private static final boolean VALUE_BOOLEAN_IS_NEED_LOCAL_LOG = BuildConfig.DEBUG;

    public static final String VALUE_STRING_REFERRER_URL = "/api/v3/referrer/" + Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_STATISTICS_LOG_URL = "/api/v3/statistics_log/" + Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_CRASH_LOG_URL = "/api/v3/crash_log/" + Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_CONFIG_URL = "/api/v3/config/" + Constants.VALUE_STRING_DB_NAME;
    public static final String VALUE_STRING_COUNTRY_URL = "/api/v3/country/" + Constants.VALUE_STRING_DB_NAME;

    public static void init(Application application) {
        XCoreFactory.setApplication(application);
        XLibFactory.setApplication(application);

        UtilsJson.addFactory(XCoreFactory.getInstance());

        UtilsEnv.init(application, BuildConfig.FLAVOR);
        UtilsNetwork.init(Constants.VALUE_STRING_DOMAIN_NAME);
        UtilsEncrypt.init(Constants.VALUE_STRING_BLOW_FISH_KEY);
        UtilsLog.init(VALUE_BOOLEAN_IS_NEED_SEND_LOG, VALUE_BOOLEAN_IS_NEED_LOCAL_LOG,
                VALUE_STRING_STATISTICS_LOG_URL, VALUE_STRING_CRASH_LOG_URL,
                null, null, null);

        UtilsImageLoader.init(application, R.drawable.ic_launcher_background);
        InstallReferrerReceiver.init(application, UtilsNetwork.getURL(VALUE_STRING_REFERRER_URL));
    }
}
