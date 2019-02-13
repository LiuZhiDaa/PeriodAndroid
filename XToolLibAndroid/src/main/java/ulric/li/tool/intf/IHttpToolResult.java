package ulric.li.tool.intf;

import java.util.List;
import java.util.Map;

import ulric.li.xlib.intf.IXObject;

public interface IHttpToolResult extends IXObject {
    /**
     * 请求是否成功
     *
     * @return
     */
    boolean isSuccess();

    /**
     * 返回http响应码
     *
     * @return
     */
    int getResponseCode();

    /**
     * 获取响应内容
     *
     * @return
     */
    byte[] getBuffer();

    /**
     * 获取响应内容
     *
     * @return
     */
    String getString();

    /**
     * 获取用fastjson解析后的对象
     *
     * @param cls
     * @param <T>
     * @return
     */
    <T> T getObject(Class<T> cls);

    /**
     * 获取请求头信息
     *
     * @return
     */
    Map<String, List<String>> getHeaderFieldMap();

    /**
     * 获取异常信息
     *
     * @return
     */
    String getException();

    /**
     * 获取下载文件的总大小
     * 单位byte
     *
     * @return
     */
    int getDownloadTotalSize();

    /**
     * 获取下载文件的当前下载的大小
     * 单位byte
     *
     * @return
     */
    int getDownloadCurrentSize();

    /**
     * 获取下载进度
     *
     * @return
     */
    double getDownloadProgress();
}
