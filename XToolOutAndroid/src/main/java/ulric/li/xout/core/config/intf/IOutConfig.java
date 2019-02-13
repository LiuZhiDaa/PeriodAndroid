package ulric.li.xout.core.config.intf;

import java.util.List;

import ulric.li.mode.intf.IXJsonSerialization;
import ulric.li.xlib.intf.IXManager;

public interface IOutConfig extends IXManager, IXJsonSerialization {
    // 体外总开关
    boolean isOutEnable();

    // 体外广告开关
    boolean isOutAdEnable();

    // 体外声明开关
    boolean isOutStatementEnable();

    // 体外潜伏期
    long getOutLatentTime();

    // 体外是否处在潜伏期中
    boolean isInLatentTime();

    // 体外场景检测周期
    long getOutSceneLoopTime();

    // 体外场景化支持列表 按权重排序
    List<String> getOutSceneList();

    // 体外某个场景开关
    boolean isOutSceneEnable(String strSceneType);

    // 体外某个场景广告开关
    boolean isOutSceneAdEnable(String strSceneType);

    // 体外某个场景每天触发次数限制
    int getOutSceneCountLimitOneDay(String strSceneType);

    // 体外某个场景保护时间
    long getOutSceneProtectTime(String strSceneType);

    // 体外某个场景保护时间的延长随机时间
    long getOutSceneProtectRandomTime(String strSceneType);

    // 体外获取某个场景配置
    IOutSceneConfig getOutSceneConfig(String strSceneType);

    // 体外主动版位之间是否处在保护时间中
    boolean isInActiveSceneProtectTime();

    // 体外主动版位更新最后展示时间
    void recordActiveSceneTriggerTime();

    boolean isShowNotification();

}
