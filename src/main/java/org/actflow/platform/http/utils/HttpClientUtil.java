package org.actflow.platform.http.utils;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.actflow.platform.http.client.*;


/**
 * @author: Davis Lau
 */
public class HttpClientUtil {

    /**
     * log日志
     */
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 是否经过万达的代理，本机测试的时候可能需要打开这个选项
     */
    private static boolean useProxy = false;

    private static String charset = "UTF-8";

    public static String doGet(String path, Map<String, String> params) {
        return doCall(path, params, HttpMethod.GET);
    }

    public static String doPost(String path, Map<String, String> params) {
        return doCall(path, params, HttpMethod.POST);
    }

    private static String doCall(String path, Map<String, String> params, HttpMethod method) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Parameter 'path' is empty.");
        }

        Date startTime = new Date();

        // 构造request对象
        Request request = new Request();
        request.setGateway(path);
        request.setMethod(method);
        request.setCharset(charset);
        if (MapUtils.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                request.addParam(key, params.get(key));
            }
        }
        Response response;
        if (useProxy) {
            ProxyInfo proxyInfo = new ProxyInfo();
            proxyInfo.setHost("proxy1.wanda.cn");
            proxyInfo.setPort(8080);
            proxyInfo.setUserName("");
            proxyInfo.setPwd("");
            response = HttpCaller.getInstance().call(request, null, proxyInfo);
        } else {
            response = HttpCaller.getInstance().call(request, null);
        }
        Date endTime = new Date();
        if (response.isSuccess()) {
            response.setRespCharset(charset);
            String result = response.getStringResult();
            // 将结果输出到日志, 超出500字符进行截取
            StringBuffer sbf = new StringBuffer();
            if (result.length() > 500) {
                sbf.append(result.substring(0, 500));
                sbf.append("..omit..");
            } else {
                sbf.append(result);
            }
            long costtime = endTime.getTime() - startTime.getTime();
            logger.debug(String.format("调用服务 : %s; response text: %s\t costtime(ms) : %d", path, sbf.toString(),
                    costtime));
            return result;
        }

        return null;
    }

    public static String doGet(String path, Map<String, String> params, int connectionTimeout, int soTimeOut) {
        return doCall(path, params, HttpMethod.GET, connectionTimeout, soTimeOut, null);
    }

    public static String doPost(String path, Map<String, String> params, int connectionTimeout, int soTimeOut) {
        return doCall(path, params, HttpMethod.POST, connectionTimeout, soTimeOut, null);
    }

    public static String doGet(String path, Map<String, String> params, int connectionTimeout, int soTimeOut,
            Integer retryCount) {
        return doCall(path, params, HttpMethod.GET, connectionTimeout, soTimeOut, retryCount);
    }

    public static String doPost(String path, Map<String, String> params, int connectionTimeout, int soTimeOut,
            Integer retryCount) {
        return doCall(path, params, HttpMethod.POST, connectionTimeout, soTimeOut, retryCount);
    }

    private static String doCall(String path, Map<String, String> params, HttpMethod method, int connectionTimeout,
            int soTimeOut, Integer retryCount) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Parameter 'path' is empty.");
        }
        Date startTime = new Date();
        // 构造request对象
        Request request = new Request();
        request.setGateway(path);
        request.setMethod(method);
        request.setCharset(charset);
        request.setConnectionTimeout(connectionTimeout);
        request.setSoTimeout(soTimeOut);
        if (MapUtils.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                request.addParam(key, params.get(key));
            }
        }
        Response response;
        if (useProxy) {
            ProxyInfo proxyInfo = new ProxyInfo();
            proxyInfo.setHost("proxy1.wanda.cn");
            proxyInfo.setPort(8080);
            proxyInfo.setUserName("");
            proxyInfo.setPwd("");
            response = HttpCaller.getInstance().call(request, retryCount, proxyInfo);
        } else {
            response = HttpCaller.getInstance().call(request, retryCount);
        }
        Date endTime = new Date();
        if (response.isSuccess()) {
            response.setRespCharset(charset);
            String result = response.getStringResult();
            // 将结果输出到日志, 超出500字符进行截取
            StringBuffer sbf = new StringBuffer();
            if (result.length() > 500) {
                sbf.append(result.substring(0, 500));
                sbf.append("..omit..");
            } else {
                sbf.append(result);
            }
            long costtime = endTime.getTime() - startTime.getTime();
            logger.debug(String.format("调用服务 : %s; response text: %s\t costtime(ms) : %d", path, sbf.toString(),
                    costtime));
            return result;
        }

        return null;
    }

    public static boolean isUseProxy() {
        return useProxy;
    }

    public static void setUseProxy(boolean useProxy) {
        HttpClientUtil.useProxy = useProxy;
    }
}
