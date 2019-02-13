package ulric.li.xout.core.trigger.intf;

import org.json.JSONObject;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

/**
 * Created by WangYu on 2018/7/16.
 */
public interface IOutTriggerMgr extends IXManager, IXObserver<IOutTriggerMgrListener> {
    // 触发主动页面
    void triggerActivePage(String strScene);

    // 触发被动页面
    void triggerPassivePage(String strScene, JSONObject jsonObjectData);

    long getChargeTime();
}
