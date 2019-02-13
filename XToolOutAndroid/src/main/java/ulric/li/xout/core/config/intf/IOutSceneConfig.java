package ulric.li.xout.core.config.intf;

import ulric.li.mode.intf.IXJsonSerialization;
import ulric.li.xlib.intf.IXObject;

public interface IOutSceneConfig extends IXObject, IXJsonSerialization {
    // 场景开关
    boolean isEnable();

    // 场景广告开关
    boolean isAdEnable();

    // 场景每天最大展示次数
    int getCountLimitOneDay();

    // 场景自身保护时间
    long getProtectTime();

    // 场景自身保护随机时间
    long getProtectRandomTime();

    // 特殊场景参数 加速
    float getOutSceneBoostRate();

    // 特殊场景参数 降温
    float getOutSceneCoolTemperature();

    // 特殊场景参数 锁屏
    String getOutSceneLockCondition();
}
