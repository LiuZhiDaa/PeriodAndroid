package ulric.li.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class UtilsMediaStore {
    public static final int VALUE_INT_UNKNOWN_FILE_TYPE = 0x1000;
    public static final int VALUE_INT_IMAGE_FILE_TYPE = 0x1001;
    public static final int VALUE_INT_VIDEO_FILE_TYPE = 0x1002;
    public static final int VALUE_INT_AUDIO_FILE_TYPE = 0x1003;
    public static final int VALUE_INT_DOC_FILE_TYPE = 0x1004;
    public static final int VALUE_INT_ARCHIVE_FILE_TYPE = 0x1005;
    public static final int VALUE_INT_APK_FILE_TYPE = 0x1006;

    public static final int VALUE_INT_SORT_BY_NAME = 0x1000;
    public static final int VALUE_INT_SORT_BY_DATE = 0x1001;
    public static final int VALUE_INT_SORT_BY_SIZE = 0x1002;
    public static final int VALUE_INT_SORT_BY_TYPE = 0x1003;

    public static final String VALUE_STRING_DEFAULT_MIME_TYPE = "*/*";

    private static HashMap<String, Integer> sMapFileType = new HashMap<String, Integer>();

    static {
        sMapFileType.put("jpe", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("jpeg", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("jpg", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("gif", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("png", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("bmp", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("wbmp", VALUE_INT_IMAGE_FILE_TYPE);
        sMapFileType.put("webp", VALUE_INT_IMAGE_FILE_TYPE);

        sMapFileType.put("webm", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mkv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("flv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("vob", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("ogv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("avi", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mov", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("wmv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("rm", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("rmvb", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("asf", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("amv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mp4", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("m4p", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("m4v", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mpg", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mp2", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mpeg", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mpe", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("mpv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("m2v", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("svi", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("3gp", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("3g2", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("flv", VALUE_INT_VIDEO_FILE_TYPE);
        sMapFileType.put("f4v", VALUE_INT_VIDEO_FILE_TYPE);

        sMapFileType.put("aac", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("aax", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("aiff", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("amr", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("ape", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("au", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("awb", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("dss", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("dvf", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("flac", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("gsm", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("ivs", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("m4a", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("mmf", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("mp3", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("mpc", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("msv", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("ogg", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("oga", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("ra", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("tta", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("vox", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("wma", VALUE_INT_AUDIO_FILE_TYPE);
        sMapFileType.put("mv", VALUE_INT_AUDIO_FILE_TYPE);

        sMapFileType.put("txt", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("pdf", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("doc", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("docx", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("xls", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("xlsx", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("ppt", VALUE_INT_DOC_FILE_TYPE);
        sMapFileType.put("pptx", VALUE_INT_DOC_FILE_TYPE);

        sMapFileType.put("zip", VALUE_INT_ARCHIVE_FILE_TYPE);
        sMapFileType.put("rar", VALUE_INT_ARCHIVE_FILE_TYPE);
        sMapFileType.put("gz", VALUE_INT_ARCHIVE_FILE_TYPE);
        sMapFileType.put("bz2", VALUE_INT_ARCHIVE_FILE_TYPE);
        sMapFileType.put("tar", VALUE_INT_ARCHIVE_FILE_TYPE);

        sMapFileType.put("apk", VALUE_INT_APK_FILE_TYPE);
    }

    public static int getFileType(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return VALUE_INT_UNKNOWN_FILE_TYPE;

        String strExtensionName = UtilsFile.getExtensionName(strPath);
        if (TextUtils.isEmpty(strExtensionName) || !sMapFileType.containsKey(strExtensionName))
            return VALUE_INT_UNKNOWN_FILE_TYPE;

        return sMapFileType.get(strExtensionName);
    }

    public static String getMimeType(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        String strExtensionName = UtilsFile.getExtensionName(strPath);
        if (TextUtils.isEmpty(strExtensionName))
            return VALUE_STRING_DEFAULT_MIME_TYPE;

        String strMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(strExtensionName);
        if (TextUtils.isEmpty(strMimeType))
            return VALUE_STRING_DEFAULT_MIME_TYPE;

        return strMimeType.toLowerCase();
    }

    public static void scan(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return;

        File file = new File(strPath);
        if (!file.exists())
            return;

        if (file.isFile()) {
            scanFile(context, file.getAbsolutePath());
            return;
        }

        try {
            for (File f : file.listFiles()) {
                if (f.isDirectory())
                    scan(context, f.getAbsolutePath());
                else
                    scanFile(context, f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return;

        File file = new File(strPath);
        if (!file.exists())
            return;

        if (file.isFile()) {
            deleteFile(context, file.getAbsolutePath());
            return;
        }

        try {
            for (File f : file.listFiles()) {
                if (f.isDirectory())
                    delete(context, f.getAbsolutePath());
                else
                    deleteFile(context, f.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scanFile(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return;

        if (UtilsEnv.getAndroidSDK() >= 19) {
            MediaScannerConnection.scanFile(context, new String[]{strPath}, null, null);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(strPath)));
            context.sendBroadcast(intent);
        }
    }

    public static void scanFile(Context context, List<String> listPath) {
        if (null == context || null == listPath)
            return;

        if (UtilsEnv.getAndroidSDK() >= 19) {
            MediaScannerConnection.scanFile(context, (String[]) listPath.toArray(), null, null);
        } else {
            for (String strPath : listPath) {
                if (TextUtils.isEmpty(strPath))
                    continue;

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(strPath)));
                context.sendBroadcast(intent);
            }
        }
    }

    public static void deleteFile(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return;

        Uri uri = MediaStore.Files.getContentUri("external");
        String strWhere = MediaStore.MediaColumns.DATA + "=?";
        context.getContentResolver().delete(uri, strWhere, new String[]{strPath});
    }

    public static void deleteFile(Context context, List<String> listPath) {
        if (null == context || null == listPath)
            return;

        Uri uri = MediaStore.Files.getContentUri("external");
        String strWhere = MediaStore.MediaColumns.DATA + "=?";
        context.getContentResolver().delete(uri, strWhere, (String[]) listPath.toArray());
    }

    public static Cursor query(Context context, int nFileType, int nSortType) {
        if (null == context)
            return null;

        Uri uri = getUri(nFileType);
        if (null == uri)
            return null;

        String[] strarrayProjection = buildProjection(nFileType);
        if (null == strarrayProjection)
            return null;

        String strSelection = buildSelection(nFileType);
        String strSortOrder = buildSortOrder(nSortType);
        return context.getContentResolver().query(uri, strarrayProjection, strSelection, null, strSortOrder);
    }

    public static Cursor search(Context context, String strKeywords, int nSortType) {
        if (null == context || TextUtils.isEmpty(strKeywords))
            return null;

        Uri uri = MediaStore.Files.getContentUri("external");
        String[] strarrayProjection = new String[]{MediaStore.Files.FileColumns.DATA};
        String strSelection = MediaStore.Files.FileColumns.DATA + " LIKE '%" + strKeywords + "%'";
        String strSortOrder = buildSortOrder(nSortType);
        return context.getContentResolver().query(uri, strarrayProjection, strSelection, null, strSortOrder);
    }

    private static Uri getUri(int nFileType) {
        switch (nFileType) {
            case VALUE_INT_IMAGE_FILE_TYPE:
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            case VALUE_INT_VIDEO_FILE_TYPE:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            case VALUE_INT_AUDIO_FILE_TYPE:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            case VALUE_INT_DOC_FILE_TYPE:
            case VALUE_INT_ARCHIVE_FILE_TYPE:
            case VALUE_INT_APK_FILE_TYPE:
                return MediaStore.Files.getContentUri("external");
        }

        return null;
    }

    private static String[] buildProjection(int nFileType) {
        switch (nFileType) {
            case VALUE_INT_IMAGE_FILE_TYPE:
                return new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                };
            case VALUE_INT_VIDEO_FILE_TYPE:
            case VALUE_INT_AUDIO_FILE_TYPE:
            case VALUE_INT_DOC_FILE_TYPE:
            case VALUE_INT_ARCHIVE_FILE_TYPE:
            case VALUE_INT_APK_FILE_TYPE:
                return new String[]{MediaStore.Files.FileColumns.DATA};
        }

        return null;
    }

    private static String buildSelection(int nFileType) {
        switch (nFileType) {
            case VALUE_INT_DOC_FILE_TYPE:
                return buildDocSelection();
            case VALUE_INT_ARCHIVE_FILE_TYPE:
                return buildArchiveSelection();
            case VALUE_INT_APK_FILE_TYPE:
                return buildApkSelection();
        }

        return null;
    }

    private static String buildDocSelection() {
        String[] strarrayExtensionName = new String[]{
                "txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"};

        StringBuilder sb = new StringBuilder();
        for (String strExtensionName : strarrayExtensionName) {
            sb.append("(" + MediaStore.Files.FileColumns.DATA + " LIKE '%." + strExtensionName + "') OR ");
        }

        return sb.substring(0, sb.lastIndexOf(")") + 1);
    }

    private static String buildArchiveSelection() {
        String[] strarrayExtensionName = new String[]{
                "zip", "rar", "gz", "bz2", "tar"};

        StringBuilder sb = new StringBuilder();
        for (String strExtensionName : strarrayExtensionName) {
            sb.append("(" + MediaStore.Files.FileColumns.DATA + " LIKE '%." + strExtensionName + "') OR ");
        }

        return sb.substring(0, sb.lastIndexOf(")") + 1);
    }

    private static String buildApkSelection() {
        return MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
    }

    private static String buildSortOrder(int nSortType) {
        switch (nSortType) {
            case VALUE_INT_SORT_BY_NAME:
                return MediaStore.Files.FileColumns.TITLE + " asc";
            case VALUE_INT_SORT_BY_DATE:
                return MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
            case VALUE_INT_SORT_BY_SIZE:
                return MediaStore.Files.FileColumns.SIZE + " desc";
            case VALUE_INT_SORT_BY_TYPE:
                return MediaStore.Files.FileColumns.MIME_TYPE + " asc";
        }

        return null;
    }
}
