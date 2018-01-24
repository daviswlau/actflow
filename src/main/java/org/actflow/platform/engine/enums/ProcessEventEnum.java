/**   
 * @Title: ProcessTypeEnmu.java
 * @Package org.actflow.platform.engine.enums
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月24日 下午2:40:25
 * @version V1.0
 */
package org.actflow.platform.engine.enums;

/** 
 * @ClassName: ProcessTypeEnmu
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月24日 下午2:40:25
 */
public enum ProcessEventEnum {
    HANDLE("handle"),
    ROLLBACK("rollback"),
    WAIT("wait");
    
    private String value;

    private ProcessEventEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
