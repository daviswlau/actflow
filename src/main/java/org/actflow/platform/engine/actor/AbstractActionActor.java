package org.actflow.platform.engine.actor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.actflow.platform.engine.core.action.Action;
import org.actflow.platform.engine.core.service.EngineDefinitionLoaderService;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.enums.ProcessEventEnum;
import org.actflow.platform.engine.exception.EngineExceptionHandleUtils;
import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.engine.xstream.definition.ProcessNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @ClassName: AbstractActionActor
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月19日 下午7:29:36
 * @param <T>
 */
public abstract class AbstractActionActor extends UntypedActor implements Action<ProcessMessage> {
	private final static Logger logger = LoggerFactory.getLogger(AbstractActionActor.class);
    
	ActionNode actionNode;
	
	@Autowired
    private EngineDefinitionLoaderService engineDefinitionLoaderService;

	/**
	 * @return the actionNode
	 */
	public ActionNode getActionNode() {
		return actionNode;
	}

	/**
	 * @param actionNode the actionNode to set
	 */
	public void setActionNode(ActionNode actionNode) {
		this.actionNode = actionNode;
	}

	@Override
	public void onReceive(Object object) {
		if (object != null && object instanceof ProcessMessage) {
			try {
				//使用克隆对象
		    	ProcessMessage message = ((ProcessMessage) object).clone();
		    	
		    	actionNode = engineDefinitionLoaderService.getActionDefinition(message.getTradeCode(), message.getId(), message.getActionId());
		    	setActionNode(actionNode);
		    	
		    	ProcessNode processNode = engineDefinitionLoaderService.getProcessDefinition(message.getTradeCode(), message.getId());
		    	if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())) {
		    		if (processNode.actionTries > 0){
		    			message = EngineExceptionHandleUtils.handleRetryExecute(this, message, processNode.actionTries);
		    		} else {
		    			message = this.handle(message);
		    		}
		    	}
		    	if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())){
		    		if (processNode.actionTries > 0){
		    			message = EngineExceptionHandleUtils.rollbackRetryExecute(this, message, processNode.actionTries);
		    		} else {
		    			message = this.rollback(message);
		    		}
				}
		    	ActorRef sender = getContext().parent();
		    	getSender().tell(message, sender);
		    	
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
			
		unhandled(object);
	}
    
}
