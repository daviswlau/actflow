package org.actflow.platform.http.client;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP调用工具类，内部使用连接池管理
 * 
 * @author leyuanren 2013-6-14 下午2:53:48
 */
public class HttpCaller {

    /**
     * log日志
     */
    private static final Logger logger = LoggerFactory.getLogger(HttpCaller.class);

    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private static final int DEFAULT_CONNECTIONT_IMEOUT = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为5秒钟 */
    private static final int DEFAULT_SO_TIMEOUT = 5000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为120秒钟 */
    private static final int DEFAULT_IDLE_TIMEOUT = 120000;

    /**
     * 每个host默认最大连接数
     */
    private static final int DEFAULT_MAX_CONN_PERHOST = 500;

    /**
     * 默认全局最大连接数
     */
    private static final int DEFAULT_MAX_TOTAL_CONN = 900;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private PoolingClientConnectionManager connectionManager;

    private static HttpCaller httpCaller = new HttpCaller();

    private IdleConnectionMonitorThread idleEvictThread;

    /**
     * 工厂方法
     * 
     * @return
     */
    public static HttpCaller getInstance() {
        return httpCaller;
    }

    /**
     * 私有的构造方法
     */
    private HttpCaller() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        // 访问https网站绕过证书验证
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        // 创建一个线程安全的HTTP连接池
        connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONN);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONN_PERHOST);

        idleEvictThread = new IdleConnectionMonitorThread(connectionManager);
        idleEvictThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                httpCaller.shutdown();
            }
        });
    }

    public Response call(Request request, Integer retryCount) {
        return call(request, retryCount, null);
    }

    public Response call(Request request, Integer retryCount, ProxyInfo proxyInfo) {
        HttpClient httpClient =
                getHttpClient(request.getConnectionTimeout(), request.getSoTimeout(), retryCount, proxyInfo);
        Response response = new Response();
        HttpResponse httpResponse = null;
        HttpRequestBase httpRequestBase = null;
        // get模式且不带上传文件
        if (request.getMethod() == HttpMethod.GET) {
            httpRequestBase = buildHttpGet(request);
        }
        // post模式且不带上传文件
        else if (request.getMethod() == HttpMethod.POST) {
            httpRequestBase = buildHttpPost(request);
        }
        try {
            httpResponse = httpClient.execute(httpRequestBase);
            HttpEntity entity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                response.setSuccess(false);
                logger.warn("http response status code is not 200. status line: " + httpResponse.getStatusLine());
            } else {
                response.setResponseHeaders(httpResponse.getAllHeaders());
                response.setByteResult(EntityUtils.toByteArray(entity));
                response.setSuccess(true);
            }
            // 释放连接
            EntityUtils.consume(entity);
        } catch (Exception e) {
            logger.error("http call error.", e);
            if (httpRequestBase != null) {
                httpRequestBase.abort();
            }
            response.setSuccess(false);
        }
        return response;
    }

    /**
     * 从连接池中取一个http连接来初始化HttpClient实例
     * 
     * @param connectionTimeout
     * @param soTimeout
     * @return
     */
    private HttpClient getHttpClient(int connectionTimeout, int soTimeout, Integer retryCount, ProxyInfo proxyInfo) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "Mozilla/4.0");
        HttpConnectionParams.setTcpNoDelay(params, true);
        // 设置连接超时
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout == 0 ? DEFAULT_CONNECTIONT_IMEOUT
                : connectionTimeout);
        // 设置回应超时
        HttpConnectionParams.setSoTimeout(params, soTimeout == 0 ? DEFAULT_SO_TIMEOUT : soTimeout);

        DefaultHttpClient httpclient = new DefaultHttpClient(connectionManager, params);

        if (proxyInfo != null) {
            // 设置代理
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyInfo.getHost(), proxyInfo.getPort()),
                    new UsernamePasswordCredentials(proxyInfo.getUserName(), proxyInfo.getPwd()));
            HttpHost proxy = new HttpHost(proxyInfo.getHost(), proxyInfo.getPort());
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        if (retryCount != null) {
            httpclient.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(retryCount, true));
        }

        return httpclient;
    }

    private HttpGet buildHttpGet(Request request) {
        try {
            URIBuilder builder = null;
            if (request.getGateway() != null) {
                builder = new URIBuilder(request.getGateway());
            } else {
                builder = new URIBuilder();
                builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
            }
            for (NameValuePair pair : request.getNameValuePairList()) {
                String value = pair.getValue();
                if (request.isExcludeEmptyValue() && (value == null || "".equals(value))) {
                    continue;
                }
                builder.addParameter(pair.getName(), value);
            }
            URI uri = builder.build();
            logger.debug("build request uri: " + uri.toString());
            return new HttpGet(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("request uri syntax error.", e);
        }
    }

    private HttpPost buildHttpPost(Request request) {
        HttpPost httpPost;
        try {
            URIBuilder builder = null;
            if (request.getGateway() != null) {
                builder = new URIBuilder(request.getGateway());
            } else {
                builder = new URIBuilder();
                builder.setScheme(request.getScheme()).setHost(request.getHost()).setPath(request.getPath());
            }
            UrlEncodedFormEntity entity =
                    new UrlEncodedFormEntity(request.getNameValuePairList(), request.getCharset());
            URI uri = builder.build();
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for (NameValuePair pair : request.getNameValuePairList()) {
                sb.append(sep).append(pair.toString());
                sep = " | ";
            }
            logger.debug(String.format("build request uri: %s; post params: [%s]", uri.toString(), sb.toString()));
            httpPost = new HttpPost(uri);
            httpPost.setEntity(entity);
            return httpPost;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("request unsupported encoding error.", e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("request uri syntax error.", e);
        }
    }

    public void shutdown() {
        this.idleEvictThread.shutdown();
        this.connectionManager.shutdown();
    }

    /**
     * 守护线程，定时清理过期和空闲时间超时的连接
     * 
     * @author leyuanren 2013-9-3 下午7:18:52
     */
    private static class IdleConnectionMonitorThread extends Thread {

        private final ClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            this.setDaemon(true);// 守护线程
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(DEFAULT_IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            if (!shutdown) {
                shutdown = true;
                synchronized (this) {
                    notifyAll();
                }
            }
        }

    }
}
