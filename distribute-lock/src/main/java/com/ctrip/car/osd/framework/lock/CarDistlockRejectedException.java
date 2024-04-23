package com.ctrip.car.osd.framework.lock;

/**
 * @author xh.gao
 * @date 2023/5/6 15:23
 */
public class CarDistlockRejectedException extends RuntimeException {

    private static final long serialVersionUID = 1530959988309500399L;

    public CarDistlockRejectedException() {
    }

    public CarDistlockRejectedException(String message) {
        super(message);
    }

    public CarDistlockRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarDistlockRejectedException(Throwable cause) {
        super(cause);
    }

    public CarDistlockRejectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
