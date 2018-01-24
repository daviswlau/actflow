package org.actflow.platform.http.client;

/**
 * 包装代理认证信息
 * 
 * @author leyuanren 2013-9-3 下午7:25:46
 */
public class ProxyInfo {

    private String host;

    private int port;

    private String userName;

    private String pwd;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
