package ulric.li.tool.intf;

import org.json.JSONObject;

import ulric.li.xlib.intf.IXManager;

public interface ILogTool extends IXManager {
    void statisticsLog(String strKey1, String strKey2, JSONObject jsonObjectContent, JSONObject jsonObjectUserInfo);

    void crashLog(Throwable throwable,String tag);

    void sendLog();
}
