/**   
 * @Title: ServiceActor.java
 * @Package org.actflow.platform.engine.core.action.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:24
 * @version V1.0
 */
package org.actflow.platform.engine.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.actflow.platform.engine.core.action.ServiceAction;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.exception.ActionExecutionException;
import org.actflow.platform.engine.exception.RetryLaterException;

/** 
 * @ClassName: ServiceActor
 * @Description: 流程动作调用rest服务
 * @author Davis Lau
 * @date 2016年8月23日 上午9:52:24
 */
//@Actor
@Component("serviceActor")
@Scope("prototype")
public class ServiceActor extends AbstractActionActor {
    private final static Logger logger = LoggerFactory.getLogger(ServiceActor.class);
    
    @Autowired
    private ServiceAction serviceAction;

    /*
     * (non-Javadoc)
     * <p>Title: handle</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#handle(java.lang.Object)
     */
    @Override
    public ProcessMessage handle(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
    	serviceAction.setActionNode(this.getActionNode());
    	return serviceAction.handle(context);
    }

	/*
     * (non-Javadoc)
     * <p>Title: rollback</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#rollback(java.lang.Object)
     */
    @Override
    public ProcessMessage rollback(ProcessMessage context) throws RetryLaterException, ActionExecutionException {
    	serviceAction.setActionNode(this.getActionNode());
    	return serviceAction.rollback(context);
    }

	/*
     * (non-Javadoc)
     * <p>Title: retry</p> 
     * <p>Description: </p>
     * @see org.actflow.platform.engine.core.action.definition.Action#retry(java.lang.Object)
     */
    @Override
    public ProcessMessage retry(ProcessMessage context) throws ActionExecutionException {
    	logger.debug("retry");
        return serviceAction.retry(context);
    }
    
}
