package ulric.li;

import android.content.Context;


import java.util.HashMap;

import ulric.li.tool.impl.DelayTool;
import ulric.li.tool.impl.HttpTool;
import ulric.li.tool.impl.HttpToolResult;
import ulric.li.tool.impl.HttpToolFile;
import ulric.li.tool.impl.LogTool;
import ulric.li.tool.impl.ProcessConfigTool;
import ulric.li.tool.impl.ScreenObserver;
import ulric.li.tool.impl.WakeLockTool;
import ulric.li.tool.intf.IDelayTool;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.tool.intf.IHttpToolFile;
import ulric.li.tool.intf.ILogTool;
import ulric.li.tool.intf.IProcessConfigTool;
import ulric.li.tool.intf.IScreenObserver;
import ulric.li.tool.intf.IWakeLockTool;
import ulric.li.xlib.impl.XFactory;
import ulric.li.xlib.impl.XThreadPool;
import ulric.li.xlib.impl.XThreadQueue;
import ulric.li.xlib.impl.XTimer;
import ulric.li.xlib.impl.XTimer2;
import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXObject;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadQueue;
import ulric.li.xlib.intf.IXTimer;

public class XLibFactory extends XFactory {
    private static IXFactory sIXFactory = null;
    private static Context sContext = null;

    public static IXFactory getInstance() {
        if (sIXFactory == null) {
            synchronized (XLibFactory.class) {
                if (sIXFactory == null)
                    sIXFactory = new XLibFactory();
            }
        }

        return sIXFactory;
    }

    public static void setApplication(Context context) {
        XLibFactory.sContext = context;
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mXFactoryInterfaceMap = new HashMap<>();
        mXFactoryInterfaceMap.put(IXThreadPool.class, new XFactoryImplementMap(new Class<?>[]{
                XThreadPool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IXThreadQueue.class, new XFactoryImplementMap(new Class<?>[]{
                XThreadQueue.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IXTimer.class, new XFactoryImplementMap(new Class<?>[]{
                XTimer.class, XTimer2.class
        }, new IXObject[]{
                null, null
        }));

        mXFactoryInterfaceMap.put(ILogTool.class, new XFactoryImplementMap(new Class<?>[]{
                LogTool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IHttpTool.class, new XFactoryImplementMap(new Class<?>[]{
                HttpTool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IHttpToolResult.class, new XFactoryImplementMap(new Class<?>[]{
                HttpToolResult.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IHttpToolFile.class, new XFactoryImplementMap(new Class<?>[]{
                HttpToolFile.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IDelayTool.class, new XFactoryImplementMap(new Class<?>[]{
                DelayTool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IScreenObserver.class, new XFactoryImplementMap(new Class<?>[]{
                ScreenObserver.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IProcessConfigTool.class, new XFactoryImplementMap(new Class<?>[]{
                ProcessConfigTool.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IWakeLockTool.class, new XFactoryImplementMap(new Class<?>[]{
                WakeLockTool.class
        }, new IXObject[]{
                null
        }));
    }
}
