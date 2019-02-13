package ulric.li.logic.alive;

import ulric.li.XLibFactory;
import ulric.li.utils.UtilsFile;
import ulric.li.utils.UtilsLog;

public class CrashTool implements Thread.UncaughtExceptionHandler {
    private static final long VALUE_LONG_THREAD_DELAY_TIME = 3000;
    // 系统默认的UncaughtException处理类
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        UtilsLog.crashLog("default",ex);

        //在sd卡存储log信息，方便测试查看，没有权限则会失败
        StringBuffer sb = new StringBuffer();
        sb.append(ex.toString());
        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append(element.toString());
        }
        UtilsFile.writeContent(XLibFactory.getApplication(),"crash","crash.txt",sb.toString());

        try {
            Thread.sleep(VALUE_LONG_THREAD_DELAY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDefaultHandler.uncaughtException(t,ex);
    }

    public static void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new CrashTool());
    }
}