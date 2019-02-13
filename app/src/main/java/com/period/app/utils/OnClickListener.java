package com.period.app.utils;

import android.view.View;


public abstract class OnClickListener implements View.OnClickListener {
    private long mExitTime = 0;

    @Override
    public void onClick(View view) {
        if ((System.currentTimeMillis() - mExitTime) > 0) {
            mExitTime = System.currentTimeMillis();
            myOnClickListener(view);
        }
    }

    protected abstract void myOnClickListener(View v);
}
