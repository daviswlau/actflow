/**   
 * @Title: SpringContextUtils.java
 * @Package org.actflow.platform.engine.utils
 * @Description: String bean工具
 * @author Davis Lau
 * @date 2016年8月27日 下午4:36:31
 * @version V1.0
 */
package org.actflow.platform.engine.utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** 
 * @ClassName: SpringContextUtils
 * @Description: 获取ApplicationContext和Spring bean的工具类
 * @author Davis Lau
 * @date 2016年8月27日 下午4:36:31
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext arg) throws BeansException {
        applicationContext = arg;
    }

    /**
     * @Title: SpringContextUtils
     * @Description: get applicationContext object
     * @param @return
     * @return 
     * @throws
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @Title: SpringContextUtils
     * @Description: get Spring bean by bean id
     * @param @param id
     * @param @return
     * @return 
     * @throws
     */
    public static Object getBeanById(String id) {
        return applicationContext.getBean(id);
    }

    /**
     * @Title: SpringContextUtils
     * @Description: get spring bean by bean class
     * @param @param cls
     * @param @return
     * @return 
     * @throws
     */
    public static Object getBeanByClass(Class cls) {
        return applicationContext.getBean(cls);
    }

    /**
     * @Title: SpringContextUtils
     * @Description: get spring bean's children bean
     * @param @param cls
     * @param @return
     * @return 
     * @throws
     */
    public static Map getBeansByClass(Class cls) {
        return applicationContext.getBeansOfType(cls);
    }

}
