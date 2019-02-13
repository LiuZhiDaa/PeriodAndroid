package com.period.app.core;

import android.content.Context;


import com.period.app.core.calendardialog.CalendarDialogManger;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.config.impl.CloudConfig;
import com.period.app.core.config.impl.ConfigMgr;
import com.period.app.core.config.intf.ICloudConfig;
import com.period.app.core.config.intf.IConfigMgr;
import com.period.app.core.dba.DbaManger;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.core.prediction.PredictionManger;

import com.period.app.core.data.DataMgr;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.reminder.IReminderMgr;
import com.period.app.core.reminder.ReminderMgr;

import java.util.HashMap;

import ulric.li.xlib.impl.XFactory;
import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXObject;

public class XCoreFactory extends XFactory {
    private static IXFactory sIXFactory = null;
    private static Context sContext = null;

    public static IXFactory getInstance() {
        if (sIXFactory == null) {
            synchronized (XCoreFactory.class) {
                if (sIXFactory == null)
                    sIXFactory = new XCoreFactory();
            }
        }

        return sIXFactory;
    }

    public static void setApplication(Context context) {
        XCoreFactory.sContext = context;
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mXFactoryInterfaceMap = new HashMap<>();
//        mXFactoryInterfaceMap.put(IConfigMgr.class, new XFactoryImplementMap(new Class<?>[]{
//                ConfigMgr.class
//        }, new IXObject[]{
//                null
//        }));

        mXFactoryInterfaceMap.put(IDataMgr.class, new XFactoryImplementMap(new Class<?>[]{
                DataMgr.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IPredictionManger.class, new XFactoryImplementMap(new Class<?>[]{PredictionManger.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IReminderMgr.class, new XFactoryImplementMap(new Class<?>[]{ReminderMgr.class}, new IXObject[]{null}));
        mXFactoryInterfaceMap.put(IDbaManger.class, new XFactoryImplementMap(new Class<?>[]{
                DbaManger.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(ICalendarDialogManger.class, new XFactoryImplementMap(new Class<?>[]{
                CalendarDialogManger.class
        }, new IXObject[]{
                null
        }));

        mXFactoryInterfaceMap.put(IConfigMgr.class,new XFactoryImplementMap(new Class[]{ConfigMgr.class},new IXObject[]{null}));
        mXFactoryInterfaceMap.put(ICloudConfig.class,new XFactoryImplementMap(new Class[]{CloudConfig.class},new IXObject[]{null}));

    }
}
