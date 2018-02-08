/**   
 * @Title: ProcessMessage.java
 * @Package org.actflow.platform.engine.dto
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月26日 上午9:56:43
 * @version V1.0
 */
package org.actflow.platform.engine.dto;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/** 
 * @ClassName: ProcessMessage
 * @Description: 流程消息, dataClass定制消息类型，可以将data字段json对象转换为dataClass对象
 * @author Davis Lau
 * @date 2016年9月26日 上午9:56:43
 */
public class ProcessMessage implements Cloneable, Serializable {
    
	private static final long serialVersionUID = 7216443258972715053L;
	
	//行为
	private String tradeCode;
	private String id;
    private String type;
    private String event;
    private String actionId;
    
    //内容
    private String status;
    private String message;
    private String dataClass;
    private String data;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
     * @return the event
     */
    public String getEvent() {
        return event;
    }
    /**
     * @param event the event to set
     */
    public void setEvent(String event) {
        this.event = event;
    }
	/**
	 * @return the actionId
	 */
	public String getActionId() {
		return actionId;
	}
	/**
	 * @param actionId the actionId to set
	 */
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the tradeCode
	 */
	public String getTradeCode() {
		return tradeCode;
	}
	/**
	 * @param tradeCode the tradeCode to set
	 */
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	
	public String getDataClass() {
		return dataClass;
	}
	
	public void setDataClass(String dataClass) {
		this.dataClass = dataClass;
	}
	
	@Override
	public ProcessMessage clone() throws CloneNotSupportedException {
//		super.clone();
		ProcessMessage clonemessag = new ProcessMessage();
		
		clonemessag.setTradeCode(tradeCode);
		clonemessag.setId(id);
		clonemessag.setType(type);
		clonemessag.setEvent(event);
		clonemessag.setActionId(actionId);
		clonemessag.setStatus(status);
		clonemessag.setMessage(message);
		clonemessag.setData(data);
		clonemessag.setDataClass(dataClass);
		
		return clonemessag;
	}
	
	public Object toObject() throws ClassNotFoundException {
		Class<?> clz = Class.forName(dataClass);
        Object obj = JSON.parseObject(data, clz);
        return obj;
	}

}
