package com.ctrip.car.osd.framework.common.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarSerializer implements JsonSerializer, JsonDeserializer {
    // 实现JsonSerializer接口的serialize（）方法，实现自定义序列化josn
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null && src instanceof Calendar) {
            Calendar calendar = (Calendar)src;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new JsonPrimitive(format.format(calendar.getTime()));//结果{"id":0,"date":"2016-04-19","calendar":"2016-04-19 21:54:31"}
        } else {
            return null;
        }
    }

    //实现JsonDeserializer接口的deserialize（）方法，实现自定义反序列化Object
    @Override
    public Calendar deserialize(JsonElement json, Type typeOfT,  JsonDeserializationContext context) throws JsonParseException {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(json.getAsString());
        } catch (ParseException e) {
            date = null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar;
    }

}
