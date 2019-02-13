package com.period.app.main;

import android.os.Message;
import android.util.Log;
import android.view.View;

import com.period.app.R;
import com.period.app.base.BaseActivity;
import com.period.app.bean.DatePhysiologyBean;
import com.period.app.constants.PeriodConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.StatusBarUtils;
import com.period.app.utils.TimeUtils;


import butterknife.OnClick;
import ulric.li.XLibFactory;
import ulric.li.utils.UtilsLog;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;


/**
 * Created by WangGuiLi
 * on 2018/12/11
 */
public class SplashActivity extends BaseActivity {


    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
//        StatusBarUtils.showBottomMenu(this);

    }

    @OnClick({R.id.bt_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                UtilsLog.statisticsLog("collection", "click_start",null);
                goActivity(WelcomActivity.class);
                finish();
                break;
        }
    }
}
