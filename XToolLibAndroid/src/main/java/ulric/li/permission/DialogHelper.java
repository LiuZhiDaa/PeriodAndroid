package ulric.li.permission;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.webkit.JsResult;

import com.afollestad.materialdialogs.MaterialDialog;

import ulric.li.xlib.R;

/**
 * Created by whl on 2017/9/30.
 * Desc:弹框Dialog工具类
 */

public class DialogHelper {

    private static final String CONFIRM = "确定";
    private static final String CANCEL = "取消";
    private static final String TITLE_DEFAULT = "温馨提示";

    /**
     * 显示一个Dialog
     * 默认title，默认按钮文本，不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showDefault(Context context, String msg) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, false, false, true, null);
    }

    /**
     * 显示一个Dialog
     * 默认title，默认按钮文本，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showDefault(Context context, String msg, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, false, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 默认title，默认按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context    上下文
     * @param msg        提示文本
     * @param cancelAble 返回按钮是否可取消对话框
     */
    public static void showDefault(Context context, String msg, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, false, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 默认title，自定义按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context     上下文
     * @param msg         提示文本
     * @param confirmText 确定按钮文本
     * @param cancelAble  返回按钮是否可取消对话框
     */
    public static void showDefault(Context context, String msg, String confirmText, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, false, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，默认按钮文本，不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleDefault(Context context, String msg) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, true, false, true, null);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，默认按钮文本，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleDefault(Context context, String msg, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, true, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，默认按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context    上下文
     * @param msg        提示文本
     * @param cancelAble 点击返回按钮是否可消失
     */
    public static void showSingleDefault(Context context, String msg, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, CONFIRM, CANCEL, true, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，自定义按钮文本，不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleCustom(Context context, String msg, String confirmText) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, true, false, true, null);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，自定义按钮文本，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleCustom(Context context, String msg, String confirmText, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, true, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 默认title，自定义按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleCustom(Context context, String msg, String confirmText, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, true, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 自定义title，自定义按钮文本，不带监听
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleCustom(Context context, String title, String msg, String confirmText) {
        showSingleCustom(context, title, msg, confirmText, null);
    }

    /**
     * 显示一个Dialog，单个按钮
     * 自定义title，自定义按钮文本，带监听
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    public static void showSingleCustom(Context context, String title, String msg, String confirmText, CallBackInterface callBackInterface) {
        show(context, title, msg, confirmText, CANCEL, true, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 自定义按钮文本，不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context     上下文
     * @param msg         提示文本
     * @param confirmText 确认按钮文本
     * @param cancelText  取消按钮文本
     */
    public static void showCustomBtn(Context context, String msg,
                                     String confirmText, String cancelText) {
        show(context, TITLE_DEFAULT, msg, confirmText, cancelText, false, false, true, null);
    }

    /**
     * 显示一个Dialog
     * 自定义按钮文本，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context           上下文
     * @param msg               提示文本
     * @param confirmText       确认按钮文本
     * @param cancelText        取消按钮文本
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustomBtn(Context context, String msg,
                                     String confirmText, String cancelText,
                                     CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, cancelText, false, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 自定义确定按钮文本，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context           上下文
     * @param msg               提示文本
     * @param confirmText       确认按钮文本
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustomBtn(Context context, String msg,
                                     String confirmText, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, false, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 自定义确定按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context           上下文
     * @param msg               提示文本
     * @param confirmText       确认按钮文本
     * @param cancelAble        点击返回按钮是否可消失
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustomBtn(Context context, String msg,
                                     String confirmText, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, TITLE_DEFAULT, msg, confirmText, CANCEL, false, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 自定义按钮文本，带监听
     * 默认点击对话框外部不消失
     *
     * @param context           上下文
     * @param msg               提示文本
     * @param confirmText       确认按钮文本
     * @param cancelText        取消按钮文本
     * @param cancelAble        点击返回按钮是否可消失
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustomBtn(Context context, String title, String msg,
                                     String confirmText, String cancelText, boolean cancelAble, CallBackInterface callBackInterface) {
        show(context, title, msg, confirmText, cancelText, false, false, cancelAble, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 自定义title,不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context 上下文
     * @param title   标题
     * @param msg     提示文本
     */
    public static void showCustomTitle(Context context, String title, String msg) {
        show(context, title, msg, CONFIRM, CANCEL, false, false, true, null);
    }

    /**
     * 显示一个Dialog
     * 自定义title,带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context           上下文
     * @param title             标题
     * @param msg               提示文本
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustomTitle(Context context, String title, String msg, CallBackInterface callBackInterface) {
        show(context, title, msg, CONFIRM, CANCEL, false, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 标题和按钮文本均自定义，不带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context     上下文
     * @param title       标题
     * @param msg         提示文本
     * @param confirmText 确认按钮文本
     * @param cancelText  取消按钮文本
     */
    public static void showCustom(Context context, String title, String msg,
                                  String confirmText, String cancelText) {
        show(context, title, msg, confirmText, cancelText, false, false, true, null);
    }

    /**
     * 显示一个Dialog
     * 标题和按钮文本均自定义，带监听
     * 默认点击对话框外部不消失，点击返回按钮可消失
     *
     * @param context           上下文
     * @param title             标题
     * @param msg               提示文本
     * @param confirmText       确认按钮文本
     * @param cancelText        取消按钮文本
     * @param callBackInterface 点击按钮回调
     */
    public static void showCustom(Context context, String title, String msg,
                                  String confirmText, String cancelText, CallBackInterface callBackInterface) {
        show(context, title, msg, confirmText, cancelText, false, false, true, callBackInterface);
    }

    /**
     * 显示一个Dialog
     * 如果上述的公有方法传参不能满足需求，请根据需求自定义
     *
     * @param context            上下文
     * @param title              标题
     * @param msg                提示文本
     * @param confirmText        确认按钮文本
     * @param cancelText         取消按钮文本
     * @param isSingleBtn        是否只有一个按钮（确认按钮）
     * @param touchOutSideCancel 触摸外部是否消失对话框
     * @param cancelAble         点击返回是否可以取消对话框
     * @param callBackInterface  点击按钮回调
     */
    private static void show(Context context, String title, String msg,
                             String confirmText, String cancelText, boolean isSingleBtn,
                             boolean touchOutSideCancel, boolean cancelAble,
                             CallBackInterface callBackInterface) {
//        if (context == null || AppUtils.isContextDestroyed(context)) return;
//        new CustomDialog.Builder(context).setMessage(title + "\n\n" + msg).setConfirmText(confirmText).setCancelText(cancelText)
//                .setSingleBtn(isSingleBtn).touchOutSideCancel(touchOutSideCancel).cancelAble(cancelAble).callBack(callBackInterface).createDialog().show();

        FragmentActivity activity;
        if (!(context instanceof FragmentActivity)) {
            return;
        } else {
            activity = (FragmentActivity) context;
            if (activity.isDestroyed()) {
                return;
            }
        }
        //创建MaterialDialog
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(msg);
        builder.positiveText(confirmText)
                .positiveColorRes(R.color.black1)
                .onPositive((dialog, which) -> {
                    if (callBackInterface != null) callBackInterface.callback(true);
                    dialog.dismiss();
                });
        if (!isSingleBtn) {
            builder.negativeText(cancelText)
                    .negativeColorRes(R.color.gray1)
                    .onNegative((dialog, which) -> {
                        if (callBackInterface != null) callBackInterface.callback(false);
                        dialog.dismiss();
                    });
        }
        builder.cancelable(cancelAble);
        builder.canceledOnTouchOutside(touchOutSideCancel);
        MaterialDialog dialog = builder.build();
        XDialogFragment dialogFragment = new XDialogFragment();
        dialogFragment.setDialog(dialog);
        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        dialogFragment.show(fragmentManager,"permission");

    }

    /**
     * 弹出webView alert警告弹框
     *
     * @param context
     * @param message
     * @param jsResult
     */
    public static void showWebAlertDialog(Context context, String message, JsResult jsResult) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title("温馨提示");
        builder.content(message);
        builder.positiveText("确定");
        builder.onPositive((dialog, which) -> {
            jsResult.confirm();//这里必须调用，否则页面会阻塞造成假死
        });
        builder.cancelListener(dialog -> jsResult.cancel());
        builder.show();
    }

}
