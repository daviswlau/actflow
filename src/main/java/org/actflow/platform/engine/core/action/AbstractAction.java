package org.actflow.platform.engine.core.action;

import org.actflow.platform.engine.dto.ProcessMessage;
import org.actflow.platform.engine.xstream.definition.ActionNode;

/**
 * @ClassName: AbstractAction
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月19日 下午7:29:36
 * @param <T>
 */
public abstract class AbstractAction implements Action<ProcessMessage> {
    
	ActionNode actionNode;

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
    
}
