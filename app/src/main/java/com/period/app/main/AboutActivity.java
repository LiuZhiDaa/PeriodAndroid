package com.period.app.main;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import com.period.app.R;
import com.period.app.base.BaseActivity;

import ulric.li.utils.UtilsApp;

public class AboutActivity extends BaseActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    private TextView mTVAppVersion = null;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_setting_about;
    }

    @Override
    public void init() {
        mTVAppVersion = findViewById(R.id.tv_app_version);
        mTVAppVersion.setText("V"+UtilsApp.getMyAppVersionName(this));
    }
}