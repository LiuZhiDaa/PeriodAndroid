package com.period.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.period.app.R;

import ulric.li.xui.utils.UtilsSize;

public class LoadingDialog extends Dialog {


    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Context context = getContext();
        View contentView = View.inflate(context, R.layout.loading_alert, null);
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_notification_dialog));
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = (int) (UtilsSize.getScreenWidth(context) * 0.66f);
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        setContentView(contentView);
    }
}
