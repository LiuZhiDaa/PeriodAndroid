package com.period.app.core.config.intf;

public interface IConfigMgrListener {
    void onDetectLocalInfoAsyncComplete();

    void onRequestConfigAsync(boolean bSuccess);
}
