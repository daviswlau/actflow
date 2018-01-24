/**   
 * @Title: TradeEngine.java
 * @Package org.actflow.platform.engine.xstream.model
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午2:24:00
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.definition;

import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** 
 * @ClassName: TradeEngine
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午2:24:00
 */
@XStreamAlias("tradeEngine")
public class TradeEngine {

    @XStreamAlias("business")
    public Set<String> business;
    
    @XStreamAlias("definition")
    public EngineDefinition definition;

}
