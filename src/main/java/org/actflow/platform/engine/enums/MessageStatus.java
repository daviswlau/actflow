package org.actflow.platform.engine.enums;


/**
 * 消息状态码
 * 
 * @author Davis Lau
 * @since 2017-11-11
 */
public enum MessageStatus {

	SUCCESS(200, "成功"),
	REQUEST_ERROR(400, "请求参数错误"),
	SERVER_ERROR(500, "服务器内部错误"),
	REMOTE_FAIL(501, "请求远程服务异常"),
	TIME_OUT(504, "请求远程服务超时");
	
	private int status;
	private String message;
	
	MessageStatus(int status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static MessageStatus findByStatus(int status) {
        for (MessageStatus code : MessageStatus.values()) {
            if ( code.getStatus() == status ) {
                return code;
            }
        }
        return null;
    }
    
	@Override
	public String toString() {
		return this.getStatus() + " - " + this.getMessage();
	}
}
