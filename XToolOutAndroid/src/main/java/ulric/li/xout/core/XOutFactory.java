package ulric.li.xout.core;

import android.content.Context;

import java.util.HashMap;

import ulric.li.xlib.impl.XFactory;
import ulric.li.xlib.intf.IXFactory;
import ulric.li.xlib.intf.IXObject;
import ulric.li.xout.core.communication.CommunicationImpl;
import ulric.li.xout.core.communication.IOutComponent;
import ulric.li.xout.core.config.impl.OutConfig;
import ulric.li.xout.core.config.impl.OutSceneConfig;
import ulric.li.xout.core.config.intf.IOutConfig;
import ulric.li.xout.core.config.intf.IOutSceneConfig;
import ulric.li.xout.core.scene.impl.OutSceneAntivirus;
import ulric.li.xout.core.scene.impl.OutSceneBoost;
import ulric.li.xout.core.scene.impl.OutSceneCall;
import ulric.li.xout.core.scene.impl.OutSceneCharge;
import ulric.li.xout.core.scene.impl.OutSceneChargeComplete;
import ulric.li.xout.core.scene.impl.OutSceneClean;
import ulric.li.xout.core.scene.impl.OutSceneCool;
import ulric.li.xout.core.scene.impl.OutSceneLock;
import ulric.li.xout.core.scene.impl.OutSceneMgr;
import ulric.li.xout.core.scene.impl.OutSceneNetwork;
import ulric.li.xout.core.scene.impl.OutSceneOptimizeBattery;
import ulric.li.xout.core.scene.impl.OutSceneUninstall;
import ulric.li.xout.core.scene.impl.OutSceneUpdate;
import ulric.li.xout.core.scene.impl.OutSceneWifi;
import ulric.li.xout.core.scene.intf.IOutScene;
import ulric.li.xout.core.scene.intf.IOutSceneMgr;
import ulric.li.xout.core.status.AppStatusMgr;
import ulric.li.xout.core.status.IAppStatusMgr;
import ulric.li.xout.core.trigger.impl.OutTriggerMgr;
import ulric.li.xout.core.trigger.intf.IOutTriggerMgr;
import ulric.li.xout.receiver.HelpServiceReceiver;
import ulric.li.xout.service.OutService;

public class XOutFactory extends XFactory {
    private static IXFactory sIXFactory = null;
    private static Context sContext = null;

    public static IXFactory getInstance() {
        if (sIXFactory == null) {
            synchronized (XOutFactory.class) {
                if (sIXFactory == null)
                    sIXFactory = new XOutFactory();
            }
        }

        return sIXFactory;
    }

    public static void setApplication(Context context) {
        XOutFactory.sContext = context;

        IAppStatusMgr iAppStatusMgr = (IAppStatusMgr) XOutFactory.getInstance().createInstance(IAppStatusMgr.class);
        iAppStatusMgr.init();
        OutService.start(sContext);

        HelpServiceReceiver.register(context);
    }

    public static Context getApplication() {
        return sContext;
    }

    public static void setComponentImpl(IOutComponent componentImpl) {
        IOutComponent iOutComponent = (IOutComponent) XOutFactory.getInstance().createInstance(IOutComponent.class);
        ((CommunicationImpl)iOutComponent).setComponentInstance(componentImpl);
    }


    {

        mXFactoryInterfaceMap = new HashMap<>();
        mXFactoryInterfaceMap.put(IOutSceneMgr.class, new XFactoryImplementMap(new Class<?>[]{
                OutSceneMgr.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IOutScene.class, new XFactoryImplementMap(new Class<?>[]{
                OutSceneBoost.class, OutSceneClean.class, OutSceneCool.class, OutSceneNetwork.class
                , OutSceneCharge.class, OutSceneChargeComplete.class, OutSceneCall.class
                , OutSceneUninstall.class, OutSceneUpdate.class, OutSceneAntivirus.class
                , OutSceneWifi.class, OutSceneLock.class, OutSceneOptimizeBattery.class
        }, new IXObject[]{
                null, null, null, null, null, null, null, null, null, null, null, null, null
        }));
        mXFactoryInterfaceMap.put(IOutConfig.class, new XFactoryImplementMap(new Class<?>[]{
                OutConfig.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IOutSceneConfig.class, new XFactoryImplementMap(new Class<?>[]{
                OutSceneConfig.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IAppStatusMgr.class, new XFactoryImplementMap(new Class<?>[]{
                AppStatusMgr.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IOutTriggerMgr.class, new XFactoryImplementMap(new Class<?>[]{
                OutTriggerMgr.class
        }, new IXObject[]{
                null
        }));
        mXFactoryInterfaceMap.put(IOutComponent.class, new XFactoryImplementMap(new Class<?>[]{
                CommunicationImpl.class
        }, new IXObject[]{
                null
        }));
    }
}
