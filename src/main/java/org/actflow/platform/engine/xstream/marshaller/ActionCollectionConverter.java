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
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.actflow.platform.engine.xstream.definition.ActionNode;


/** 
 * @ClassName: ProcessCollectionConverter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午7:11:39
 */
public class ActionCollectionConverter extends AbstractCollectionConverter{

    public ActionCollectionConverter(Mapper mapper) {
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
        List<ActionNode> actions = new LinkedList<ActionNode>();
        while (reader.hasMoreChildren()) { 
            reader.moveDown();
            String actionType = reader.getAttribute("type");
            System.out.println(actionType);
            reader.moveUp();
        }
        return actions;
    }

}
