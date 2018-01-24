/**   
 * @Title: AttributeConverter.java
 * @Package org.actflow.platform.engine.xstream.marshaller
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午6:08:13
 * @version V1.0
 */
package org.actflow.platform.engine.xstream.marshaller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/** 
 * @ClassName: AttributeConverter
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年9月19日 下午6:08:13
 */
public class IdMapAttributeConverter extends AbstractCollectionConverter{

    public IdMapAttributeConverter(Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(HashMap.class) || clazz.equals(Hashtable.class) || clazz.equals(HashSet.class)
                || clazz.getName().equals("java.util.LinkedHashMap") || clazz.getName().equals("sun.font.AttributeMap");
    }

    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Set<String> set = new HashSet<String>();
        populateSet(reader, context, set);
        return set;
    }

    private void populateSet(HierarchicalStreamReader reader, UnmarshallingContext context, Set<String> set) {
        while (reader.hasMoreChildren()) { 
            reader.moveDown();
            String id = reader.getAttribute("id");
            set.add(id);            
            reader.moveUp();
        }
    }

   

}
