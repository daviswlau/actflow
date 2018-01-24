/**   
 * @Title: EngineMessageSender.java
 * @Package com.wanda.trade.platform.engine.utils
 * @Description: 引擎消息发送
 * @author Davis Lau
 * @date 2016年8月27日 下午3:05:44
 * @version V1.0
 */
package org.actflow.platform.engine.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import org.actflow.platform.common.utils.message.MessageSender;
import org.actflow.platform.engine.dto.ProcessMessage;


/** 
 * @ClassName: EngineMessageSender
 * @Description: 引擎消息发送实现类
 * @author Davis Lau
 * @date 2016年8月27日 下午3:05:44
 */
@Service
public class EngineMessageSender extends MessageSender {
    private final static Logger logger = LoggerFactory.getLogger(EngineMessageSender.class);
    
    @Value("${invokeSenderFlag}")
    private boolean invokeSenderFlag = true;
    
    @Value("${tradeMsgTopic}")
    private String tradeMsgTopic;
	
	public void sendMessage(ProcessMessage message) {
		logger.info("TradeMessageSender.sendTradeMessage [{}]", JSONObject.toJSONString(message));
		this.sendMessage(tradeMsgTopic, JSONObject.toJSONString(message));
	}
    
}
