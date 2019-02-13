package ulric.li.tool.intf;

import java.util.List;
import java.util.Map;

import ulric.li.xlib.intf.IXObject;
import ulric.li.xlib.intf.IXObserver;

public interface IHttpTool extends IXObject, IXObserver<IHttpToolListener> {
    /**
     * get请求
     */
    IHttpToolResult requestToBufferByGetSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty);

    IHttpToolResult requestToBufferByGetSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    void requestByGetAsync(String strURL, Map<String, String> mapParam, IHttpObjectResultListener listener);

    void requestToBufferByGetAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener);

    void requestToBufferByGetAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    /**
     * post请求
     */
    IHttpToolResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty);

    IHttpToolResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    void requestByPostAsync(String strURL, Map<String, String> mapParam, IHttpObjectResultListener listener);

    void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener);

    void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    /**
     * 上传文件
     */
    IHttpToolResult uploadFileByPostSync(String strURL, Map<String, String> mapParam, List<IHttpToolFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty);

    IHttpToolResult uploadFileByPostSync(String strURL, Map<String, String> mapParam, List<IHttpToolFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    void uploadFileByPostAsync(String strURL, Map<String, String> mapParam, List<IHttpToolFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener);

    void uploadFileByPostAsync(String strURL, Map<String, String> mapParam, List<IHttpToolFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    /**
     * 下载文件
     */
    IHttpToolResult downloadFileByPostSync(String strURL, Map<String, String> mapParam, IHttpToolFile downloadFile, Map<String, String> mapRequestProperty, Object objectTag);

    IHttpToolResult downloadFileByPostSync(String strURL, Map<String, String> mapParam, IHttpToolFile downloadFile, Map<String, String> mapRequestProperty, Object objectTag, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    void downloadFileByPostAsync(String strURL, Map<String, String> mapParam, IHttpToolFile downloadFile, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener);

    void downloadFileByPostAsync(String strURL, Map<String, String> mapParam, IHttpToolFile downloadFile, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);
}
