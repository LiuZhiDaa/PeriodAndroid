package ulric.li.xui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ulric.li.utils.UtilsApp;

public class UtilsImageLoader {
    private static final int VALUE_INT_DISK_CACHE_SIZE = 1024 * 1024 * 100;
    private static final int VALUE_INT_CONNECT_TIMEOUT = 1000 * 5;
    private static final int VALUE_INT_READ_TIMEOUT = 1000 * 5;

    public static final String SCHEMA_APP = "app";
    public static final String SCHEMA_APK = "apk";

    public static void init(Context context, int nResDefaultImageID) {
        if (null == context)
            return;

        DisplayImageOptions option = new DisplayImageOptions.Builder()
                .showImageOnLoading(nResDefaultImageID)
                .showImageForEmptyUri(nResDefaultImageID)
                .showImageOnFail(nResDefaultImageID)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(option)
                .memoryCacheSize((int) Runtime.getRuntime().maxMemory() / 4)
                .diskCacheSize(VALUE_INT_DISK_CACHE_SIZE)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new CustomImageDownloader(context, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT))
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static ImageLoader getInstance() {
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getDisplayImageOptions(int nResDefaultImageID) {
        DisplayImageOptions option = new DisplayImageOptions.Builder()
                .showImageOnLoading(nResDefaultImageID)
                .showImageForEmptyUri(nResDefaultImageID)
                .showImageOnFail(nResDefaultImageID)
                .resetViewBeforeLoading(false)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return option;
    }

    public static String getDiskCachePath() {
        File file = ImageLoader.getInstance().getDiskCache().getDirectory();
        if (null == file)
            return null;

        return file.getAbsolutePath();
    }

    public static String getDiskCachePath(String strUri) {
        if (TextUtils.isEmpty(strUri))
            return null;

        File file = ImageLoader.getInstance().getDiskCache().get(strUri);
        if (null == file)
            return null;

        return file.getAbsolutePath();
    }

    public static boolean saveFileToDiskCache(Context context, String strUri, String strFilePath) {
        if (null == context || TextUtils.isEmpty(strUri) || TextUtils.isEmpty(strFilePath))
            return false;

        return saveBitmapToDiskCache(strUri, UtilsImage.getBitmap(context, strFilePath));
    }

    public static boolean saveBitmapToDiskCache(String strUri, Bitmap bitmap) {
        if (TextUtils.isEmpty(strUri) || null == bitmap)
            return false;

        try {
            return ImageLoader.getInstance().getDiskCache().save(strUri, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void clearCache(String strUri) {
        if (TextUtils.isEmpty(strUri))
            return;

        MemoryCacheUtils.removeFromCache(strUri, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(strUri, ImageLoader.getInstance().getDiskCache());
    }

    public static void clearMemoryCache(String strUri) {
        if (TextUtils.isEmpty(strUri))
            return;

        MemoryCacheUtils.removeFromCache(strUri, ImageLoader.getInstance().getMemoryCache());
    }

    public static void clearDiskCache(String strUri) {
        if (TextUtils.isEmpty(strUri))
            return;

        DiskCacheUtils.removeFromCache(strUri, ImageLoader.getInstance().getDiskCache());
    }

    public static void clearCache() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    public static void clearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    public static void clearDiskCache() {
        ImageLoader.getInstance().clearDiskCache();
    }
}

class CustomImageDownloader extends BaseImageDownloader {
    private Context mContext = null;

    public CustomImageDownloader(Context context) {
        super(context);
        mContext = context;
        _init();
    }

    public CustomImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
        mContext = context;
        _init();
    }

    private void _init() {
    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        Uri uri = Uri.parse(imageUri);
        if (UtilsImageLoader.SCHEMA_APP.equals(uri.getScheme())) {
            String strPackageName = uri.getPath();
            Drawable drawable = UtilsApp.getAppIcon(mContext, strPackageName);
            if (null == drawable)
                return null;

            Bitmap bitmap = UtilsImage.drawableToBitmap(mContext, drawable);
            if (null == bitmap)
                return null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } else if (UtilsImageLoader.SCHEMA_APK.equals(uri.getScheme())) {
            String strApkPath = uri.getPath();
            Drawable drawable = UtilsApp.getApkFileIcon(mContext, strApkPath);
            if (null == drawable)
                return null;

            Bitmap bitmap = UtilsImage.drawableToBitmap(mContext, drawable);
            if (null == bitmap)
                return null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return new ByteArrayInputStream(baos.toByteArray());
        }

        return null;
    }
}
