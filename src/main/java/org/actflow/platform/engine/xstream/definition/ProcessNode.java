/**   
 * @Title: ProcessNode.java
 * @Package org.actflow.platform.engine.xstream.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午8:52:50
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.definition;

import java.util.LinkedList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** 
 * @ClassName: ProcessNode
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午8:52:50
 */
@XStreamAlias("process")
public class ProcessNode {
    
    @XStreamAsAttribute
    public String id;
    
    @XStreamAsAttribute
    public String type;
    
    @XStreamAsAttribute
    public String name;
    
    @XStreamAsAttribute
    public int actionTries = 0;
    
    @XStreamAsAttribute
    public String messageClass;
    
    @XStreamAsAttribute
    public boolean autoRollback = false;
    
    @XStreamAsAttribute
    public boolean parallelMerge = false;
    
    @XStreamImplicit(itemFieldName="action")
    public LinkedList<ActionNode> actions;

}
