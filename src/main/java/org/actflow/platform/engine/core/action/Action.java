package org.actflow.platform.engine.core.action;

import org.actflow.platform.engine.exception.ActionExecutionException;
import org.actflow.platform.engine.exception.RetryLaterException;

/**
 * @ClassName: Action
 * @Description: 动作定义接口
 * @author Davis Lau
 * @date 2016年8月19日 下午7:29:48
 * @param <T>
 */
public interface Action<T extends Object> {
    
    /**
     * @Title: Action
     * @Description: 动作处理
     * @param @param context
     * @param @return
     * @param @throws RetryLaterException
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    T handle(T context) throws RetryLaterException, ActionExecutionException;
    
    /**
     * @Title: Action
     * @Description: 动作回滚
     * @param @param context
     * @param @return
     * @param @throws RetryLaterException
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    T rollback(T context) throws RetryLaterException, ActionExecutionException;
    
    /**
     * @Title: Action
     * @Description: 动作重试
     * @param @param context
     * @param @return
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    T retry(T context) throws ActionExecutionException;
}
