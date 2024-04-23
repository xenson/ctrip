package com.ctrip.car.osd.framework.common.utils.json;

import com.ctrip.soa.caravan.common.value.DateValues;
import com.ctrip.soa.caravan.util.serializer.date.AbstractDateSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimestampDateSerializer  extends AbstractDateSerializer {

    public static final TimestampDateSerializer INSTANCE = new TimestampDateSerializer();

    public static final String PATTERN = "^\\d*[1-9]\\d*$";

    private TimestampDateSerializer() {
        super(DateValues.SIMPLE_DATE_FORMAT, PATTERN, null);
    }

    @Override
    public GregorianCalendar deserialize(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateValues.SIMPLE_DATE_FORMAT);
        Date date = new Date(Long.parseLong(timestamp));
        String dateStr = dateFormat.format(date);
        return super.deserialize(dateStr);
    }
}