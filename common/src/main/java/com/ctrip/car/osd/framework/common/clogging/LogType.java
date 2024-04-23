package com.ctrip.car.osd.framework.common.clogging;

public enum LogType {
    NONE(0),ARGUMENTS(1), RETURN(2),ALL(3);

    private final int value;

    private LogType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
