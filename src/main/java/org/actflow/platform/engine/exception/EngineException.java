/**   
 * @Title: EngineException.java
 * @Package org.actflow.platform.engine.exception
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月23日 上午8:48:08
 * @version V1.0
 */
package org.actflow.platform.engine.exception;

/** 
 * @ClassName: EngineException
 * @Description: 引擎异常
 * @author Davis Lau
 * @date 2016年8月23日 上午8:48:08
 */
public class EngineException extends Exception {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;

    public EngineException() {
    }
    
    public EngineException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineException(String message) {
        super(message);
    }

    public EngineException(Throwable cause) {
        super(cause);
    }
}
