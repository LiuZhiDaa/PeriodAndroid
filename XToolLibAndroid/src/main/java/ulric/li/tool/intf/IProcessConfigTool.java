package ulric.li.tool.intf;

import ulric.li.xlib.intf.IXManager;

public interface IProcessConfigTool extends IXManager {
    Object getConfig(String strKey);

    void setConfig(String strKey, Object object);
}
