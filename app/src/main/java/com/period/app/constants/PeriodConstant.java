package com.period.app.constants;

/**
 * Created by WangGuiLi
 * on 2018/12/21
 */
public class PeriodConstant {
    public static final long VALUE_LONG_ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static final int VALUE_INT_UNSET_OPEN_TIME =1000;

    public static final int VALUE_INT_STATE_NO_DATA = -1;                                      //无数据
    public static final int VALUE_INT_STATE_PERIOD = 1;                                        //月经期
    public static final int VALUE_INT_STATE_OVULATION = 2;                                     //排卵期
    public static final int VALUE_INT_STATE_OVULATION_DAY = 3;                                //排卵日
    public static final int VALUE_INT_STATE_SAFETY = 4;                                        //安全期


    public static final int VALUE_INT_OPERATION_MENSTRUATION_START =0;
    public static final int VALUE_INT_OPERATION_MENSTRUATION_END =1;
    public static final int VALUE_INT_OPERATION_MENSTRUATION =2;
    public static final int VALUE_INT_OPERATION_SAFETY =3;
    public static final int VALUE_INT_OPERATION_PREGNANCY_PROBABILITY =4;
    public static final int VALUE_INT_OPERATION_UNKNOWN =5;                                   //未确定的状态，比如将今天设置为了经期开始日


}
