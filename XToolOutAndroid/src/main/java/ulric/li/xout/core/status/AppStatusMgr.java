package ulric.li.xout.core.status;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;

import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xout.core.XOutFactory;
import ulric.li.xout.main.base.OutBaseActivity;

/**
 * Created by WangYu on 2018/6/11.
 */
public class AppStatusMgr extends XObserverAutoRemove<IAppStatusListener> implements IAppStatusMgr {
    private boolean mIsInApp;
    private int mActivityAmount = 0;
    private ArrayList<Class<?>> mActivitySimpleClassList;

    public AppStatusMgr() {
        mActivitySimpleClassList = new ArrayList<>();
    }

    @Override
    public void init() {
        ((Application) XOutFactory.getApplication()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                if (activity == null) {
                    return;
                }
                mActivityAmount++;
                try {
                    if (mActivitySimpleClassList != null) {
                        mActivitySimpleClassList.add(activity.getClass());
                    }
                } catch (Exception e) {

                }
                if (mActivityAmount > 0) {
                    setAppStatus(true);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivityAmount--;
//                UtilsProfitLog.logI("wangyu","destroy:"+ activity.getClass().getSimpleName());
                if (mActivityAmount <= 0) {
                    // 此时表明应用在后台
                    setAppStatus(false);
                }
                try {
                    if (mActivitySimpleClassList != null && activity != null) {
                        mActivitySimpleClassList.remove(activity.getClass());
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    /**
     * 判断是否在前台
     * 注意：home出去依旧属于在前台
     *
     * @return
     */
    @Override
    public boolean isAppForeground() {
        if (mActivitySimpleClassList == null) {
            return false;
        }
        if (mActivitySimpleClassList.isEmpty()) {
            return false;
        }
        for (Class<?> cls : mActivitySimpleClassList) {
            if (!OutBaseActivity.class.isAssignableFrom(cls)) {
                //遍历所有的class，只要有一个不属于outbaseactivity,那么就是前台
                return true;
            }
        }
        //走到这里说明所有的cls都是体外页面，此时被动触发的页面是可以弹出的
        return false;
    }

    @Override
    public boolean isApplicationForeground() {
        return mIsInApp;
    }

    @Override
    public int getActivityCount() {
        return mActivityAmount;
    }

    /**
     * 判断主动触发的体外页面是否在前台
     *
     * @return
     */
    @Override
    public boolean isProactiveOutPageForeground() {
//        if (mActivitySimpleClassList == null) {
//            return false;
//        }
//        if (mActivitySimpleClassList.isEmpty()) {
//            return false;
//        }
//        for (Class cls : mProactiveActivityClassArr) {
//            if (mActivitySimpleClassList.contains(cls)) {
//                return true;
//            }
//        }
        return false;
    }


    private void setAppStatus(boolean isInApp) {
        if (mIsInApp != isInApp) {
            mIsInApp = isInApp;
//            UtilsProfitLog.logI("wangyu", "status:"+isInApp);
            for (IAppStatusListener listener : getListenerList()) {
                listener.onAppStatusChanged(mIsInApp);
            }
        }
    }
}
