/**   
 * @Title: ClassAction.java
 * @Package org.actflow.platform.engine.core.action.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:05
 * @version V1.0
 */
package org.actflow.platform.engine.core.action;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.exception.ActionExecutionException;
import org.actflow.platform.engine.exception.RetryLaterException;
import org.actflow.platform.engine.exception.RetryLaterException.Method;

/** 
 * @ClassName: ClassAction
 * @Description: 流程动作调用外部class
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:05
 */
@Service
@Scope("prototype")
public class ClassAction extends AbstractAction {
    private final static Logger logger = LoggerFactory.getLogger(ClassAction.class);

    /*
     * (non-Javadoc)
     * <p>Title: handle</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#handle(java.lang.Object)
     */
    @Override
    public ProcessMessage handle(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
    	ProcessMessage result = null;
        try {
            Class<?> clz = Class.forName(getActionNode().handle);
            Object obj = clz.newInstance();
            if (obj instanceof ActionExecutor) {
            	ActionExecutor action = (ActionExecutor) obj;
                if (StringUtils.isNotBlank(getActionNode().name)) {
                    action.inject(getActionNode().name);
                }
                result = action.handle(context);
            }
//            logger.info("[ClassAction handle]调用成功: {}, {}", getActionNode().handle, result);
        } catch (Exception e) {
            String errMsg = String.format("[ClassAction handle][%s]调用异常，参数: %s, 返回信息:%s", getActionNode().handle, context, result);
            logger.error(errMsg, e);
            //throw new ActionExecutionException(e.getMessage());

            RetryLaterException later = new RetryLaterException(errMsg);
            later.setMethod(Method.LINEAR);
            later.setDelay(RetryLaterException.DEFAULT_DELAY);
            
            //是否可以单独rollback
            if (getActionNode().soloRollback && StringUtils.isNotBlank(getActionNode().rollback)) {
            	later.setRollBack(true); 
            }
            
            throw later;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * <p>Title: rollback</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#rollback(java.lang.Object)
     */
    @Override
    public ProcessMessage rollback(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
        if (StringUtils.isEmpty(getActionNode().rollback)) {
            return null;
        }
        ProcessMessage result = null;
        try {
            Class<?> clz = Class.forName(getActionNode().rollback);
            Object obj = clz.newInstance();
            if (obj instanceof ActionExecutor) {
            	ActionExecutor action = (ActionExecutor) obj;
                if (StringUtils.isNotBlank(getActionNode().name)) {
                    action.inject(getActionNode().name);
                }
                result = action.rollback(context);
            }
//            logger.info("[ClassAction rollback]调用成功: {}", result);
        } catch (Exception e) {
            String errMsg = String.format("[ClassAction rollback][%s]调用参数: %s, 返回信息:%s", getActionNode().rollback, context, result);
            logger.error(errMsg, e);

            RetryLaterException later = new RetryLaterException(errMsg);
            later.setMethod(Method.LINEAR);
            later.setDelay(RetryLaterException.DEFAULT_DELAY);
            later.setRollBack(false);
            throw later;
        }
        return result;
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

}
