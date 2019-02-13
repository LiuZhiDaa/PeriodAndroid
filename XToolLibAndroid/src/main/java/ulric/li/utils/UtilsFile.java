package ulric.li.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilsFile {
    public static boolean isFile(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.isFile();
    }

    public static boolean isDirectory(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.isDirectory();
    }

    public static boolean isHidden(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.isHidden();
    }

    public static boolean isRootPath(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return true;

        return null == getParentPath(strPath);
    }

    public static boolean isLocalFullPath(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        return strPath.startsWith("/");
    }

    public static boolean isExists(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        return file.exists();
    }

    public static boolean isCanRead(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.canRead();
    }

    public static boolean isCanWrite(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.canWrite();
    }

    public static boolean isCanExecute(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        return file.canExecute();
    }

    public static String getName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        File file = new File(strPath);
        if (!file.exists())
            return null;

        return file.getName();
    }

    public static long getSize(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return 0;

        File file = new File(strPath);
        if (!file.exists())
            return 0;

        if (file.isFile())
            return file.length();

        long lSize = 0;
        try {
            for (File f : file.listFiles()) {
                if (f.isDirectory())
                    lSize += getSize(f.getAbsolutePath());
                else
                    lSize += f.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lSize;
    }

    public static String getParentPath(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        File file = new File(strPath);
        if (!file.exists())
            return null;

        return file.getParent();
    }

    public static String getParentName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        File file = new File(strPath);
        if (!file.exists())
            return null;

        File fileParent = file.getParentFile();
        if (null == fileParent)
            return null;

        return fileParent.getName();
    }

    public static boolean delete(String strPath, boolean bDeleteRootDirectory) {
        if (TextUtils.isEmpty(strPath))
            return false;

        File file = new File(strPath);
        if (!file.exists())
            return false;

        if (file.isFile())
            return file.delete();

        boolean bResult = true;
        try {
            for (File f : file.listFiles()) {
                boolean bDelete = false;
                if (f.isDirectory()) {
                    bDelete = delete(f.getAbsolutePath(), true);
                } else {
                    bDelete = f.delete();
                }

                if (!bDelete)
                    bResult = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bDeleteRootDirectory && bResult)
            bResult = file.delete();

        return bResult;
    }

    public static boolean rename(String strOldPath, String strNewPath) {
        if (TextUtils.isEmpty(strOldPath) || TextUtils.isEmpty(strNewPath))
            return false;

        File fileOld = new File(strOldPath);
        if (!fileOld.exists())
            return false;

        File fileNew = new File(strNewPath);
        return fileOld.renameTo(fileNew);
    }

    public static long getLastModifiedTime(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return 0;

        File file = new File(strPath);
        if (!file.exists())
            return 0;

        return file.lastModified();
    }

    public static String getExtensionName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        int nIndex = strPath.lastIndexOf(".");
        if (-1 == nIndex)
            return null;

        return strPath.substring(nIndex + 1).toLowerCase();
    }

    public static String getTargetName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        int nIndex = strPath.lastIndexOf("/");
        if (-1 == nIndex)
            return null;

        return strPath.substring(nIndex + 1).toLowerCase();
    }

    public static boolean copy(String strSrcPath, String strDestPath) {
        if (TextUtils.isEmpty(strSrcPath) || TextUtils.isEmpty(strDestPath))
            return false;

        File file = new File(strSrcPath);
        if (!file.exists())
            return false;

        if (file.isFile())
            return copyFile(strSrcPath, strDestPath);

        File fileDest = new File(strDestPath);
        if (!fileDest.mkdir())
            return false;

        boolean bResult = true;
        try {
            for (File f : file.listFiles()) {
                boolean b = false;
                if (f.isDirectory())
                    b = copy(f.getAbsolutePath(), new File(strDestPath, f.getName()).getAbsolutePath());
                else
                    b = copyFile(f.getAbsolutePath(), new File(strDestPath, f.getName()).getAbsolutePath());

                if (!b)
                    bResult = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bResult;
    }

    public static boolean copyFile(String strSrcPath, String strDestPath) {
        if (TextUtils.isEmpty(strSrcPath) || TextUtils.isEmpty(strDestPath))
            return false;

        File file = new File(strSrcPath);
        if (!file.exists() || file.isDirectory())
            return false;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(new File(strDestPath));
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            while ((nReadSize = fis.read(buffer, 0, UtilsEnv.VALUE_INT_BUFFER_SIZE)) != -1) {
                fos.write(buffer, 0, nReadSize);
            }
            fos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis)
                    fis.close();
                if (null != fos)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean open(Context context, String strPath) {
        if (null == context || TextUtils.isEmpty(strPath))
            return false;

        if (!isExists(strPath) || isDirectory(strPath))
            return false;

        Uri uri = Uri.parse("file://" + strPath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String strMimeType = UtilsMediaStore.getMimeType(strPath);
        if (TextUtils.isEmpty(strMimeType))
            return false;

        intent.setDataAndType(uri, strMimeType);
        context.startActivity(intent);
        return true;
    }

    public static String makePath(String strPath1, String strPath2) {
        if (strPath1.endsWith(File.separator))
            return strPath1 + strPath2;

        return strPath1 + File.separator + strPath2;
    }

    public static boolean createFile(String strPath, String strName) {
        if (TextUtils.isEmpty(strPath) || TextUtils.isEmpty(strName))
            return false;

        File file = new File(strPath, strName);
        try {
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean createDirectory(String strPath, String strName) {
        if (TextUtils.isEmpty(strPath) || TextUtils.isEmpty(strName))
            return false;

        File file = new File(strPath, strName);
        return file.mkdir();
    }

    /**
     * 向一个文件里写内容，如果文件不存在，那么创建，如果已经存在，那么续写
     * 每次写入以---***---折行分隔
     * @param strLog
     */
    public static void writeContent(Context context,String dirName,String fileName,String strLog) {
        if (context==null||TextUtils.isEmpty(dirName) || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(strLog)) {
            return;
        }
        try {
            String filePath = Environment.getExternalStorageDirectory() +File.separator+context.getPackageName()+File.separator+dirName;
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(filePath+File.separator+fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file,true);

            do {
                fos.write((strLog + "\n---***---\n").getBytes());
                fos.flush();
            } while (false);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
