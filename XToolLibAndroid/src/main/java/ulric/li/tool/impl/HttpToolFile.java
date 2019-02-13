package ulric.li.tool.impl;

import ulric.li.tool.intf.IHttpToolFile;

public class HttpToolFile implements IHttpToolFile {
    private String mFilePath = null;
    private String mName = null;
    private String mFileName = null;
    private String mContentDisposition = null;
    private String mContentType = null;

    public HttpToolFile() {
        _init();
    }

    private void _init() {
    }

    @Override
    public String getFilePath() {
        return mFilePath;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getFileName() {
        return mFileName;
    }

    @Override
    public String getContentDisposition() {
        return mContentDisposition;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public void setFilePath(String strFilePath) {
        mFilePath = strFilePath;
    }

    @Override
    public void setName(String strName) {
        mName = strName;
    }

    @Override
    public void setFileName(String strFileName) {
        mFileName = strFileName;
    }

    @Override
    public void setContentDisposition(String strContentDisposition) {
        mContentDisposition = strContentDisposition;
    }

    @Override
    public void setContentType(String strContentType) {
        mContentType = strContentType;
    }
}
