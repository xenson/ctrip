package com.ctrip.car.osd.framework.common.exception;

import org.apache.commons.lang3.StringUtils;

public interface ServerRespExceptionCode {
    //Response返回码
    public String getCode();

    //Response返回消息
    public String getMsg();


    default String toStringMsg() {
        StringBuilder stringBuilder = new StringBuilder(StringUtils.trimToEmpty(getMsg()));
        String code = getCode();
        if (StringUtils.isNotBlank(code)) {
            stringBuilder.append("(").append(code).append(")");
        }
        return stringBuilder.toString();
    }
}
