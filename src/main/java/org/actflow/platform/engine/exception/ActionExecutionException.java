/**   
 * @Title: ActionExecutionException.java
 * @Package org.actflow.platform.engine.exception
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午8:48:08
 * @version V1.0
 */
package org.actflow.platform.engine.exception;

/** 
 * @ClassName: ActionExecutionException
 * @Description: 动作执行异常
 * @author Davis Lau
 * @date 2016年8月23日 上午8:48:08
 */
public class ActionExecutionException extends RuntimeException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    public ActionExecutionException() {
    }
    
    public ActionExecutionException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

    public ActionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionExecutionException(String message) {
        super(message);
    }

    public ActionExecutionException(Throwable cause) {
        super(cause);
    }
}
