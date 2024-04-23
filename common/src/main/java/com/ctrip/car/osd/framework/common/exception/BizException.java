package com.ctrip.car.osd.framework.common.exception;

import org.apache.commons.lang3.StringUtils;

public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;
    private Object exceptionObj = null;

    public final String getCode() {
        return code;
    }

    public final String getMsg() {
        return msg;
    }

    public BizException() {
        super();
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public BizException(String message) {
        super(message);
        this.msg = message;
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Object exceptionObj) {
        super(message);
        this.exceptionObj = exceptionObj;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.msg = message;
    }

    private BizException(String code, String msg, Throwable cause, Object obj) {
        this(msg + "(" + code + ")", cause);
        this.code = code;
        this.msg = msg;
        this.exceptionObj = obj;
    }

    private BizException(String code, String msg, Throwable cause) {
        this(code, msg, cause, null);
    }

    /**
     * 该重载方法用于将soa服务返回的code和msg重新生产BizException抛出
     * 不要自定义一些新的code
     */
    public static BizException instance(String code, String msg, Throwable cause, Object obj) {
        CommonExceptionCode server00001 = CommonExceptionCode.SERVER00001;
        if (StringUtils.isBlank(code)) {
            code = server00001.getCode();
        }
        if (StringUtils.isBlank(msg)) {
            msg = server00001.getMsg();
        }
        return new BizException(code, msg, cause, obj);
    }

    public static BizException instance(String code, String msg, Throwable cause) {
        return instance(code, msg, cause, null);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, String otherMsg, Throwable cause, Object obj) {
        if (serverRespExceptionCode == null) {
            serverRespExceptionCode = CommonExceptionCode.SERVER00001;
        }
        String msg = StringUtils.trimToEmpty(serverRespExceptionCode.getMsg());
        if (StringUtils.isNotBlank(msg)) {
            msg += " ; ";
        }
        if (StringUtils.isNotBlank(otherMsg)) {
            msg += otherMsg;
        }
        return new BizException(serverRespExceptionCode.getCode(), msg, cause, obj);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, String otherMsg, Throwable cause) {
        return instance(serverRespExceptionCode, otherMsg, cause, null);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, String otherMsg) {
        return instance(serverRespExceptionCode, otherMsg, null, null);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, String otherMsg, Object obj) {
        return instance(serverRespExceptionCode, otherMsg, null, obj);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, Throwable cause) {
        return instance(serverRespExceptionCode, null, cause, null);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, Throwable cause, Object obj) {
        return instance(serverRespExceptionCode, null, cause, obj);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode) {
        return instance(serverRespExceptionCode, null, null, null);
    }

    public static BizException instance(ServerRespExceptionCode serverRespExceptionCode, Object obj) {
        return instance(serverRespExceptionCode, null, null, obj);
    }

    public Object getExceptionObj() {
        return exceptionObj;
    }
}
