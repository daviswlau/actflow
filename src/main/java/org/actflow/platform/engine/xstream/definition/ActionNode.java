/**   
 * @Title: ActionNode.java
 * @Package org.actflow.platform.engine.xstream.definition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午8:53:07
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.definition;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/** 
 * @ClassName: ActionNode
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月20日 上午8:53:07
 */
@XStreamAlias("action")
public class ActionNode {
    
    @XStreamAsAttribute
    public String id;
    
    @XStreamAsAttribute
    public String type;
    
    @XStreamAsAttribute
    public String name;
    
    @XStreamAsAttribute
    public String httpMethod;
    
    @XStreamAsAttribute
    public String handle;
    
    @XStreamAsAttribute
    public String rollback;
    
    @XStreamAsAttribute
    public boolean soloRollback = false;
}
