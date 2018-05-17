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
