/**   
 * @Title: EngineExceptionHandleUtils.java
 * @Package org.actflow.platform.engine.utils
 * @Description: 引擎异常处理类
 * @author Davis Lau
 * @date 2016年8月25日 上午11:09:49
 * @version V1.0
 */
package org.actflow.platform.engine.exception;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.actflow.platform.engine.actor.AbstractActionActor;
import org.actflow.platform.engine.core.action.AbstractAction;
import org.actflow.platform.engine.dto.ProcessMessage;

/** 
 * @ClassName: EngineExceptionHandleUtils
 * @Description: 引擎异常处理，包括：动作重试、动作回滚、流程本地重试、流程消息timer重试、流程回滚等机制
 * @author Davis Lau
 * @date 2016年8月25日 上午11:09:49
 */
public class EngineExceptionHandleUtils {
    private final static Logger logger = LoggerFactory.getLogger(EngineExceptionHandleUtils.class);
    
    public static ProcessMessage handleRetryExecute(AbstractAction action, ProcessMessage context, int actionTries) throws ActionExecutionException {
        int tries = actionTries;
        ProcessMessage result = null;
        ActionExecutionException aee = null;
        boolean actionError = false;
        do{
            try {
                result = action.handle(context);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof RetryLaterException) {
                    RetryLaterException later = (RetryLaterException) e;
                    tries = tries - 1;
                    if (tries < 0) {
                        actionError = true;
                        aee = new ActionExecutionException(later.getMessage(), later);
                        break;
                    }
                    Date currentTime = new Date();
                    Date nextTime = later.getMethod().nextInvocation(currentTime, tries, later.getDelay());
                    try {
                        Thread.sleep(nextTime.getTime() - currentTime.getTime());
                    } catch (InterruptedException ie) {
                        actionError = true;
                        aee = new ActionExecutionException(ie.getMessage(), later);
                        break;
                    }
                }
            }
        } while(tries >= 0);
        
        if (actionError) {
            RetryLaterException retry = (RetryLaterException) aee.getCause();
            if(retry.isRollBack()) {
                result = rollbackRetryExecute(action, context, actionTries);
            } else {
                throw aee;
            }
        }
        
        return result;
    }
    
    public static ProcessMessage rollbackRetryExecute(AbstractAction action, ProcessMessage context, int actionTries) throws ActionExecutionException {
        int tries = actionTries;
        ProcessMessage result = null;
        ActionExecutionException aee = null;
        boolean actionError = false;
        do{
            try {
                result = action.rollback(context);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof RetryLaterException) {
                    RetryLaterException later = (RetryLaterException) e;
                    if (tries < 0) {
                        actionError = true;
                        aee = new ActionExecutionException(later.getMessage(), later.getCause());
                        break;
                    }
                    Date currentTime = new Date();
                    Date nextTime = later.getMethod().nextInvocation(currentTime, tries, later.getDelay());
                    try {
                        Thread.sleep(nextTime.getTime() - currentTime.getTime());
                    } catch (InterruptedException ie) {
                        actionError = true;
                        aee = new ActionExecutionException(ie.getMessage(), ie.getCause());
                        break;
                    }
                    tries = tries - 1;
                }
            }
        } while(tries >= 0);
        
        if (actionError) {
            throw aee;
        }
        
        return result;
    }
    
    public static ProcessMessage handleRetryExecute(AbstractActionActor actor, ProcessMessage context, int actionTries) throws ActionExecutionException {
        int tries = actionTries;
        ProcessMessage result = null;
        ActionExecutionException aee = null;
        boolean actionError = false;
        do{
            try {
                result = actor.handle(context);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof RetryLaterException) {
                    RetryLaterException later = (RetryLaterException) e;
                    tries = tries - 1;
                    if (tries < 0) {
                        actionError = true;
                        aee = new ActionExecutionException(later.getMessage(), later);
                        break;
                    }
                    Date currentTime = new Date();
                    Date nextTime = later.getMethod().nextInvocation(currentTime, tries, later.getDelay());
                    try {
                        Thread.sleep(nextTime.getTime() - currentTime.getTime());
                    } catch (InterruptedException ie) {
                        actionError = true;
                        aee = new ActionExecutionException(ie.getMessage(), later);
                        break;
                    }
                }
            }
        } while(tries >= 0);
        
        if (actionError) {
            RetryLaterException retry = (RetryLaterException) aee.getCause();
            if(retry.isRollBack()) {
                result = rollbackRetryExecute(actor, context, actionTries);
            } else {
                throw aee;
            }
        }
        
        return result;
    }
    
    public static ProcessMessage rollbackRetryExecute(AbstractActionActor actor, ProcessMessage context, int actionTries) throws ActionExecutionException {
        int tries = actionTries;
        ProcessMessage result = null;
        ActionExecutionException aee = null;
        boolean actionError = false;
        do{
            try {
                result = actor.rollback(context);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof RetryLaterException) {
                    RetryLaterException later = (RetryLaterException) e;
                    if (tries < 0) {
                        actionError = true;
                        aee = new ActionExecutionException(later.getMessage(), later.getCause());
                        break;
                    }
                    Date currentTime = new Date();
                    Date nextTime = later.getMethod().nextInvocation(currentTime, tries, later.getDelay());
                    try {
                        Thread.sleep(nextTime.getTime() - currentTime.getTime());
                    } catch (InterruptedException ie) {
                        actionError = true;
                        aee = new ActionExecutionException(ie.getMessage(), ie.getCause());
                        break;
                    }
                    tries = tries - 1;
                }
            }
        } while(tries >= 0);
        
        if (actionError) {
            throw aee;
        }
        
        return result;
    }

}
