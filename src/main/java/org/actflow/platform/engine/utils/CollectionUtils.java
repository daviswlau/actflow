package org.actflow.platform.engine.utils;

import java.util.Map;

/**
 * 
 * @ClassName: CollectionUtils
 * @Description: 扩展集合工具类
 * @author zhangxinfa
 * @date 2016年9月7日 下午6:51:52
 */
public class CollectionUtils  extends  org.apache.commons.collections.CollectionUtils{
    
    public static   boolean  isEmpty(Map<Object,Object> map) {
        return  map == null || map.size()  == 0 ?true : false;
    }
    
    public static boolean  isNotEmpty(Map<Object,Object> map) {
        return !isEmpty(map);
    }
    
    
    public static boolean isEmpty(Object[] arry) {
        return arry == null || arry.length == 0 ? true : false;
    }
    
    public static boolean isNotEmpty(Object[] arry){
        return !isEmpty(arry);
    }

}
