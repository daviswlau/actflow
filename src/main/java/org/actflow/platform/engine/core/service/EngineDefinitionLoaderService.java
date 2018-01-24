/**   
 * @Title: ProcessLoaderService.java
 * @Package org.actflow.platform.engine.core.service
 * @Description: 获取流程服务
 * @author Davis Lau
 * @date 2016年8月27日 上午10:19:47
 * @version V1.0
 */
package org.actflow.platform.engine.core.service;

import org.actflow.platform.engine.xstream.definition.ActionNode;
import org.actflow.platform.engine.xstream.definition.EngineDefinition;
import org.actflow.platform.engine.xstream.definition.ProcessNode;
/** 
 * @ClassName: EngineDefinitionLoaderService
 * @Description: 获取流程服务接口
 * @author Davis Lau
 * @date 2016年8月27日 上午10:19:47
 */
public interface EngineDefinitionLoaderService {
    
    public EngineDefinition getProcessDefinitions(String tradeCode);
    
    public ProcessNode getProcessDefinition(String tradeCode, String processId);
    
    public ActionNode getActionDefinition(String tradeCode, String processId, String actionId);
    
    public ActionNode getNextHandleDefinition(String tradeCode, String processId, String actionId);
    
    public ActionNode getNextRollbackDefinition(String tradeCode, String processId, String actionId);

}
