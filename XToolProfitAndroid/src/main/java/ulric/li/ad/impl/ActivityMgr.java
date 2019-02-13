package ulric.li.ad.impl;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.ads.AudienceNetworkActivity;
import com.google.android.gms.ads.AdActivity;

import ulric.li.XProfitFactory;
import ulric.li.ad.intf.IActivityMgr;
import ulric.li.ad.intf.IAdConfig;
import ulric.li.ad.utils.ProfitAdUtils;
import ulric.li.ad.view.AdParentLayout;
import ulric.li.utils.UtilsLog;
import ulric.li.xlib.impl.XObserverAutoRemove;

/**
 * 监控插屏广告activity的开启，用来控制点击
 * Created by WangYu on 2018/6/11.
 */
public class ActivityMgr extends XObserverAutoRemove implements IActivityMgr {

    public ActivityMgr() {

    }

    @Override
    public void init() {
        ((Application) XProfitFactory.getApplication()).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                dealAdActivity(activity);
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
            }
        });
    }

    /**
     * 判断如果是admob插屏activity或者facebook插屏activity，特殊处理
     *
     * @param activity
     */
    private void dealAdActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        final IAdConfig iAdConfig = ProfitAdUtils.mCurrentInterstitialAdConfig;
        if (iAdConfig == null) {
            return;
        }
        String channel = ProfitAdUtils.getInterstitialAdChannel(activity);
        if (!iAdConfig.isNeedMask(channel)) {
            return;
        }
//        Log.i("wangyu", "add_mask");
        if (activity instanceof AudienceNetworkActivity) {
            //通过intent获取插屏广告类型
//            Serializable viewType = activity.getIntent().getSerializableExtra("viewType");
//            Log.i("wangyu", viewType.toString());
            FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
            FrameLayout mFlContent = decorView.findViewById(android.R.id.content);
            findViewFromGroup(mFlContent);
        } else if (activity instanceof AdActivity) {
            //如果是admob插屏，采取加入adparent拦截点击事件
            try {
                FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
                FrameLayout mFlContent = decorView.findViewById(android.R.id.content);
                ViewGroup parent = (ViewGroup) mFlContent.getParent();
                if (parent == null || parent instanceof AdParentLayout) {
                    //已经添加过adparentlayout 则不需要再次添加
                    return;
                }
                parent.removeView(mFlContent);
                AdParentLayout adParentLayout = new AdParentLayout(XProfitFactory.getApplication());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                adParentLayout.setLayoutParams(params);
                adParentLayout.setInterceptTouchEvent(true);
                adParentLayout.addView(mFlContent);
                parent.addView(adParentLayout);
            } catch (Exception e) {
                UtilsLog.crashLog(e);
            }
        }
    }

    /**
     * 遍历到最根的子view  如果是button 设置不能点击
     * @param viewGroup
     */
    private void findViewFromGroup(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        viewGroup.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                findViewFromGroup((ViewGroup) parent);
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child == null) {
                continue;
            } else if (child instanceof ViewGroup) {
                findViewFromGroup((ViewGroup) child);
            } else {
                if ((child instanceof Button)) {
                    child.setClickable(false);
//                    Log.i("wangyu", "button: "+((Button) child).getText() +"  id:"+child.getId());
                } else if (child instanceof TextView) {
//                    Log.i("wangyu", "TextView: "+((TextView) child).getText()+"  id:"+child.getId());
                } else {
//                    Log.i("wangyu", "other: "+child.getId() + "");
                }
            }
        }
    }
}
