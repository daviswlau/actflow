/*
 * Copyright 2012-2014 Wanda.cn All right reserved. This software is the confidential and
 * proprietary information of Wanda.cn ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Wanda.cn.
 */
package org.actflow.platform.http.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * Adapter的Response描述
 * 
 * @author Davis Lau
 */
public class AdapterBaseResponse {
	/**
	 * 状态
	 */
	private int status;

	/**
	 * 消息
	 */
	private String message;

	private JSONObject response;

	public AdapterBaseResponse() {

	}

	public AdapterBaseResponse(String result) {
		this.response = JSONObject.parseObject(result);
		this.status = response.getIntValue("status");
		this.message = response.getString("message");
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return this.status == 200;
	}

	public JSONObject getResponse() {
		return response;
	}

	public void setResponse(JSONObject response) {
		this.response = response;
	}

}
