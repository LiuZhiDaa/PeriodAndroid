package com.period.app.core.clock;

import android.app.AlarmManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author XuChuanting
 * on 2018/11/22-16:00
 */
public class AlarmClockInfo implements Parcelable {


    public int mType;
    public long mTriggerAtMillis;
    public long mIntervalMillis;
    public boolean mRepeat;
    private int requestCode;
    private String dataJson;

    private AlarmClockInfo() {

    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getType() {
        return mType;
    }

    public long getTriggerAtMillis() {
        return mTriggerAtMillis;
    }

    public long getIntervalMillis() {
        return mIntervalMillis;
    }

    public boolean isRepeat() {
        return mRepeat;
    }


    public void setTriggerAtMillis(long triggerAtMillis) {
        mTriggerAtMillis = triggerAtMillis;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setIntervalMillis(long intervalMillis) {
        mIntervalMillis = intervalMillis;
    }

    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }


    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public String toString() {
        return "mTriggerTime:" + mTriggerAtMillis + ",mRepeat:" + mRepeat + "xxxxx";
    }



    public static class Builder {
        private int mType = AlarmManager.RTC_WAKEUP;
        private long mTriggerAtMillis;
        private long mIntervalMillis;
        private boolean mRepeat;
        private int requestCode;
        private String dataJson;

        public String getDataJson() {
            return dataJson;
        }

        public Builder setDataJson(String dataJson) {
            this.dataJson = dataJson;
            return this;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public AlarmClockInfo build() {
            AlarmClockInfo alarmClockInfo = new AlarmClockInfo();
            alarmClockInfo.mType = this.mType;
            alarmClockInfo.mTriggerAtMillis = this.mTriggerAtMillis;
            alarmClockInfo.mIntervalMillis = this.mIntervalMillis;
            alarmClockInfo.mRepeat = this.mRepeat;
            alarmClockInfo.requestCode = this.requestCode;
            alarmClockInfo.dataJson = this.dataJson;
            return alarmClockInfo;
        }

        public int getType() {
            return mType;
        }

        public Builder setType(int type) {
            mType = type;
            return this;
        }

        public long getTriggerAtMillis() {
            return mTriggerAtMillis;
        }

        public Builder setTriggerAtMillis(long triggerAtMillis) {
            mTriggerAtMillis = triggerAtMillis;
            return this;
        }

        public long getIntervalMillis() {
            return mIntervalMillis;
        }

        public Builder setIntervalMillis(long intervalMillis) {
            mIntervalMillis = intervalMillis;
            if (intervalMillis > 0) {
                setRepeat(true);
            }
            return this;
        }

        public boolean isRepeat() {
            return mRepeat;
        }

        public Builder setRepeat(boolean repeat) {
            mRepeat = repeat;
            return this;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeLong(this.mTriggerAtMillis);
        dest.writeLong(this.mIntervalMillis);
        dest.writeByte(this.mRepeat ? (byte) 1 : (byte) 0);
        dest.writeInt(this.requestCode);
        dest.writeString(this.dataJson);
    }

    protected AlarmClockInfo(Parcel in) {
        this.mType = in.readInt();
        this.mTriggerAtMillis = in.readLong();
        this.mIntervalMillis = in.readLong();
        this.mRepeat = in.readByte() != 0;
        this.requestCode = in.readInt();
        this.dataJson = in.readString();
    }

    public static final Parcelable.Creator<AlarmClockInfo> CREATOR = new Parcelable.Creator<AlarmClockInfo>() {
        @Override
        public AlarmClockInfo createFromParcel(Parcel source) {
            return new AlarmClockInfo(source);
        }

        @Override
        public AlarmClockInfo[] newArray(int size) {
            return new AlarmClockInfo[size];
        }
    };
}
