/**
 * 
 */
package org.actflow.platform.engine.core.process;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import org.actflow.platform.engine.akkaspringfactory.Actor;
import org.actflow.platform.engine.core.service.EngineDefinitionLoaderService;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.enums.ActionTypeEnum;
import org.actflow.platform.engine.enums.ProcessEventEnum;
import org.actflow.platform.engine.enums.ProcessTypeEnum;
import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.enums.MessageStatus;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @author Davis Lau
 *
 */
@Actor
public class Process extends UntypedActor {
	
	private final static Logger logger = LoggerFactory.getLogger(Process.class);
	
	@Inject
    private EngineDefinitionLoaderService engineDefinitionLoaderService;
	
	@Inject
	private ActorRef classActor;
    
    @Inject
	private ActorRef beanActor;
    
    @Inject
	private ActorRef serviceActor;

	@Override
	public void onReceive(Object object) {
		if (object != null && object instanceof ProcessMessage) {
			try {
				//使用克隆对象
		    	ProcessMessage message = ((ProcessMessage) object).clone();
		    	
		    	ActionNode nextAction = null;
		    	if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())) {
		    		nextAction = engineDefinitionLoaderService.getNextHandleDefinition(message.getTradeCode(), message.getId(), message.getActionId());
		    	}
		    	if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())){
		    		nextAction = engineDefinitionLoaderService.getNextRollbackDefinition(message.getTradeCode(), message.getId(), message.getActionId());
		    	}
		    	
		    	logger.info("next action : [{}]", JSON.toJSON(nextAction));
		    	if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
		    		if (nextAction != null && StringUtils.equals(message.getStatus(), String.valueOf(MessageStatus.SUCCESS.getStatus()))) {
		    			message.setActionId(nextAction.id);
		    			if (StringUtils.equals(nextAction.type, ActionTypeEnum.CLASS.getValue())) {
		    				classActor.tell(message, getSelf());
		    			}
		    			if (StringUtils.equals(nextAction.type, ActionTypeEnum.BEAN.getValue())) {
		    				beanActor.tell(message, getSelf());
		    			}
		    			if (StringUtils.equals(nextAction.type, ActionTypeEnum.SERVICE.getValue())) {
		    				serviceActor.tell(message, getSelf());
		    			}
		    		}
	    		}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		unhandled(object);
	}

}
