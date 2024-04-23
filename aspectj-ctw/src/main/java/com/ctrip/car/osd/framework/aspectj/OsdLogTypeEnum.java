package com.ctrip.car.osd.framework.aspectj;

public enum OsdLogTypeEnum {
    COST("cost", "方法执行时间"),
    EMPTY_RESPONSE("empty_response", "返回结果为空"),;

    private String type;
    private String desc;

    OsdLogTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public final String getType() {
        return type;
    }

    public final String getDesc() {
        return desc;
    }
}
