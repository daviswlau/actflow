/**   
 * @Title: ActionOperateEnum.java
 * @Package org.actflow.platform.engine.enums
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年12月1日 下午4:36:17
 * @version V1.0
 */
package org.actflow.platform.engine.enums;

/** 
 * @ClassName: ActionOperateEnum
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年12月1日 下午4:36:17
 */
public enum ActionTypeEnum {
    CLASS("class"),
    BEAN("bean"),
    SERVICE("service");
    
    private String value;

    private ActionTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
