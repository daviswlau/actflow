/**   
 * @Title: BizXmlProcessLoaderService.java
 * @Package org.actflow.platform.engine.service.impl
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午9:53:26
 * @version V1.0
 */
package org.actflow.platform.engine.core.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.actflow.platform.common.utils.CollectionUtils;
import org.actflow.platform.engine.xstream.definition.TradeEngine;
import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.engine.xstream.definition.EngineDefinition;
import org.actflow.platform.engine.xstream.definition.ProcessNode;
import org.actflow.platform.engine.xstream.marshaller.EngineXStreamMarshaller;

/** 
 * @ClassName: XmlEngineLoaderService
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午9:53:26
 */
public class XmlEngineLoaderService extends AbstractEngineLoaderService {
	private final static Logger logger = LoggerFactory.getLogger(XmlEngineLoaderService.class);
	
    private String location;
    private EngineXStreamMarshaller engineMarshaller;
    private Map<String, EngineDefinition> tradeEngineDefinitions;
    
    
    public void init() throws IOException {
    	logger.info("engine definitions loading...");
        setTradeEngineDefinitions(new HashMap<String, EngineDefinition>());
        Resource[] resources = null;
        PathMatchingResourcePatternResolver  resolver = new PathMatchingResourcePatternResolver();
        resources = resolver.getResources(location);
        if(CollectionUtils.isNotEmpty(resources)) {
//            File[] files = new File[resources.length];
            for(Resource  resource : resources) {
                TradeEngine tradeEngine = (TradeEngine) engineMarshaller.unmarshalInputStream(resource.getInputStream());
                if (tradeEngine != null && !tradeEngine.business.isEmpty()) {
                    for (String tradeCode : tradeEngine.business) {
                        getTradeEngineDefinitions().put(tradeCode, tradeEngine.definition);
                    }
                }
            }
        }
        logger.info("engine definitions load end.");
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the engineMarshaller
     */
    public EngineXStreamMarshaller getEngineMarshaller() {
        return engineMarshaller;
    }

    /**
     * @param engineMarshaller the engineMarshaller to set
     */
    public void setEngineMarshaller(EngineXStreamMarshaller engineMarshaller) {
        this.engineMarshaller = engineMarshaller;
    }

    /**
     * @return the tradeEngineDefinitions
     */
    public Map<String, EngineDefinition> getTradeEngineDefinitions() {
        return tradeEngineDefinitions;
    }

    /**
     * @param tradeEngineDefinitions the tradeEngineDefinitions to set
     */
    public void setTradeEngineDefinitions(Map<String, EngineDefinition> tradeEngineDefinitions) {
        this.tradeEngineDefinitions = tradeEngineDefinitions;
    }

	@Override
	public EngineDefinition getProcessDefinitions(String tradeCode) {
		return tradeEngineDefinitions.get(tradeCode);
	}

	@Override
	public ProcessNode getProcessDefinition(String tradeCode, String processId) {
		EngineDefinition definition = tradeEngineDefinitions.get(tradeCode);
		for (ProcessNode process : definition.processes) {
			if (StringUtils.equals(process.id, processId)) {
				return process;
			}
		}
		return null;
	}

	@Override
	public ActionNode getActionDefinition(String tradeCode, String processId, String actionId) {
		ProcessNode process = getProcessDefinition(tradeCode, processId);
		for (ActionNode action : process.actions) {
			if (StringUtils.equals(action.id, actionId)) {
				return action;
			}
		}
		return null;
	}

	@Override
	public ActionNode getNextHandleDefinition(String tradeCode, String processId, String actionId) {
		ProcessNode process = getProcessDefinition(tradeCode, processId);
		if (StringUtils.isBlank(actionId)) {
			return process.actions.get(0);
		}
		for (int i=0; i<process.actions.size(); i++) {
			if (StringUtils.equals(process.actions.get(i).id, actionId) && (i+1 < process.actions.size())) {
				return process.actions.get(i+1);
			}
		}
		return null;
	}

	@Override
	public ActionNode getNextRollbackDefinition(String tradeCode, String processId, String actionId) {
		ProcessNode process = getProcessDefinition(tradeCode, processId);
		LinkedList<ActionNode> rollbackList = new LinkedList<ActionNode>();
		for (int i = process.actions.size(); i >= 0; --i) {
			if (StringUtils.isNotBlank(process.actions.get(i).rollback)) {
				rollbackList.add(process.actions.get(i));
			}
		}
		
		if (StringUtils.isBlank(actionId) && rollbackList.size() > 0) {
			return rollbackList.get(0);
		}
		for (int i=0; i<rollbackList.size(); i++) {
			if (StringUtils.equals(rollbackList.get(i).id, actionId) && (i+1 < rollbackList.size())) {
				return rollbackList.get(i+1);
			}
		}
		
		return null;
	}



}
