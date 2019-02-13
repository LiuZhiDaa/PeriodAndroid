package ulric.li.xout.core.scene.intf;

import ulric.li.xlib.intf.IXManager;
import ulric.li.xlib.intf.IXObserver;

public interface IOutSceneMgr extends IXManager, IXObserver<IOutSceneMgrListener> {
    // 初始化
    boolean initAsync();

    // 是否在监听中
//    boolean isListened();

    // 开始监听
    boolean startListen();

    // 停止监听
    void stopListen();

    /**
     * 开启主动场景循环任务
     */
    void startActiveSceneLoop();

    // 获取场景
    IOutScene getOutScene(String strSceneType, boolean bIsNeedActiveScene, boolean bIsNeedPassiveScene);

    // 加速场景
    String VALUE_STRING_BOOST_SCENE_TYPE = "boost";

    // 清理场景
    String VALUE_STRING_CLEAN_SCENE_TYPE = "clean";

    // 降温场景
    String VALUE_STRING_COOL_SCENE_TYPE = "cool";

    // 网络场景
    String VALUE_STRING_NETWORK_SCENE_TYPE = "network";

    // WIFI优化场景
    String VALUE_STRING_WIFI_SCENE_TYPE = "wifi";

    // 充电场景
    String VALUE_STRING_CHARGE_SCENE_TYPE = "charge";

    // 充电完成场景
    String VALUE_STRING_CHARGE_COMPLETE_SCENE_TYPE = "charge_complete";

    // 电话呼叫场景
    String VALUE_STRING_CALL_SCENE_TYPE = "call";

    // 锁屏场景
    String VALUE_STRING_LOCK_SCENE_TYPE = "lock";

    // 卸载场景
    String VALUE_STRING_UNINSTALL_SCENE_TYPE = "uninstall";

    // 更新场景
    String VALUE_STRING_UPDATE_SCENE_TYPE = "update";

    // 杀毒场景
    String VALUE_STRING_ANTIVIRUS_SCENE_TYPE = "antivirus";

    //电池优化
    String VALUE_STRING_BATTERY_OPTIMIZE_SCENE_TYPE = "battery_optimize";
}
