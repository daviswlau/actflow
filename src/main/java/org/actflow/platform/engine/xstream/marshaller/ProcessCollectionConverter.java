/**   
 * @Title: ProcessCollectionConverter.java
 * @Package org.actflow.platform.engine.xstream.marshaller
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午7:11:39
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.marshaller;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.actflow.platform.engine.xstream.definition.ProcessNode;


/** 
 * @ClassName: ProcessCollectionConverter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午7:11:39
 */
public class ProcessCollectionConverter extends AbstractCollectionConverter{

    public ProcessCollectionConverter(Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(Class clazz) {
        // TODO Auto-generated method stub
        return clazz.equals(ArrayList.class);
    }

    @Override
    public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        List<ProcessNode> processes = new ArrayList<ProcessNode>();
        while (reader.hasMoreChildren()) { 
            reader.moveDown();
            ProcessNode process = new ProcessNode();
            reader.getAttribute("id");
            processes.add(process);            
            reader.moveUp();
        }
        return processes;
    }

}
