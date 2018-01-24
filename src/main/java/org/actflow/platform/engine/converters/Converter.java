/**   
 * @Title: Converter.java
 * @Package org.actflow.platform.engine.converter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月9日 上午9:43:16
 * @version V1.0
 */
package org.actflow.platform.engine.converters;


/** 
 * @ClassName: Converter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月9日 上午9:43:16
 */
public interface Converter<SOURCE, TARGET> {
    
    TARGET convert(SOURCE source) throws ConversionException;
    
    TARGET convert(SOURCE source, TARGET target) throws ConversionException;
    
}
