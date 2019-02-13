package ulric.li.permission;

import android.Manifest;
import android.content.Context;

import com.yanzhenjie.permission.AndPermission;

import java.util.List;

/**
 * Created by wanghailong on 2018/3/30.
 * 权限管理助手
 */

public class PermissionHelper {

    private Context context;
    private PermissionRationale mRationale;
    private PermissionSetting mSetting;

    private PermissionHelper(Context context) {
        this.context = context;
        mRationale = new PermissionRationale();
        mSetting = new PermissionSetting(context);
    }

    public static PermissionHelper getInstance(Context context) {
        return new PermissionHelper(context);
    }

    public abstract static class PermissionGrantedCallback {
        public void onGranted(List<String> permissions) {
        }

        public boolean onBaseDenied(List<String> permissions, PermissionRationale rationale) {
            return false;
        }

        public void onDenied(List<String> permissions, PermissionRationale rationale) {

        }

        public void onCancelWarningDialog() {

        }

        public void onOpenSettingPage() {

        }
    }

    public void requestPermission(String[] permissions, final PermissionGrantedCallback callback) {
        if (context == null || mRationale == null || mSetting == null) return;
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .onGranted(permissions1 -> {
                    if (callback != null) {
                        callback.onGranted(permissions1);
                    }

                })
                .onDenied(permissions12 -> {
                    if (callback != null&&callback.onBaseDenied(permissions12,mRationale)) {
                        return;
                    }
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions12)) {
                        mSetting.showSetting(permissions12,callback);
                    } else {
                        if (callback != null) {
                            callback.onDenied(permissions12, mRationale);
                        }
                    }
                })
                .start();

    }

    public static void requestAdPermission(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE}, callback);
    }

    /**
     * 获取手机状态权限--读取imei等
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionPhoneState(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.READ_PHONE_STATE}, callback);
    }

    /**
     * 获取相机权限--拍照等
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionCamera(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.CAMERA}, callback);
    }

    /**
     * 获取定位权限--获取地理位置
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionLocation(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, callback);
    }


    /**
     * 获取读写文件权限
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionFile(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//写文件权限
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                callback);
    }

    /**
     * 获取相机和读写文件权限，用于拍照并存储照片场景
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionCameraFile(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//写文件权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,//读文件权限
                        Manifest.permission.CAMERA},//相机
                callback);
    }


    /**
     * 获取定位、获取imei权限(这两个权限使用场景较多，同时申请)
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionLocationAndPhone(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,//定位权限
                        Manifest.permission.ACCESS_FINE_LOCATION,//定位权限
                        Manifest.permission.READ_PHONE_STATE},//读取手机状态
                callback);
    }


    /**
     * 获取通讯录权限--读取通讯录
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionContacts(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.READ_CONTACTS}, callback);
    }

    /**
     * 获取短信权限--读取短信
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionSms(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.READ_SMS}, callback);
    }

    /**
     * 获取通话记录权限--读取通话记录
     *
     * @param context
     * @param callback
     */
    public static void requestPermissionCallLog(Context context, PermissionGrantedCallback callback) {
        PermissionHelper.getInstance(context).requestPermission(new String[]{
                Manifest.permission.READ_CALL_LOG}, callback);
    }

}
