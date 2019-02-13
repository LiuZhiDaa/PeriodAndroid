//package com.period.app.receivers;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.period.app.core.XCoreFactory;
//import com.period.app.core.calendardialog.ICalendarDialogManger;
//import com.period.app.core.data.IDataMgr;
//import com.period.app.core.prediction.IPredictionManger;
//import com.period.app.utils.CalendarUtil;
//
///**
// * Created by WangGuiLi
// * on 2019/1/2
// */
//public class DateChangedReceiver extends BroadcastReceiver{
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        IDataMgr iDataMgr= (IDataMgr) XCoreFactory.getInstance().createInstance(IDataMgr.class);
//        ICalendarDialogManger iCalendarDialogManger = (ICalendarDialogManger)XCoreFactory.getInstance().createInstance(ICalendarDialogManger.class);
//        if(true){
//            java.util.Calendar calendar = java.util.Calendar.getInstance();
//            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
//            calendar.set(java.util.Calendar.MINUTE, 0);
//            calendar.set(java.util.Calendar.SECOND, 0);
//            calendar.set(java.util.Calendar.MILLISECOND, 0);
//            calendar.set(java.util.Calendar.YEAR, CalendarUtil.year);
//            calendar.set(java.util.Calendar.MONTH, CalendarUtil.month);
//            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
//            iCalendarDialogManger.update(calendar.getTimeInMillis());
//        }
//    }
//}
