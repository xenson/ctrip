package com.ctrip.car.osd.framework.common.config;

public class ContentFormatConfig {

    private boolean numberCheckEnabled = true;
    private int bigNumberLength = 60;
    private int bigDecimalMaxSignificandLength = 60;
    private int bigDecimalMaxExponentLength = 6;

    public boolean isNumberCheckEnabled() {
        return numberCheckEnabled;
    }

    public void setNumberCheckEnabled(boolean numberCheckEnabled) {
        this.numberCheckEnabled = numberCheckEnabled;
    }

    public int getBigNumberLength() {
        return bigNumberLength;
    }

    public void setBigNumberLength(int bigNumberLength) {
        this.bigNumberLength = bigNumberLength;
    }

    public int getBigDecimalMaxSignificandLength() {
        return bigDecimalMaxSignificandLength;
    }

    public void setBigDecimalMaxSignificandLength(int bigDecimalMaxSignificandLength) {
        this.bigDecimalMaxSignificandLength = bigDecimalMaxSignificandLength;
    }

    public int getBigDecimalMaxExponentLength() {
        return bigDecimalMaxExponentLength;
    }

    public void setBigDecimalMaxExponentLength(int bigDecimalMaxExponentLength) {
        this.bigDecimalMaxExponentLength = bigDecimalMaxExponentLength;
    }
}
