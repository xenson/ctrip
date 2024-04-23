package com.ctrip.car.osd.framework.soa.server.exception;

import com.ctrip.car.osd.framework.common.clogging.LogLevel;

public class CommonBizException extends RuntimeException {

    private final LogLevel level;
    private String code;
    private String message;

    public CommonBizException(String code, String message) {
        this(code, message, LogLevel.INFO);
    }

    public CommonBizException(String code, String message, LogLevel logLevel) {
        super(message + "(" + code + ")");
        this.level = logLevel;
    }

    public CommonBizException(String code, String message, Throwable cause, LogLevel logLevel) {
        super(message + "(" + code + ")", cause);
        this.level = logLevel;
    }

    public LogLevel getLevel() {
        return level;
    }

}
