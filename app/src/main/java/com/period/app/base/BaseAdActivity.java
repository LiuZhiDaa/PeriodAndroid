package com.period.app.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import ulric.li.utils.UtilsLog;

/**
 * Created by WangYu on 2018/8/17.
 */
public abstract class BaseAdActivity extends AppCompatActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void onStop() {
        super.onStop();
        UtilsLog.sendLog();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
