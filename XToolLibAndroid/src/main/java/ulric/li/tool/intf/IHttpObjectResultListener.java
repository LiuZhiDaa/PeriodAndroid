package ulric.li.tool.intf;

/**
 * Created by WangYu on 2018/10/11.
 */
public interface IHttpObjectResultListener {

   void onRequestSuccess(IHttpToolResult result);

    void onRequestFailed(IHttpToolResult result);
}
