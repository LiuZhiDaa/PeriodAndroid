package ulric.li.tool.impl;

import java.util.HashMap;
import java.util.Map;

import ulric.li.tool.intf.IProcessConfigTool;

public class ProcessConfigTool implements IProcessConfigTool {
    private Map<String, Object> mMapConfig = null;

    public ProcessConfigTool() {
        _init();
    }

    private void _init() {
        mMapConfig = new HashMap<>();
    }

    @Override
    public Object getConfig(String strKey) {
        return mMapConfig.get(strKey);
    }

    @Override
    public void setConfig(String strKey, Object object) {
        mMapConfig.put(strKey, object);
    }
}
