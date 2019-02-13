package ulric.li.xui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

import ulric.li.utils.UtilsFile;

public class UtilsImage {
    public static final int VALUE_INT_COMPRESS_1080_SIZE = 1080;
    public static final int VALUE_INT_COMPRESS_960_SIZE = 960;
    public static final int VALUE_INT_COMPRESS_840_SIZE = 840;
    public static final int VALUE_INT_COMPRESS_720_SIZE = 720;
    public static final int VALUE_INT_COMPRESS_600_SIZE = 600;
    public static final int VALUE_INT_COMPRESS_480_SIZE = 480;
    public static final int VALUE_INT_COMPRESS_360_SIZE = 360;

    public static final int VALUE_INT_COMPRESS_SIZE = VALUE_INT_COMPRESS_720_SIZE;
    public static final int VALUE_INT_COMPRESS_QUALITY = 80;

    public static Bitmap compressBitmapScaleX(Context context, String strFilePath, int nCompressWidth) {
        if (null == context || TextUtils.isEmpty(strFilePath))
            return null;

        return compressBitmapScaleX(context, getBitmap(context, strFilePath), nCompressWidth);
    }

    public static Bitmap compressBitmapScaleX(Context context, Bitmap bitmap, int nCompressWidth) {
        if (null == context || null == bitmap)
            return bitmap;

        int nWidth = bitmap.getWidth();
        int nHeight = bitmap.getHeight();
        if (0 == nWidth || 0 == nHeight || nWidth <= nCompressWidth)
            return bitmap;

        float fScale = (float) nCompressWidth / nWidth;
        nWidth = Math.round(nWidth * fScale);
        nHeight = Math.round(nHeight * fScale);
        return ThumbnailUtils.extractThumbnail(bitmap, nWidth, nHeight);
    }

    public static boolean compressBitmapToFileScaleX(Context context, String strFileSourcePath, String strFileDestinationPath, int nCompressWidth, Bitmap.CompressFormat compressFormat) {
        if (null == context || TextUtils.isEmpty(strFileSourcePath) || TextUtils.isEmpty(strFileDestinationPath))
            return false;

        Bitmap bitmap = getBitmap(context, strFileSourcePath);
        if (null == bitmap)
            return false;

        return compressBitmapToFileScaleX(context, bitmap, strFileDestinationPath, nCompressWidth, compressFormat);
    }

    public static boolean compressBitmapToFileScaleX(Context context, Bitmap bitmap, String strFilePath, int nCompressWidth, Bitmap.CompressFormat compressFormat) {
        if (null == context || null == bitmap || TextUtils.isEmpty(strFilePath))
            return false;

        Bitmap bitmapCompress = compressBitmapScaleX(context, bitmap, nCompressWidth);
        if (null == bitmapCompress)
            return false;

        try {
            File file = new File(strFilePath);
            FileOutputStream fos = new FileOutputStream(file);
            return bitmapCompress.compress(compressFormat, VALUE_INT_COMPRESS_QUALITY, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Bitmap compressBitmapCenterInside(Context context, String strFilePath, int nCompressWidth, int nCompressHeight) {
        if (null == context || TextUtils.isEmpty(strFilePath))
            return null;

        return compressBitmapCenterInside(context, getBitmap(context, strFilePath), nCompressWidth, nCompressHeight);
    }

    public static Bitmap compressBitmapCenterInside(Context context, Bitmap bitmap, int nCompressWidth, int nCompressHeight) {
        if (null == context || null == bitmap)
            return bitmap;

        int nWidth = bitmap.getWidth();
        int nHeight = bitmap.getHeight();
        if (0 == nWidth || 0 == nHeight || nWidth <= nCompressWidth && nHeight <= nCompressHeight)
            return bitmap;

        float fScale = 1;
        if (nWidth > nCompressWidth && nHeight < nCompressHeight) {
            fScale = (float) nCompressWidth / nWidth;
        } else if (nWidth < nCompressWidth && nHeight > nCompressHeight) {
            fScale = (float) nCompressHeight / nHeight;
        } else if (nWidth > nCompressWidth && nHeight > nCompressHeight) {
            float scaleW = (float) nCompressWidth / nWidth;
            float scaleH = (float) nCompressHeight / nHeight;
            fScale = Math.min(scaleW, scaleH);
        }

        if (nWidth < nCompressWidth && nHeight > nCompressHeight) {
            fScale = (float) nCompressHeight / nHeight;
        } else if (nWidth > nCompressWidth && nHeight < nCompressHeight) {
            fScale = (float) nCompressWidth / nWidth;
        } else if (nWidth < nCompressWidth && nHeight < nCompressHeight) {
            float scaleW = (float) nCompressWidth / nWidth;
            float scaleH = (float) nCompressHeight / nHeight;
            fScale = Math.min(scaleW, scaleH);
        }

        nWidth = Math.round(nWidth * fScale);
        nHeight = Math.round(nHeight * fScale);
        nWidth = nWidth > nCompressWidth ? nCompressWidth : nWidth;
        nHeight = nHeight > nCompressHeight ? nCompressHeight : nHeight;
        return ThumbnailUtils.extractThumbnail(bitmap, nWidth, nHeight);
    }

    public static boolean compressBitmapToFileCenterInside(Context context, String strFileSourcePath, String strFileDestinationPath, int nCompressWidth, int nCompressHeight, Bitmap.CompressFormat compressFormat) {
        if (null == context || TextUtils.isEmpty(strFileSourcePath) || TextUtils.isEmpty(strFileDestinationPath))
            return false;

        Bitmap bitmap = getBitmap(context, strFileSourcePath);
        if (null == bitmap)
            return false;

        return compressBitmapToFileCenterInside(context, bitmap, strFileDestinationPath, nCompressWidth, nCompressHeight, compressFormat);
    }

    public static boolean compressBitmapToFileCenterInside(Context context, Bitmap bitmap, String strFilePath, int nCompressWidth, int nCompressHeight, Bitmap.CompressFormat compressFormat) {
        if (null == context || null == bitmap || TextUtils.isEmpty(strFilePath))
            return false;

        Bitmap bitmapCompress = compressBitmapCenterInside(context, bitmap, nCompressWidth, nCompressHeight);
        if (null == bitmapCompress)
            return false;

        try {
            File file = new File(strFilePath);
            FileOutputStream fos = new FileOutputStream(file);
            return bitmapCompress.compress(compressFormat, VALUE_INT_COMPRESS_QUALITY, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Bitmap compressBitmapCenterCrop(Context context, String strFilePath, int nCompressWidth, int nCompressHeight) {
        if (null == context || TextUtils.isEmpty(strFilePath))
            return null;

        return compressBitmapCenterCrop(context, getBitmap(context, strFilePath), nCompressWidth, nCompressHeight);
    }

    public static Bitmap compressBitmapCenterCrop(Context context, Bitmap bitmap, int nCompressWidth, int nCompressHeight) {
        if (null == context || null == bitmap)
            return bitmap;

        int nWidth = bitmap.getWidth();
        int nHeight = bitmap.getHeight();
        if (0 == nWidth || 0 == nHeight || nWidth <= nCompressWidth && nHeight <= nCompressHeight)
            return bitmap;

        float fScale = 1;
        if (nWidth > nCompressWidth && nHeight < nCompressHeight) {
            fScale = (float) nCompressHeight / nHeight;
        } else if (nWidth < nCompressWidth && nHeight > nCompressHeight) {
            fScale = (float) nCompressWidth / nWidth;
        } else if (nWidth > nCompressWidth && nHeight > nCompressHeight) {
            float scaleW = (float) nCompressWidth / nWidth;
            float scaleH = (float) nCompressHeight / nHeight;
            fScale = Math.max(scaleW, scaleH);
        }

        if (nWidth < nCompressWidth && nHeight > nCompressHeight) {
            fScale = (float) nCompressWidth / nWidth;
        } else if (nWidth > nCompressWidth && nHeight < nCompressHeight) {
            fScale = (float) nCompressHeight / nHeight;
        } else if (nWidth < nCompressWidth && nHeight < nCompressHeight) {
            float scaleW = (float) nCompressWidth / nWidth;
            float scaleH = (float) nCompressHeight / nHeight;
            fScale = Math.max(scaleW, scaleH);
        }

        nWidth = Math.round(nWidth * fScale);
        nHeight = Math.round(nHeight * fScale);
        nWidth = nWidth < nCompressWidth ? nCompressWidth : nWidth;
        nHeight = nHeight < nCompressHeight ? nCompressHeight : nHeight;
        Bitmap bitmapScale = ThumbnailUtils.extractThumbnail(bitmap, nWidth, nHeight);
        if (null == bitmapScale)
            return bitmap;

        return Bitmap.createBitmap(bitmapScale, Math.abs(bitmapScale.getWidth() - nCompressWidth) / 2, Math.abs(bitmapScale.getHeight() - nCompressHeight) / 2, nCompressWidth, nCompressHeight);
    }

    public static boolean compressBitmapToFileCenterCrop(Context context, String strFileSourcePath, String strFileDestinationPath, int nCompressWidth, int nCompressHeight, Bitmap.CompressFormat compressFormat) {
        if (null == context || TextUtils.isEmpty(strFileSourcePath) || TextUtils.isEmpty(strFileDestinationPath))
            return false;

        Bitmap bitmap = getBitmap(context, strFileSourcePath);
        if (null == bitmap)
            return false;

        return compressBitmapToFileCenterCrop(context, bitmap, strFileDestinationPath, nCompressWidth, nCompressHeight, compressFormat);
    }

    public static boolean compressBitmapToFileCenterCrop(Context context, Bitmap bitmap, String strFilePath, int nCompressWidth, int nCompressHeight, Bitmap.CompressFormat compressFormat) {
        if (null == context || null == bitmap || TextUtils.isEmpty(strFilePath))
            return false;

        Bitmap bitmapCompress = compressBitmapCenterCrop(context, bitmap, nCompressWidth, nCompressHeight);
        if (null == bitmapCompress)
            return false;

        try {
            File file = new File(strFilePath);
            FileOutputStream fos = new FileOutputStream(file);
            return bitmapCompress.compress(compressFormat, VALUE_INT_COMPRESS_QUALITY, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Bitmap getBitmap(Context context, String strFilePath) {
        return getBitmap(context, strFilePath, VALUE_INT_COMPRESS_SIZE, VALUE_INT_COMPRESS_SIZE);
    }

    public static Bitmap getBitmap(Context context, String strFilePath, int nCompressWidth, int nCompressHeight) {
        if (null == context || TextUtils.isEmpty(strFilePath))
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strFilePath, options);
        int nWidth = options.outWidth;
        int nHeight = options.outHeight;
        if (0 == nCompressWidth || 0 == nCompressHeight || nWidth <= nCompressWidth || nHeight <= nCompressHeight)
            return BitmapFactory.decodeFile(strFilePath);

        options = new BitmapFactory.Options();
        options.inSampleSize = Math.min(nWidth / nCompressWidth, nHeight / nCompressHeight);
        return BitmapFactory.decodeFile(strFilePath, options);
    }


    public static Bitmap drawableToBitmap(Context context, Drawable drawable) {
        if (null == context || null == drawable)
            return null;

        return ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        if (null == context || null == bitmap)
            return null;

        return new BitmapDrawable(bitmap);
    }

    public static int[] getBitmapSize(Context context, String strFilePath) {
        if (null == context || TextUtils.isEmpty(strFilePath) || !UtilsFile.isExists(strFilePath))
            return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strFilePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }
}
