/**   
 * @Title: ProcessExecutorService.java
 * @Package org.actflow.platform.engine.core.process.service
 * @Description: 流程执行服务接口
 * @author Davis Lau
 * @date 2016年8月26日 上午8:26:02
 * @version V1.0
 */
package org.actflow.platform.engine.core.process;

import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.exception.EngineException;

/** 
 * @ClassName: ProcessExecutorService
 * @Description: 流程执行服务接口定义
 * @author Davis Lau
 * @date 2016年8月26日 上午8:26:02
 */
public interface ProcessExecutorService {

    /**
     * @Title: ProcessService
     * @Description: 执行action
     * @param @param process
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    public ProcessMessage syHandle(ProcessMessage message) throws EngineException;
    
    /**
     * @Title: ProcessService
     * @Description: 回滚action
     * @param @param process
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    public ProcessMessage syRollback(ProcessMessage message) throws EngineException;
    
    /**
     * @Title: ProcessService
     * @Description: 执行action
     * @param @param process
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    public void asyHandle(ProcessMessage message) throws EngineException;
    
    /**
     * @Title: ProcessService
     * @Description: 回滚action
     * @param @param process
     * @param @throws ActionExecutionException
     * @return 
     * @throws
     */
    public void asyRollback(ProcessMessage message) throws EngineException;
    
}
