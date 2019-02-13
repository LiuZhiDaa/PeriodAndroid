package com.period.app.main;

import com.period.app.R;
import com.period.app.base.BaseActivity;
import com.period.app.core.XCoreFactory;
import com.period.app.core.config.intf.IConfigMgr;
import com.period.app.core.config.intf.IConfigMgrListener;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.utils.StatusBarUtils;

import ulric.li.XLibFactory;

import ulric.li.utils.UtilsLog;
import ulric.li.xlib.intf.IXTimer;
import ulric.li.xlib.intf.IXTimerListener;

public class LaunchActivity extends BaseActivity {

    long DELAYTIME = 1000;
    private IXTimer mTimer;
    private IConfigMgr mIConfigMgr;
    private IConfigMgrListener mIConfigMgrListener;
    private int mCheckLoadingCompleteCount = 0;

    private IDataMgr dmgr;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_launch;
    }

    @Override
    public void init() {
        StatusBarUtils.hideBottomUIMenu(this);
        UtilsLog.startLog("splash", null);



        mIConfigMgr = (IConfigMgr) XCoreFactory.getInstance().createInstance(IConfigMgr.class);
        mIConfigMgrListener = new IConfigMgrListener() {
            @Override
            public void onDetectLocalInfoAsyncComplete() {

                checkLoadingComplete();
            }

            @Override

            public void onRequestConfigAsync(boolean bSuccess) {
            }
        };
        mIConfigMgr.addListener(mIConfigMgrListener);
        mIConfigMgr.requestConfigAsync();
        if (mIConfigMgr.detectLocalInfoAsync()) {
            mCheckLoadingCompleteCount++;
        }

        dmgr= (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        mTimer = (IXTimer) XLibFactory.getInstance().createInstance(IXTimer.class);
        mTimer.start(DELAYTIME, new IXTimerListener() {
            @Override
            public void onTimerComplete() {
                checkLoadingComplete();
                if (dmgr.getIsInfoComplete()){
                    IDbaManger iDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
                    iDbaManger.supplyData();
                }
            }

            @Override
            public void onTimerRepeatComplete() {

            }
        });
        {
            mCheckLoadingCompleteCount++;
        }
    }



    private void checkLoadingComplete() {
        mCheckLoadingCompleteCount--;
        if (mCheckLoadingCompleteCount > 0)
            return;

        goActivity(dmgr.getIsInfoComplete()?MainActivity.class:SplashActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer!=null) {
            mTimer.stop();
        }
        if (null != mIConfigMgr) {
            mIConfigMgr.removeListener(mIConfigMgrListener);
        }
    }
}
