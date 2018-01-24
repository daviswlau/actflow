/**   
 * @Title: EngineDefinition.java
 * @Package org.actflow.platform.engine.xstream.model
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午5:40:20
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.definition;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** 
 * @ClassName: EngineDefinition
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午5:40:20
 */
@XStreamAlias("definition")
public class EngineDefinition {

    @XStreamImplicit(itemFieldName="process")
    public List<ProcessNode> processes;

}
