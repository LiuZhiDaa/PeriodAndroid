package com.period.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.period.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ulric.li.xui.utils.UtilsSize;

/**
 * @author XuChuanting
 * on 2018/11/27-11:14
 */
public class CustomDialog extends Dialog implements IResultGetter, View.OnClickListener {

//    @BindView(R.id.fl_content)
    FrameLayout flContent;
//    @BindView(R.id.btn_cancel)
    Button btnCancel;
//    @BindView(R.id.btn_save)
    Button btnSave;
//    @BindView(R.id.tv_title)
    TextView tvTitle;
    private View mCustomView;
    private CharSequence mTitle;

    private View.OnClickListener mOnClickListenerCancel;
    private View.OnClickListener mOnClickListenerSave;


    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog setOnClickListenerCancel(View.OnClickListener onClickListenerCancel) {
        mOnClickListenerCancel = onClickListenerCancel;
        return this;
    }
    public CustomDialog setOnClickListenerSave(View.OnClickListener onClickListenerSave) {
        mOnClickListenerSave = onClickListenerSave;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Window window = getWindow();
        Context context = getContext();
        View contentView = View.inflate(context, R.layout.dialog_common, null);
        flContent = contentView.findViewById(R.id.fl_content);
        btnCancel = contentView.findViewById(R.id.btn_cancel);
        btnSave = contentView.findViewById(R.id.btn_save);
        tvTitle = contentView.findViewById(R.id.tv_title);

        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
//            window.setWindowAnimations(R.style.Animation_AppCompat_Dialog_Custom);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_notification_dialog));
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = (int) (UtilsSize.getScreenWidth(context) * 0.88f);
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        setContentView(contentView);
//        ButterKnife.bind(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        if (mCustomView != null) {
            ViewGroup.LayoutParams layoutParams = mCustomView.getLayoutParams();
            if (layoutParams != null) {
                flContent.addView(mCustomView, layoutParams);
            } else {
                flContent.addView(mCustomView);
            }
        }

        if (mTitle != null) {
            if (mTitle.equals("")){
                tvTitle.setVisibility(View.GONE);
            }else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(mTitle);
            }

        }
    }

    public void setCustomView(View view) {
        this.mCustomView = view;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
    }


    @Override
    public String[] getResult() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (mOnClickListenerCancel != null) {
                    mOnClickListenerCancel.onClick(v);
                }
                break;
            case R.id.btn_save:
                if (mOnClickListenerSave != null) {
                    mOnClickListenerSave.onClick(v);
                }
                break;
        }
        dismiss();
    }
}
