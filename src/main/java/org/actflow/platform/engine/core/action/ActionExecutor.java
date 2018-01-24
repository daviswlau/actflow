/**   
 * @Title: ActionExecutor.java
 * @Package com.wanda.trade.platform.engine.core.action
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午10:10:50
 * @version V1.0
 */
package org.actflow.platform.engine.core.action;

import org.actflow.platform.engine.dto.ProcessMessage;

/** 
 * @ClassName: ActionExecutor
 * @Description: 流程的动作模板接口，可供class和spring bean使用
 * @author Davis Lau
 * @date 2016年8月23日 上午10:10:50
 */
public interface ActionExecutor {

    /**
     * @Title: ActionExecutor
     * @Description: 执行逻辑
     * @param ProcessMessage
     * @return ProcessMessage
     * @throws Exception
     */
	ProcessMessage handle(ProcessMessage message) throws Exception;

    /**
     * @Title: ActionExecutor
     * @Description: 回滚逻辑
     * @param ProcessMessage
     * @return ProcessMessage
     * @throws Exception
     */
	ProcessMessage rollback(ProcessMessage message) throws Exception;
    
    /**
     * @Title: ActionExecutor
     * @Description: 注入逻辑
     * @param @param injection
     * @return 
     * @throws
     */
    void inject(String injection);

}
