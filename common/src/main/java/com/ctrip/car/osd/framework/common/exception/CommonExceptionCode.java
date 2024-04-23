package com.ctrip.car.osd.framework.common.exception;

/**
 * 默认错误信息
 */
public enum CommonExceptionCode implements ServerRespExceptionCode {
    SERVER00000("0", "success"),
    SERVER00001("-1", "系统内部错误"),
    //SOA
    SOA00002("-2","SOA服务返回空结果"),
    SOA00003("-3","SOA服务返回baseResponse空结果"),
    ;

    CommonExceptionCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
