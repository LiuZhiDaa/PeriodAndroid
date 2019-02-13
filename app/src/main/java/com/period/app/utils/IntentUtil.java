package com.period.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.period.app.Constants;
import com.yanzhenjie.permission.FileProvider;

import java.io.File;

import ulric.li.utils.UtilsMediaStore;

import static ulric.li.utils.UtilsFile.isDirectory;
import static ulric.li.utils.UtilsFile.isExists;


/**
 * Create by XuChuanting
 * on 2018/9/28-19:09
 */
public class IntentUtil {

    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    public static boolean openFile(Context context, String strPath) {
        try {
            if (null == context || TextUtils.isEmpty(strPath))
                return false;

            if (!isExists(strPath) || isDirectory(strPath))
                return false;
            Intent intent = new Intent();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context,
                        "com.superapp.filemanager.fileprovider",
                        new File(strPath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                uri = Uri.parse("file://" + strPath);
            }
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String strMimeType = UtilsMediaStore.getMimeType(strPath);
            if (TextUtils.isEmpty(strMimeType))
                return false;

            intent.setDataAndType(uri, strMimeType);
            context.startActivity(intent);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void openBrowse(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }catch (Exception e){
        }
    }

    /**
     * 给该app打分
     *
     * @param context
     */
    public static void gradeApp(Context context) {
        try {
            //欢迎评分
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception e){
        }
    }

    /**
     * 打开app设置详情页面
     *
     * @param context
     * @param packageName
     */
    public static void openAppDetails(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", packageName, null));
            context.startActivity(intent);
        }catch (Exception e){
        }
    }

    public static void openPic(Context context,String path){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //进行图片-->bitmap-->uri转换
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qiaoba);
        //系统提供了现成的API让用户可以将自己喜欢的图片保存到系统相册中.
//        String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,null,null);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri,"image/*");
        context.startActivity(intent);
    }

    /**
     * 通知系统扫描指定文件
     * @param context
     * @param filePath
     */
    public static void scanFileAsync(Context context,String filePath){
        try {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(filePath)));
            context.sendBroadcast(scanIntent);
        }catch (Exception e){
        }

//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+filePath)));

//        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//                File f = new File(path);
//            }
//        });
    }

    public static void toEmail(Context context){
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:"+Constants.VALUE_STRING_FEEDBACK_EMAIL));
        context.startActivity(data);

    }


    /**
     * 通知系统扫描指定目录
     * @param context
     * @param dir
     */
    public static void scanDirAsync(Context context, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        context.sendBroadcast(scanIntent);

//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+dir)));
    }


}
