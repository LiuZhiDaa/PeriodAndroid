package com.period.app.core.main;

public class MainMgr implements IMainMgr {

    public MainMgr(){

    }

    /***
     *
     * 1，如果在月经期内，就根据月经开始时间计算月经第几天
     * 2，如果不在月经期内，先判断是不是在安全期，
     * 如果在安全期，根据月经结束时间判断是第一段安全期还是第二段安全期。
     * 如果第一段安全期，根据当前
     * @return
     */
    @Override
    public String getMainData() {
        String text = "";




        return text;
    }
}
