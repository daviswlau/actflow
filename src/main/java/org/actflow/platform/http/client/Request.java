package org.actflow.platform.http.client;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP请求信息包装类
 * 
 * @author leyuanren 2013-6-14 下午2:16:09
 */

public class Request {
    /**
     * 默认的请求编码方式
     */
    private String charset = "UTF-8";

    private String scheme = "http";

    private String host = null;

    private String path = "";

    /**
     * 如果设置了gateway,将忽略scheme、host、path的设置
     */
    private String gateway = null;

    private boolean excludeEmptyValue = false;

    private List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    /**
     * 默认的请求方式
     */
    private HttpMethod method = HttpMethod.GET;

    private int soTimeout = 0;
    private int connectionTimeout = 0;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void addParam(String key, String value) {
        if (key != null) {
            parameters.add(new BasicNameValuePair(key, value));
        }
    }

    public List<NameValuePair> getNameValuePairList() {
        return parameters;
    }

    public boolean isExcludeEmptyValue() {
        return excludeEmptyValue;
    }

    public void setExcludeEmptyValue(boolean excludeEmptyValue) {
        this.excludeEmptyValue = excludeEmptyValue;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

}
