/**
 * 
 */
package org.actflow.platform.engine.actor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import org.actflow.platform.engine.akkaspringfactory.SpringProps;
import org.actflow.platform.engine.core.service.EngineDefinitionLoaderService;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.enums.ActionTypeEnum;
import org.actflow.platform.engine.enums.MessageStatus;
import org.actflow.platform.engine.enums.ProcessEventEnum;
import org.actflow.platform.engine.enums.ProcessTypeEnum;
import org.actflow.platform.engine.xstream.definition.ActionNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @author Davis Lau
 *
 */
// @Actor
@Component("processActor")
@Scope("prototype")
public class ProcessActor extends UntypedActor {

	private final static Logger logger = LoggerFactory.getLogger(Process.class);

	@Autowired
	private EngineDefinitionLoaderService engineDefinitionLoaderService;

	ActorRef classActor = getContext().actorOf(SpringProps.create(true, getContext().system(), "classActor"), "classAct");

	ActorRef beanActor = getContext().actorOf(SpringProps.create(true, getContext().system(), "beanActor"), "beanAct");

	ActorRef serviceActor = getContext().actorOf(SpringProps.create(true, getContext().system(), "serviceActor"), "serviceAct");

	@Override
	public void onReceive(Object object) {
		if (object != null && object instanceof ProcessMessage) {
			try {
				// 使用克隆对象
				ProcessMessage message = ((ProcessMessage) object).clone();

				ActionNode nextAction = null;
				if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())) {
					nextAction = engineDefinitionLoaderService.getNextHandleDefinition(message.getTradeCode(),
							message.getId(), message.getActionId());
				}
				if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())) {
					nextAction = engineDefinitionLoaderService.getNextRollbackDefinition(message.getTradeCode(),
							message.getId(), message.getActionId());
				}

				if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
					if (nextAction != null && StringUtils.equals(message.getStatus(),
							String.valueOf(MessageStatus.SUCCESS.getStatus()))) {
						logger.debug("next action : [{}]", JSON.toJSON(nextAction));
						ActorRef sender = getContext().parent();
						message.setActionId(nextAction.id);
						if (StringUtils.equals(nextAction.type, ActionTypeEnum.CLASS.getValue())) {
							classActor.tell(message, sender);
						}
						if (StringUtils.equals(nextAction.type, ActionTypeEnum.BEAN.getValue())) {
							beanActor.tell(message, sender);
						}
						if (StringUtils.equals(nextAction.type, ActionTypeEnum.SERVICE.getValue())) {
							serviceActor.tell(message, sender);
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
