package ulric.li.tool.intf;

import ulric.li.xlib.intf.IXObject;

public interface IHttpToolFile extends IXObject {
    String getFilePath();

    String getName();

    String getFileName();

    String getContentDisposition();

    String getContentType();

    void setFilePath(String strFilePath);

    void setName(String strName);

    void setFileName(String strFileName);

    void setContentDisposition(String strContentDisposition);

    void setContentType(String strContentType);
}
