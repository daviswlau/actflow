/**   
 * @Title: ServiceAction.java
 * @Package org.actflow.platform.engine.core.action.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:24
 * @version V1.0
 */
package org.actflow.platform.engine.core.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import org.actflow.platform.engine.core.constants.EngineConstants;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.exception.ActionExecutionException;
import org.actflow.platform.engine.exception.RetryLaterException;
import org.actflow.platform.engine.exception.RetryLaterException.Method;
import org.actflow.platform.engine.utils.SpringContextUtils;
import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.http.utils.HttpClientUtil;

/** 
 * @ClassName: ServiceAction
 * @Description: 流程动作调用rest服务
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:24
 */
@Service
@Scope("prototype")
public class ServiceAction extends AbstractAction {
    private final static Logger logger = LoggerFactory.getLogger(ServiceAction.class);
    
    private ActionUrl actionUrl = new ActionUrl();

    /*
     * (non-Javadoc)
     * <p>Title: handle</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#handle(java.lang.Object)
     */
    @Override
    public ProcessMessage handle(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
    	ActionNode node = getActionNode();
    	if (node == null || StringUtils.isEmpty(node.handle)) {
    		return ProcessMessage.errorMessage();
    	}
    	
    	String resultString = null;
        Map<String, String> params = transformMap(context.getData());
//        Map params = JSONObject.parseObject(context.getData());
        try {
        	transferUrl(getActionNode().handle, getActionNode(), this, params);
            // currently only handle http url
            if (this.getActionUrl() != null && !this.getActionUrl().doSSL) {
                if (StringUtils.equalsIgnoreCase("post", this.getActionUrl().getHttpMethod())) {
                    resultString = HttpClientUtil.doPost(this.getActionUrl().getUrl(), params);
                } else if (StringUtils.equalsIgnoreCase("get", this.getActionUrl().getHttpMethod())) {
                    resultString = HttpClientUtil.doGet(this.getActionUrl().getUrl(), params);
                }
            }
//            logger.debug("[ServiceAction handle][{}]调用成功，返回结果:{}", this.getActionUrl().getUrl(), resultString);
        } catch (Exception e) {
            String errMsg = String.format("[ServiceAction handle][%s]调用, 调用参数: %s, 返回信息:%s", this.getActionUrl().getUrl(), params, resultString);
            logger.error(errMsg, e);
            // throw new ActionExecutionException(e.getMessage());

            RetryLaterException later = new RetryLaterException(errMsg);
            later.setMethod(Method.EXPONENTIAL);
            later.setDelay(RetryLaterException.DEFAULT_DELAY);
            
            //是否可以单独rollback
            if (getActionNode().soloRollback && StringUtils.isNotBlank(getActionNode().rollback)) {
            	later.setRollBack(true); 
            }
            
            throw later;
        }
        
        return JSON.parseObject(resultString, ProcessMessage.class);
    }

	/*
     * (non-Javadoc)
     * <p>Title: rollback</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#rollback(java.lang.Object)
     */
    @Override
    public ProcessMessage rollback(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
    	ActionNode node = getActionNode();
    	if (node == null || StringUtils.isEmpty(node.rollback)) {
    		return ProcessMessage.errorMessage();
    	}
    	
        String resultString = null;
        Map<String, String> params = transformMap(context.getData());
//        Map params = JSONObject.parseObject(context.getData());
        try {
        	transferUrl(getActionNode().rollback, getActionNode(), this, params);
            // currently only handle http url
            if (this.getActionUrl() != null && !this.getActionUrl().doSSL) {
                if (StringUtils.equalsIgnoreCase("post", this.getActionUrl().getHttpMethod())) {
                    resultString = HttpClientUtil.doPost(this.getActionUrl().getUrl(), params);
                } else if (StringUtils.equalsIgnoreCase("get", this.getActionUrl().getHttpMethod())) {
                    resultString = HttpClientUtil.doGet(this.getActionUrl().getUrl(), params);
                }
            }
//            logger.debug("[ServiceAction rollback]调用成功: {}", resultString);
        } catch (Exception e) {
            String errMsg = String.format("[ServiceAction rollback][%s]调用, 调用参数: %s, 返回信息:%s", this.getActionUrl().getUrl(), params, resultString);
            logger.error(errMsg, e);

            RetryLaterException later = new RetryLaterException(errMsg);
            later.setMethod(Method.LINEAR);
            later.setDelay(RetryLaterException.DEFAULT_DELAY);
            later.setRollBack(false);
            throw later;
        }
        return JSON.parseObject(resultString, ProcessMessage.class);
    }

	/*
     * (non-Javadoc)
     * <p>Title: retry</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#retry(java.lang.Object)
     */
    @Override
    public ProcessMessage retry(ProcessMessage context) throws ActionExecutionException {
        return this.handle(context);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> transformMap(String jsonString) {
        Map<String, String> paramMap = JSON.parseObject(jsonString, new HashMap<String, String>().getClass());
        return paramMap;
    }
    
	private static void transferUrl(String originalUrl, ActionNode actonNode, ServiceAction action, Map<String, String> params)
			throws IllegalAccessException {

		String fieldName = originalUrl.replace("${", "").replace("}", "");
		HashMap<String, String> urlMap = (HashMap<String, String>) SpringContextUtils.getBeanById(EngineConstants.SPRING_ENGINE_URL_MAP);
		String url = urlMap.get(fieldName);

		for (Map.Entry<String, String> entry : params.entrySet()) {
			url = url.replace("{" + entry.getKey() + "}", entry.getValue());
		}

		action.getActionUrl().setUrl(url);
		if (StringUtils.equals(actonNode.httpMethod, "posts")) {
			action.getActionUrl().setHttpMethod("post");
			action.getActionUrl().setDoSSL(true);
		} else if (StringUtils.equals(actonNode.httpMethod, "post")) {
			action.getActionUrl().setHttpMethod("post");
		} else if (StringUtils.equals(actonNode.httpMethod, "gets")) {
			action.getActionUrl().setHttpMethod("get");
			action.getActionUrl().setDoSSL(true);
		} else if (StringUtils.equals(actonNode.httpMethod, "get")) {
			action.getActionUrl().setHttpMethod("get");
		} else {
			action.getActionUrl().setHttpMethod("get");
		}
	}

    /**
     * @return the actionUrl
     */
    public ActionUrl getActionUrl() {
        return actionUrl;
    }

    /**
     * @param actionUrl the actionUrl to set
     */
    public void setActionUrl(ActionUrl actionUrl) {
        this.actionUrl = actionUrl;
    }

    private class ActionUrl {
        String httpMethod;
        String url;
        boolean doSSL = false;

        /**
         * @return the httpMethod
         */
        public String getHttpMethod() {
            return httpMethod;
        }

        /**
         * @param httpMethod the httpMethod to set
         */
        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @param doSSL the doSSL to set
         */
        public void setDoSSL(boolean doSSL) {
            this.doSSL = doSSL;
        }
    }
    
}
