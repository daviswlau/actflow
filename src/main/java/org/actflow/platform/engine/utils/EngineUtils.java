/**   
 * @Title: EngineUtils.java
 * @Package org.actflow.platform.engine.utils
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年10月25日 上午11:25:30
 * @version V1.0
 */
package org.actflow.platform.engine.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/** 
 * @ClassName: EngineUtils
 * @Description: 流程引擎工具类
 * @author Davis Lau
 * @date 2016年10月25日 上午11:25:30
 */
public class EngineUtils {
    private final static Logger logger = LoggerFactory.getLogger(EngineUtils.class);
    
    public static JSONObject deepMerge(JSONObject source, JSONObject target) {
        Date startTime = new Date();
//        for (String key : source.keySet()) {
        for (Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (source.get(key) instanceof Map && target.get(key) instanceof Map) {
                JSONObject targetChild = (JSONObject) target.get(key);
                JSONObject newChild = (JSONObject) source.get(key);
                target.put(key, deepMerge(targetChild, newChild));
            }  else if (source.get(key) instanceof List && target.get(key) instanceof List) {
                JSONArray targetChildren = (JSONArray) target.get(key);
                JSONArray sourceChildren = (JSONArray) source.get(key);
                JSONArray tempList = new JSONArray();
                for (Object targetItem : targetChildren) {
                    JSONObject jsonTargetItem = (JSONObject) targetItem;
                    JSONObject newJsonItem = null;
                    for (Object sourceItem : sourceChildren) {
                        JSONObject jsonSourceItem = (JSONObject) sourceItem;
                        if (StringUtils.isNotBlank(jsonSourceItem.getString("entryType")) && 
                                StringUtils.isNotBlank(jsonTargetItem.getString("entryType")) && 
                                StringUtils.equals(jsonSourceItem.getString("entryType"), jsonTargetItem.getString("entryType"))) {
                            newJsonItem = deepMerge(jsonSourceItem, jsonTargetItem);
                        }
                    }
                    if (newJsonItem != null) {
                        tempList.add(newJsonItem);
                    } else {
                        tempList.add(jsonTargetItem);
                    }
                }
                
                targetChildren.clear();
                targetChildren.addAll(tempList);
            } else {
                target.put(key, source.get(key));
            }
        }
        
        long costtime = new Date().getTime() - startTime.getTime();
        logger.info(String.format("Deep Merge Trade JSON, cost time (ms) : %d", costtime));
        
        return target;
    }
    
}
