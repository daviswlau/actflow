/**   
 * @Title: Process.java
 * @Package org.actflow.platform.engine.core.process.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月19日 下午7:33:23
 * @version V1.0
 */
package org.actflow.platform.engine.core.process;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.actflow.platform.engine.akkaspringfactory.ActorSystemFactoryBean;
import org.actflow.platform.engine.core.action.BeanAction;
import org.actflow.platform.engine.core.action.ClassAction;
import org.actflow.platform.engine.core.action.ServiceAction;
import org.actflow.platform.engine.core.constants.EngineConstants;
import org.actflow.platform.engine.core.service.EngineDefinitionLoaderService;
import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.enums.ActionTypeEnum;
import org.actflow.platform.engine.enums.ProcessEventEnum;
import org.actflow.platform.engine.enums.ProcessTypeEnum;
import org.actflow.platform.engine.exception.EngineException;
import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.engine.xstream.definition.ProcessNode;
import org.actflow.platform.enums.MessageStatus;

import akka.actor.ActorRef;
import static akka.pattern.Patterns.ask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;


/** 
 * @ClassName: DefaultProcessExecutor
 * @Description: 流程逻辑
 * @author Davis Lau
 * @date 2016年8月19日 下午7:33:23
 */
@Service
public class DefaultProcessExecutor implements ProcessExecutorService {
	private final static Logger logger = LoggerFactory.getLogger(DefaultProcessExecutor.class);
    
    ProcessNode processNode;
    
    @Inject
	private ActorRef processActor;
    
    @Inject
	private ActorRef classActor;
    
    @Inject
	private ActorRef beanActor;
    
    @Inject
	private ActorRef serviceActor;
    
    @Autowired
    ClassAction classAction;
    
    @Autowired
    BeanAction beanAction;
    
    @Autowired
    ServiceAction serviceAction;
    
    @Autowired
    EngineDefinitionLoaderService engineDefinitionLoaderService;
    
    @Autowired
    ActorSystemFactoryBean actorSystemFactoryBean;

	/**
	 * @return the processNode
	 */
	public ProcessNode getProcessNode() {
		return processNode;
	}

	/**
	 * @param processNode the processNode to set
	 */
	public void setProcessNode(ProcessNode processNode) {
		this.processNode = processNode;
	}
	
//	private static DefaultProcessExecutor processExecutor = null;
//	
//	public static DefaultProcessExecutor getInstance() {
//		if (processExecutor == null) {
//			processExecutor = new DefaultProcessExecutor();
//			processExecutor.engineDefinitionLoaderService = (EngineDefinitionLoaderService) SpringContextUtils.getBeanById(EngineConstants.SPRING_ENGINE_LOADER_CONFIG);
//			processExecutor.processActor = (ActorRef) SpringContextUtils.getBeanById(EngineConstants.SPRING_ACTOR_PROCESS_CONFIG);
//		    processExecutor.classActor = (ActorRef) SpringContextUtils.getBeanById(EngineConstants.SPRING_ACTOR_CLASS_CONFIG);
//		    processExecutor.beanActor = (ActorRef) SpringContextUtils.getBeanById(EngineConstants.SPRING_ACTOR_BEAN_CONFIG);
//		    processExecutor.serviceActor = (ActorRef) SpringContextUtils.getBeanById(EngineConstants.SPRING_ACTOR_SERVICE_CONFIG);
//		}
//		
//		return processExecutor;
//	}

	@Override
	public void asyHandle(ProcessMessage message) throws EngineException {
		message.setEvent(ProcessEventEnum.HANDLE.getValue());
		
		processNode = engineDefinitionLoaderService.getProcessDefinition(message.getTradeCode(), message.getId());
    	setProcessNode(processNode);
    	message.setType(processNode.type);
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
			processActor.tell(message, ActorRef.noSender());
		}
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.PARALLEL.getValue())) {
			for (ActionNode action : processNode.actions) {
				try {
					actionAsyExecute(message, action);
				} catch (CloneNotSupportedException e) {
					logger.error(e.getMessage());
					throw new EngineException(e);
				}
			}
		}
	}

	@Override
	public void asyRollback(ProcessMessage message) throws EngineException {
		message.setEvent(ProcessEventEnum.ROLLBACK.getValue());
		
		processNode = engineDefinitionLoaderService.getProcessDefinition(message.getTradeCode(), message.getId());
    	setProcessNode(processNode);
    	message.setType(processNode.type);
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
			processActor.tell(message, ActorRef.noSender());
		}
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.PARALLEL.getValue())) {
			for (ActionNode action : processNode.actions) {
				if (StringUtils.isNotBlank(action.rollback)) {
					try {
						actionAsyExecute(message, action);
					} catch (CloneNotSupportedException e) {
						logger.error(e.getMessage());
						throw new EngineException(e);
					}
				}
			}
		}
		
	}
	
	/**
	 * akka action async execute
	 * @param message
	 * @param actionNode
	 * @throws CloneNotSupportedException 
	 */
	private void actionAsyExecute(ProcessMessage oriMessage, ActionNode actionNode) throws CloneNotSupportedException {
		if (actionNode != null) {
			ProcessMessage message = oriMessage.clone();
			message.setActionId(actionNode.id);
			if (StringUtils.equals(actionNode.type, ActionTypeEnum.CLASS.getValue())) {
				classActor.tell(message, ActorRef.noSender());
			}
			if (StringUtils.equals(actionNode.type, ActionTypeEnum.BEAN.getValue())) {
				beanActor.tell(message, ActorRef.noSender());
			}
			if (StringUtils.equals(actionNode.type, ActionTypeEnum.SERVICE.getValue())) {
				serviceActor.tell(message, ActorRef.noSender());
			}
		}
	}

	@Override
	public ProcessMessage syHandle(ProcessMessage message) throws EngineException {
		message.setEvent(ProcessEventEnum.HANDLE.getValue());
		
		processNode = engineDefinitionLoaderService.getProcessDefinition(message.getTradeCode(), message.getId());
    	setProcessNode(processNode);
    	message.setType(processNode.type);
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
			for (ActionNode action : processNode.actions) {
				message = actionSyExecute(message, action);
				if (!StringUtils.equals(message.getStatus(), String.valueOf(MessageStatus.SUCCESS.getStatus()))) {
					logger.error(message.getMessage());
					break;
				}
			}
		}
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.PARALLEL.getValue())) {
			for (ActionNode action : processNode.actions) {
				try {
					actionAsyExecute(message, action);
				} catch (CloneNotSupportedException e) {
					logger.error(e.getMessage());
					throw new EngineException(e);
				}
			}
		}
		
		return message;
	}

	@Override
	public ProcessMessage syRollback(ProcessMessage message) throws EngineException {
		message.setEvent(ProcessEventEnum.ROLLBACK.getValue());
		
		processNode = engineDefinitionLoaderService.getProcessDefinition(message.getTradeCode(), message.getId());
    	setProcessNode(processNode);
    	message.setType(processNode.type);
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.SEQUENCE.getValue())) {
			
			ActionNode actionNode = engineDefinitionLoaderService.getNextRollbackDefinition(message.getTradeCode(), message.getId(), null);
			while(actionNode != null) {
				message = actionSyExecute(message, actionNode);
				if (!StringUtils.equals(message.getStatus(), String.valueOf(MessageStatus.SUCCESS.getStatus()))) {
					logger.error(message.getMessage());
					break;
				}
				actionNode = engineDefinitionLoaderService.getNextRollbackDefinition(message.getTradeCode(), message.getId(), actionNode.id);
			}
		}
		if (message != null && StringUtils.equals(message.getType(), ProcessTypeEnum.PARALLEL.getValue())) {
			for (ActionNode action : processNode.actions) {
				if (StringUtils.isNotBlank(action.rollback)) {
					try {
						actionAsyExecute(message, action);
					} catch (CloneNotSupportedException e) {
						logger.error(e.getMessage());
						throw new EngineException(e);
					}
				}
			}
		}
		return message;
	}
	
	/**
	 * akka action sync execute with scala future - block mode
	 * @param message
	 * @param actionNode
	 */
	private ProcessMessage blockedActionSyExecute(ProcessMessage oriMessage, ActionNode actionNode) {
		try {
			ProcessMessage message = oriMessage.clone();
			if (actionNode != null) {
				ProcessMessage result = null;
				message.setActionId(actionNode.id);
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.CLASS.getValue())) {
					Future<Object> future = Patterns.ask(classActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
					result = (ProcessMessage) Await.result(future, FiniteDuration.create(EngineConstants.TIME_OUT_SECONDS, TimeUnit.SECONDS));
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.BEAN.getValue())) {
					Future<Object> future = Patterns.ask(beanActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
					result = (ProcessMessage) Await.result(future, FiniteDuration.create(EngineConstants.TIME_OUT_SECONDS, TimeUnit.SECONDS));
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.SERVICE.getValue())) {
					Future<Object> future = Patterns.ask(serviceActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
					result = (ProcessMessage) Await.result(future, FiniteDuration.create(EngineConstants.TIME_OUT_SECONDS, TimeUnit.SECONDS));
				}
				if (result != null) {
					message = result;
				}
			}
			return message;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return oriMessage;
	}
	
	/**
	 * akka action sync execute with akka future
	 * @param message
	 * @param actionNode
	 */
	private ProcessMessage akkaActionSyExecute(ProcessMessage oriMessage, ActionNode actionNode) {
		try {
			ProcessMessage message = oriMessage.clone();
			if (actionNode != null) {
				message.setActionId(actionNode.id);
				final ExecutionContext ec = actorSystemFactoryBean.getObject().dispatcher();
				Future<Object> future = null;
				AkkaResult<Object> akkaResult = new AkkaResult<Object>();
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.CLASS.getValue())) {
					future = ask(classActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.BEAN.getValue())) {
					future = ask(beanActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.SERVICE.getValue())) {
					future = ask(serviceActor, message, EngineConstants.TIME_OUT_MILLISECONDS);
				}
				if (future != null) {
					future.onSuccess(akkaResult, ec);
					akkaResult.getLatch().await(EngineConstants.TIME_OUT_SECONDS, TimeUnit.SECONDS);
				}
				
				if (akkaResult.getMessage() != null) {
					message = akkaResult.getMessage();
				}
			}
			return message;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return oriMessage;
	}
	
	class AkkaResult<Object> extends OnSuccess<Object> {
		
		private ProcessMessage message;
		private CountDownLatch latch = new CountDownLatch(1);
		
		@Override
		public void onSuccess(Object result) throws Throwable {
			this.message = (ProcessMessage) result;
			latch.countDown();
//			logger.info("Process[{}] Action[{}] get result: [{}]", this.message.getId(), this.message.getActionId(), JSON.toJSONString(this.message));
		}

		/**
		 * @return the message
		 */
		public ProcessMessage getMessage() {
			return message;
		}

		/**
		 * @return the latch
		 */
		public CountDownLatch getLatch() {
			return latch;
		}
		
	}
	
	/**
	 * sync execute
	 * @param message
	 * @param actionNode
	 */
	private ProcessMessage actionSyExecute(ProcessMessage oriMessage, ActionNode actionNode) {
		try {
			ProcessMessage message = oriMessage.clone();
			if (actionNode != null) {
				message.setActionId(actionNode.id);
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.CLASS.getValue())) {
					classAction.setActionNode(actionNode);
					if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())){
						message = classAction.handle(message);
					} else if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())){
						message = classAction.rollback(message);
					}
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.BEAN.getValue())) {
					beanAction.setActionNode(actionNode);
					if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())){
						message = beanAction.handle(message);
					} else if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())){
						message = beanAction.rollback(message);
					}
				}
				if (StringUtils.equals(actionNode.type, ActionTypeEnum.SERVICE.getValue())) {
					serviceAction.setActionNode(actionNode);
					if (StringUtils.equals(message.getEvent(), ProcessEventEnum.HANDLE.getValue())){
						message = serviceAction.handle(message);
					} else if (StringUtils.equals(message.getEvent(), ProcessEventEnum.ROLLBACK.getValue())){
						message = serviceAction.rollback(message);
					}
				}
			}
			return message;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return oriMessage;
	}
	
	
	
}


