package org.actflow.platform.http.client;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

/**
 * HTTP响应包装类
 * 
 * @author leyuanren 2013-9-2 下午4:06:52
 */
public class Response {

    /**
     * 是否成功获得http响应且响应码为200,且读取响应内容成功
     */
    private boolean success = false;

    /**
     * 返回中的Header信息
     */
    private Header[] responseHeaders;

    /**
     * 返回内容的字符编码
     */
    private String respCharset = "UTF-8";

    /**
     * btye类型的result
     */
    private byte[] byteResult;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRespCharset() {
        return respCharset;
    }

    public void setRespCharset(String respCharset) {
        this.respCharset = respCharset;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getByteResult() {
        if (byteResult != null) {
            return byteResult;
        }
        return null;
    }

    public void setByteResult(byte[] byteResult) {
        this.byteResult = byteResult;
    }

    public String getStringResult() {
        if (byteResult != null) {
            try {
                return new String(byteResult, respCharset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
