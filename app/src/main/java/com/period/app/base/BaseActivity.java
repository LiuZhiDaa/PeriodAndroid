package com.period.app.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ulric.li.utils.UtilsLog;

public abstract class BaseActivity extends  BaseAdActivity {

    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (null != savedInstanceState) {
//            Intent intent = new Intent(this, SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return;
//        }
        setContentView(getLayoutRes());
        mBind = ButterKnife.bind(this);
        init();
    }

    public abstract int getLayoutRes();

    public abstract void init();

    protected void setToolBar(Toolbar toolbar, boolean showNavigationIcon) {
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (showNavigationIcon) {
//            toolbar.setNavigationIcon(R.drawable.icon_toolbar_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNavigationClick();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilsLog.sendLog();
        mBind.unbind();
    }


    protected void onNavigationClick() {
        finish();
    }


    protected void goActivity(Class<? extends Activity> clazz){
        startActivity(new Intent(this,clazz));
    }

}
