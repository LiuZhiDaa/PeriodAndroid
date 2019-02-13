package com.period.app.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.period.app.R;
import com.period.app.base.BaseActivity;
import com.period.app.constants.RemindConstant;
import com.period.app.core.XCoreFactory;
import com.period.app.core.calendardialog.ICalendarDialogManger;
import com.period.app.core.config.intf.ICloudConfig;
import com.period.app.core.config.intf.IConfigMgr;
import com.period.app.core.data.IDataMgr;
import com.period.app.core.dba.IDbaManger;
import com.period.app.core.prediction.IPredictionManger;
import com.period.app.core.reminder.IReminderMgr;
import com.period.app.dialog.calendar.TipsDialog;
import com.period.app.utils.CalendarUtil;
import com.period.app.utils.LogUtils;
import com.period.app.utils.NotificationsUtils;
import com.period.app.utils.StatusBarUtils;
import com.period.app.widget.CustomScrollViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    CustomScrollViewPager viewPager;
    long backTime;
    private IDataMgr mgr;
    private IPredictionManger mMIpredictionManger;
    int notifType = -1;
    TimeChangeReceiver timeChangeReceiver;

    private ICloudConfig mICloudConfig;
    private int mClickNum = 0;

    private boolean mReceiverTag = false; //广播接受者标识

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {

        notifType = getIntent().getIntExtra("notification",-1);
        mMIpredictionManger = (IPredictionManger) XCoreFactory.getInstance().createInstance(IPredictionManger.class);
        IReminderMgr rmgr = (IReminderMgr) XCoreFactory.getInstance().createInstance(IReminderMgr.class);

        mICloudConfig = (ICloudConfig) XCoreFactory.getInstance().createInstance(ICloudConfig.class);


        tabLayout.post(() -> rmgr.resetAllRemind());

        final List<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new MainFragment());
        mFragmentList.add(new CalendarFragment());
        mFragmentList.add(new SettingFragment());
        FragmentPagerAdapter fpa = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        viewPager.setAdapter(fpa);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mClickNum ++;

                if (mClickNum > 0 && mClickNum % mICloudConfig.getmClickNum() == 0){


                }


                if (position == 1 || position == 2) {
                    StatusBarUtils.setWindowStatusBarColor(MainActivity.this, R.color.toolBarBgColor);
                }
                if (position==0){
                    LogUtils.TwoFieldLog("bottomGuide","click_main");
                }
                if (position==1){
                    LogUtils.TwoFieldLog("bottomGuide","click_calendar");
                }
                if (position==2){
                    LogUtils.TwoFieldLog("bottomGuide","click_more");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        int[] arrayDrawable = new int[]{R.drawable.sel_main, R.drawable.sel_calendar, R.drawable.sel_setting};
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(arrayDrawable[i]);
            }
        }
        if (!mReceiverTag){
            mReceiverTag = true;//标识值 赋值为 true 表示广播已被注册
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.TIME_TICK");
            timeChangeReceiver=new TimeChangeReceiver();
            registerReceiver(timeChangeReceiver,filter);
        }
        IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
        iDataMgr.setEveryReceiverTime(CalendarUtil.getZeroDate(System.currentTimeMillis()));

        if (!NotificationsUtils.isNotificationEnabled(this)){
            TipsDialog tipsDialog = new TipsDialog(this, getString(R.string.string_nopermissions));
            tipsDialog.setOnClickListenerSave(v1 -> {
                tipsDialog.cancel();
            });
            tipsDialog.show();
        }


    }
    @Override
    public void onBackPressed() {
        long secTime = System.currentTimeMillis();
        if (secTime - backTime > 2000) {
            backTime = secTime;
            Toast.makeText(this, getString(R.string.main_exit), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag){
            mReceiverTag = false; //Tag值 赋值为false 表示该广播已被注销
            unregisterReceiver(timeChangeReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        notifType = intent.getIntExtra("notification",-1);
        log();
    }

    private void log(){
        switch (notifType){
            case RemindConstant.VAULE_INT_TYPE_MEN_START:
                LogUtils.reminderLog("click_menstruationStartReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_MEN_END:
                LogUtils.reminderLog("click_menstruationEndReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_START:
                LogUtils.reminderLog("click_ovulationStartReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_DAY:
                LogUtils.reminderLog("click_ovulationDaytReminder");
                break;
            case RemindConstant.VAULE_INT_TYPE_OVULATION_END:
                LogUtils.reminderLog("click_ovulationEndReminder");
                break;

        }
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //每过一分钟 触发
                    IDataMgr iDataMgr = (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
                    ICalendarDialogManger iCalendarDialogManger = (ICalendarDialogManger)XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
                    if (CalendarUtil.getZeroDate(System.currentTimeMillis())-iDataMgr.getEveryReceiverTime()>0){
                        IDbaManger iDbaManger = (IDbaManger) XCoreFactory.getInstance().createInstance(IDbaManger.class);
                        iDbaManger.supplyData();
                        iDataMgr.setEveryReceiverTime(CalendarUtil.getZeroDate(System.currentTimeMillis()));
                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        calendar.set(java.util.Calendar.MINUTE, 0);
                        calendar.set(java.util.Calendar.SECOND, 0);
                        calendar.set(java.util.Calendar.MILLISECOND, 0);
                        calendar.set(java.util.Calendar.YEAR, CalendarUtil.year);
                        calendar.set(java.util.Calendar.MONTH, CalendarUtil.month);
                        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                        iCalendarDialogManger.update(calendar.getTimeInMillis(),false);
                    }

                    break;

            }
        }
    }





    private String getSceneName(){

        if (mClickNum == 0){
            return "main_launch";
        }else{
            if (mClickNum % mICloudConfig.getmClickNum() == 0){
                return "main";
            }else{
                return "";
            }
        }

    }
}
