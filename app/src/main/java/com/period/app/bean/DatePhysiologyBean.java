package com.period.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


@Entity
public class DatePhysiologyBean {


    @Id(autoincrement = true)
    private long currentDate;
    //0 预测经期 1 经期  2 安全期  3 排卵期   4 排卵日
    private int currentState;
    //处于某一状态的第几天
    private int mIndexofPeriod;
    private boolean isPeriodStart;
    private boolean isPeriodEnd;



    @Generated(hash = 1101751493)
    public DatePhysiologyBean(long currentDate, int currentState,
            int mIndexofPeriod, boolean isPeriodStart, boolean isPeriodEnd) {
        this.currentDate = currentDate;
        this.currentState = currentState;
        this.mIndexofPeriod = mIndexofPeriod;
        this.isPeriodStart = isPeriodStart;
        this.isPeriodEnd = isPeriodEnd;
    }

    @Generated(hash = 89169961)
    public DatePhysiologyBean() {
    }

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }


    public int getMIndexofPeriod() {
        return this.mIndexofPeriod;
    }

    public void setMIndexofPeriod(int mIndexofPeriod) {
        this.mIndexofPeriod = mIndexofPeriod;
    }

    public boolean getIsPeriodStart() {
        return this.isPeriodStart;
    }

    public void setIsPeriodStart(boolean isPeriodStart) {
        this.isPeriodStart = isPeriodStart;
    }

    public boolean getIsPeriodEnd() {
        return this.isPeriodEnd;
    }

    public void setIsPeriodEnd(boolean isPeriodEnd) {
        this.isPeriodEnd = isPeriodEnd;
    }
}
