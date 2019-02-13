package ulric.li.tool.impl;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import ulric.li.tool.intf.IHttpToolResult;

public class HttpToolResult implements IHttpToolResult {
    private boolean mSuccess = false;
    private int mResponseCode = 0;
    private byte[] mBuffer = null;
    private Map<String, List<String>> mMapHeaderField = null;
    private String mException = null;
    private int mDownloadTotalSize = 0;
    private int mDownloadCurrentSize = 0;

    public HttpToolResult() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean isSuccess() {
        return mSuccess;
    }

    @Override
    public int getResponseCode() {
        return mResponseCode;
    }

    @Override
    public byte[] getBuffer() {
        return mBuffer;
    }

    @Override
    public String getString() {
        if (mBuffer==null) {
            return null;
        }
        return new String(mBuffer);
    }

    @Override
    public Map<String, List<String>> getHeaderFieldMap() {
        return mMapHeaderField;
    }

    @Override
    public String getException() {
        return mException;
    }

    @Override
    public <T> T getObject(Class<T> cls) {
        if (mBuffer==null) {
            return null;
        }
        T t=null;
        try {
           t = JSON.parseObject(new String(mBuffer), cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public int getDownloadTotalSize() {
        return mDownloadTotalSize;
    }

    @Override
    public int getDownloadCurrentSize() {
        return mDownloadCurrentSize;
    }

    @Override
    public double getDownloadProgress() {
        if (mDownloadTotalSize==0) {
            return 0;
        }
        return mDownloadCurrentSize*1.0d/mDownloadTotalSize;
    }

    public void setSuccess(boolean bSuccess) {
        mSuccess = bSuccess;
    }

    public void setResponseCode(int nResponseCode) {
        mResponseCode = nResponseCode;
    }

    public void setBuffer(byte[] buffer) {
        mBuffer = buffer;
    }

    public void setHeaderFieldMap(Map<String, List<String>> mapHeaderField) {
        mMapHeaderField = mapHeaderField;
    }

    public void setException(String strException) {
        mException = strException;
    }

    public void setDownloadTotalSize(int downloadTotalSize) {
        mDownloadTotalSize = downloadTotalSize;
    }

    public void setDownloadCurrentSize(int downloadCurrentSize) {
        mDownloadCurrentSize = downloadCurrentSize;
    }
}
