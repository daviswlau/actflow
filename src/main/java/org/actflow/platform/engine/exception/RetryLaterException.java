/**
 * @Title: RetryLaterException.java
 * @Package org.actflow.platform.engine.exception
 * @Description: TODO
 * @author Davis Lau
 * @date 2016年8月19日 下午7:23:33
 * @version V1.0
 */
package org.actflow.platform.engine.exception;

import java.util.Date;
import java.util.Random;

/**
 * @ClassName: RetryLaterException
 * @Description: 异常重试回滚
 * @author Davis Lau
 * @date 2016年8月19日 下午7:23:33
 */
public class RetryLaterException extends RuntimeException {

    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 1L;
    
    public static final long DEFAULT_DELAY = 2000L;
    private long delay;
    private RetryLaterException.Method method;
    private boolean RollBack = false;

    public RetryLaterException() {
        this((String) null);
    }

    public RetryLaterException(String msg) {
        this(msg, (Throwable) null);
    }

    public RetryLaterException(String msg, Throwable cause) {
        super(msg, cause);
        this.delay = DEFAULT_DELAY;
        this.method = RetryLaterException.Method.EXPONENTIAL;
        this.RollBack = true;
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(long delay) {
        if (delay <= 0L) {
            throw new IllegalArgumentException("Delay must be positive.");
        } else {
            this.delay = delay;
        }
    }

    public RetryLaterException.Method getMethod() {
        return this.method;
    }

    public void setMethod(RetryLaterException.Method method) {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        } else {
            this.method = method;
        }
    }

    public void setRollBack(boolean rollBack) {
        this.RollBack = rollBack;
    }

    public boolean isRollBack() {
        return this.RollBack;
    }

    public static enum Method {
        //线性
        LINEAR {
            public Date nextInvocation(Date lastCall, int tries, long delay) {
                return new Date(lastCall.getTime() + this.unsharpen(delay));
            }
        },
        //指数性
        EXPONENTIAL {
            public Date nextInvocation(Date lastCall, int tries, long delay) {
                return new Date((long) ((double) lastCall.getTime()
                        + Math.pow(2.0D, (double) tries) * (double) this.unsharpen(delay)));
            }
        },
        //明确时间性
        EXACT_TIME {
            public Date nextInvocation(Date lastCall, int tries, long nextRunTimeAsLong) {
                return new Date(nextRunTimeAsLong);
            }
        };

        private final Random random;

        private Method() {
            this.random = new Random();
        }

        long unsharpen(long delay) {
            double off = this.random.nextDouble() * 0.2D + 0.9D;
            return (long) ((double) delay * off);
        }

        public abstract Date nextInvocation(Date lastCall, int tries, long delayOrDate);
        
    }
}
