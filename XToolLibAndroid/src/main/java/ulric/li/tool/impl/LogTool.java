package ulric.li.tool.impl;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.tool.intf.ILogTool;
import ulric.li.utils.UtilsApp;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsFile;
import ulric.li.utils.UtilsJson;
import ulric.li.utils.UtilsLog;
import ulric.li.utils.UtilsNetwork;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;
import ulric.li.xui.utils.UtilsSize;

public class LogTool implements ILogTool {
    private Context mContext = null;
    private IXThreadPool mIXThreadPool = null;
    private IHttpTool mIHttpTool = null;
    private ReadWriteLock mReadWriteLockStatistics = null;
    private ReadWriteLock mReadWriteLockCrash = null;

    public static final int VALUE_INT_STATISTICS_LOG_TYPE = 0x1000;
    public static final int VALUE_INT_CRASH_LOG_TYPE = 0x1001;

    public LogTool() {
        mContext = XLibFactory.getApplication();
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
        mIHttpTool = (IHttpTool) XLibFactory.getInstance().createInstance(IHttpTool.class);
        mReadWriteLockStatistics = new ReentrantReadWriteLock();
        mReadWriteLockCrash = new ReentrantReadWriteLock();
    }

    @Override
    public void statisticsLog(final String strKey1, final String strKey2, final JSONObject jsonObjectContent, final JSONObject jsonObjectUserInfo) {
        if (TextUtils.isEmpty(strKey1))
            return;
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    UtilsJson.JsonSerialization(jsonObject, "key1", strKey1);
                    UtilsJson.JsonSerialization(jsonObject, "key2", strKey2);
                    UtilsJson.JsonSerialization(jsonObject, "content", jsonObjectContent);
                    UtilsJson.JsonSerialization(jsonObject, "user", jsonObjectUserInfo);
                    UtilsJson.JsonSerialization(jsonObject, "time", System.currentTimeMillis());
                    UtilsJson.JsonSerialization(jsonObject, "network", UtilsNetwork.getNetworkType(mContext));
                    UtilsJson.JsonSerialization(jsonObject, "app_version_code", UtilsApp.getMyAppVersionCode(mContext));
                    UtilsJson.JsonSerialization(jsonObject, "app_version_name", UtilsApp.getMyAppVersionName(mContext));
                    UtilsJson.JsonSerialization(jsonObject, "basic", getBasicInfo());

                    String strData = jsonObject.toString();
                    writeLog(VALUE_INT_STATISTICS_LOG_TYPE, strData);
                } catch (Exception e) {
//                    crashLog(e);
                }
            }

            @Override
            public void onTaskComplete() {

            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    @Override
    public void crashLog(Throwable throwable,String tag) {
        if (null == throwable)
            return;

        final JSONObject jsonObject = new JSONObject();

        StringBuffer sb = new StringBuffer();
        sb.append(throwable.toString());
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append(element.toString());
        }
        if (!TextUtils.isEmpty(tag)) {
            tag += ":";
        }

        UtilsJson.JsonSerialization(jsonObject, "crash", tag+sb.toString());
        UtilsJson.JsonSerialization(jsonObject, "time", System.currentTimeMillis());
        UtilsJson.JsonSerialization(jsonObject, "app_version_code", UtilsApp.getMyAppVersionCode(mContext));
        UtilsJson.JsonSerialization(jsonObject, "app_version_name", UtilsApp.getMyAppVersionName(mContext));
        UtilsJson.JsonSerialization(jsonObject, "basic", getBasicInfo());
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                String strData = jsonObject.toString();
                writeLog(VALUE_INT_CRASH_LOG_TYPE, strData);
            }

            @Override
            public void onTaskComplete() {
            }

            @Override
            public void onMessage(Message msg) {
            }
        });
    }

    @Override
    public void sendLog() {
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                sendLog(VALUE_INT_STATISTICS_LOG_TYPE);
                sendLog(VALUE_INT_CRASH_LOG_TYPE);
            }

            @Override
            public void onTaskComplete() {

            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    private JSONObject getBasicInfo() {
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "mid", UtilsEnv.getPhoneID(mContext));
        UtilsJson.JsonSerialization(jsonObject, "brand", UtilsEnv.getAndroidBrand());
        UtilsJson.JsonSerialization(jsonObject, "model", UtilsEnv.getAndroidModel());
        UtilsJson.JsonSerialization(jsonObject, "language", UtilsEnv.getSystemLanguage());
        UtilsJson.JsonSerialization(jsonObject, "country", UtilsEnv.getCountry());
        UtilsJson.JsonSerialization(jsonObject, "version", UtilsEnv.getAndroidVersionString());
        UtilsJson.JsonSerialization(jsonObject, "sdk", UtilsEnv.getAndroidSDK());
        UtilsJson.JsonSerialization(jsonObject, "channel", UtilsEnv.getChannel());
        UtilsJson.JsonSerialization(jsonObject, "display_width", UtilsSize.getScreenWidth(mContext));
        UtilsJson.JsonSerialization(jsonObject, "display_height", UtilsSize.getScreenHeight(mContext));
        return jsonObject;
    }

    private void writeLog(int nLogType, String strLog) {
        if (TextUtils.isEmpty(strLog))
            return;

        ReadWriteLock lock = VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? mReadWriteLockStatistics : mReadWriteLockCrash;
        try {
            lock.writeLock().lock();

            FileOutputStream fos = mContext.openFileOutput(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath(), Context.MODE_APPEND);

            do {
                fos.write((strLog + UtilsLog.getSeparator()).getBytes());
                fos.flush();
            } while (false);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.writeLock().unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendLog(final int nLogType) {
        if (!UtilsFile.isExists(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(),
                VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath())))
            return;

        if (UtilsFile.getSize(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(),
                VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath())) > 10 * 1000 * 1000) {
            mContext.deleteFile(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath());
            return;
        }


        ReadWriteLock lock = VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? mReadWriteLockStatistics : mReadWriteLockCrash;
        try {
            lock.writeLock().lock();

            FileInputStream fis = mContext.openFileInput(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath());
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((nReadSize = fis.read(buffer)) != -1)
                baos.write(buffer, 0, nReadSize);

            fis.close();
            baos.close();

            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put("data", baos.toString());
            IHttpToolResult iHttpToolResult = mIHttpTool.requestToBufferByPostSync(UtilsNetwork.getURL(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogURL() : UtilsLog.getCrashLogURL()), mapParam, null);
            if (null != iHttpToolResult && iHttpToolResult.isSuccess()) {
                int nCode = UtilsNetwork.VALUE_INT_FAIL_CODE;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(iHttpToolResult.getBuffer()));
                    nCode = UtilsJson.JsonUnserialization(jsonObject, "code", nCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (UtilsNetwork.VALUE_INT_SUCCESS_CODE == nCode) {
                    mContext.deleteFile(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath());
                }
            }
        } catch (OutOfMemoryError e) {
            mContext.deleteFile(VALUE_INT_STATISTICS_LOG_TYPE == nLogType ? UtilsLog.getStatisticsLogPath() : UtilsLog.getCrashLogPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.writeLock().unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
