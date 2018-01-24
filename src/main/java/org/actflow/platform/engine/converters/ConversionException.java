/**   
 * @Title: ConversionException.java
 * @Package org.actflow.platform.engine.converter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月9日 上午9:45:16
 * @version V1.0
 */
package org.actflow.platform.engine.converters;

/** 
 * @ClassName: ConversionException
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月9日 上午9:45:16
 */
public class ConversionException extends RuntimeException {

    private static final long serialVersionUID = -3952656853584920000L;

    public ConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConversionException(String msg) {
        super(msg);
    }
}
