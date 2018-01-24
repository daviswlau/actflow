/**   
 * @Title: EngineXStreamMarshaller.java
 * @Package org.actflow.platform.engine.xstream.marshaller
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午4:24:59
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.marshaller;

import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.XStream;


/** 
 * @ClassName: EngineXStreamMarshaller
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午4:24:59
 */
public class EngineXStreamMarshaller extends XStreamMarshaller {

    @Override
    protected void configureXStream(final XStream xstream) {
        super.configureXStream(xstream);
        xstream.ignoreUnknownElements();
    }
}