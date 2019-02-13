package ulric.li;

import android.content.Context;

import com.facebook.ads.AudienceNetworkAds;
import com.google.firebase.FirebaseApp;

import java.util.HashMap;

import ulric.li.ad.impl.ActivityMgr;
import ulric.li.ad.impl.AdConfig;
import ulric.li.ad.impl.AdMgr;
import ulric.li.ad.impl.AdMobAdvertisementMgr;
import ulric.li.ad.impl.AdMobConfig;
import ulric.li.ad.impl.FacebookAdConfig;
import ulric.li.ad.impl.FacebookAdvertisementMgr;
import ulric.li.ad.intf.IActivityMgr;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.intf.IAdMgr;
import ulric.li.ad.intf.IAdvertisementConfig;
import ulric.li.ad.intf.IAdvertisementMgr;
import ulric.li.billing.impl.BillingTool;
import ulric.li.billing.intf.IBillingTool;
import ulric.li.xlib.impl.XFactory;
import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXObject;

public class XProfitFactory extends XFactory {
    private static IXFactory sIXFactory = null;
    private static Context sContext = null;

    public static IXFactory getInstance() {
        if (sIXFactory == null) {
            synchronized (XProfitFactory.class) {
                if (sIXFactory == null)
                    sIXFactory = new XProfitFactory();
            }
        }

        return sIXFactory;
    }

    public static void setApplication(Context context) {
        XProfitFactory.sContext = context;
        IActivityMgr activityMgr = (IActivityMgr) XProfitFactory.getInstance().createInstance(IActivityMgr.class);
        activityMgr.init();
        try {
            FirebaseApp.initializeApp(context);
            AudienceNetworkAds.initialize(context);
            AudienceNetworkAds.isInAdsProcess(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mXFactoryInterfaceMap = new HashMap<>();
        mXFactoryInterfaceMap.put(IBillingTool.class, new XFactoryImplementMap(new Class<?>[]{
                BillingTool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IAdvertisementMgr.class, new XFactoryImplementMap(new Class<?>[]{
                FacebookAdvertisementMgr.class, AdMobAdvertisementMgr.class
        }, new IXObject[]{
                null, null
        }));
        mXFactoryInterfaceMap.put(IAdvertisementConfig.class, new XFactoryImplementMap(new Class<?>[]{
                FacebookAdConfig.class, AdMobConfig.class
        }, new IXObject[]{
                null, null
        }));
        mXFactoryInterfaceMap.put(IAdMgr.class, new XFactoryImplementMap(new Class<?>[]{
                AdMgr.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IAdConfig.class, new XFactoryImplementMap(new Class<?>[]{
                AdConfig.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IActivityMgr.class, new XFactoryImplementMap(new Class<?>[]{
                ActivityMgr.class
        }, new IXObject[]{
                null
        }));
    }
}
