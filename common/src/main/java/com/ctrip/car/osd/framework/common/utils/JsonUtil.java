package com.ctrip.car.osd.framework.common.utils;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.utils.json.CustomDeserializationProblemHandler;
import com.ctrip.car.osd.framework.common.utils.json.TimestampDateSerializer;
import com.ctrip.soa.caravan.common.serializer.DateSerializer;
import com.ctrip.soa.caravan.util.serializer.ssjson.GregorianCalendarDeserializer;
import com.ctrip.soa.caravan.util.serializer.ssjson.SSJsonSerializerConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xiayx on 2017/8/17.
 */

public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping()
            .registerTypeHierarchyAdapter(Calendar.class,new CalendarSerializer())
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    static {
        MAPPER.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        // 单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 特殊字符
        MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        // 空字符串转空对象
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // 空数组转空对象
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        // 忽略大小写
        MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
        // 防止jackson使用缓存
        MAPPER.getFactory().configure(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING,false);
        // 在序列化时忽略值为 null 的属性
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 日期格式化
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 时区设置
        MAPPER.setTimeZone(TimeZone.getDefault());
        // 自定义异常处理
        MAPPER.addHandler(new CustomDeserializationProblemHandler());
        // 过滤Schema
        MAPPER.setFilterProvider(new SimpleFilterProvider().addFilter("Schema", SimpleBeanPropertyFilter.serializeAllExcept("Schema")));
        // Calendar处理module
        MAPPER.registerModule(createCalendarModule());
    }

    private static Module createCalendarModule() {
        List<DateSerializer> dateSerializers = Lists.newArrayList(SSJsonSerializerConfig.DEFAULT_CALENDAR_DESERIALIZERS);
        dateSerializers.add(TimestampDateSerializer.INSTANCE);
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Calendar.class, GregorianCalendar.class);
        module.addDeserializer(GregorianCalendar.class, new GregorianCalendarDeserializer(new ArrayList<>(dateSerializers)));
        return module;
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            try {
                String result = GSON.toJson(object);
                LOGGER.warn("jackson序列化报错，gson兼容成功", "类型：" + object.getClass().toString() +
                        " 错误信息：" + e.getMessage() + " 兼容结果：" + result);
                return result;
            } catch (Exception e2) {
                LOGGER.warn("gson序列化报错", "类型：" + object.getClass().toString() + "。错误信息：" + e.getMessage());
                return null;
            }
        }
    }

    public static <T> T toJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            try {
                T result = GSON.fromJson(json, clazz);
                LOGGER.warn("jackson反序列化报错，gson兼容成功", "类型：" + clazz.getName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return result;
            } catch (Exception e2) {
                LOGGER.warn("反序列化报错", "类型：" + clazz.getName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return null;
            }
        }
    }

    /**
     * 携程toString 经过特殊处理
     *
     * @param object
     * @return
     */
    public static String toJSONStringExcludeSchema(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return toJson(object);
        }
    }

    /**
     * 将字符串反序列化为List对象，使用范例如下：
     * List<UgcQueryReslutDto> tempUgcQueryReslutDtos = JsonUtil.toJson(strResponse,
     * new TypeToken<List<UgcQueryReslutDto>>() {}.getType());
     *
     * @param json json字符串
     * @param t    TypeToken对象的Type
     * @param <T>
     * @return
     */
    public static <T> T toJson(String json, Type t) {
        if (json == null) {
            return null;
        }
        try {

            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructType(t));
        } catch (Exception e) {
            try {
                T result = GSON.fromJson(json, t);
                LOGGER.warn("jackson反序列化报错，gson兼容成功", "类型：" + t.getTypeName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return result;
            } catch (Exception e2) {
                LOGGER.warn("反序列化报错", "类型：" + t.getTypeName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return null;
            }

        }
    }

    public static <T> T toJson(String json, TypeReference<T> typeReference) {
        if (json == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            Type t = typeReference.getType();
            try {
                T result = GSON.fromJson(json, t);
                LOGGER.warn("jackson反序列化报错，gson兼容成功", "类型：" + t.getTypeName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return result;
            } catch (Exception e2) {
                LOGGER.warn("反序列化报错", "类型：" + t.getTypeName() + " 错误信息：" + e.getMessage() + " 原始json：" + json);
                return null;
            }
        }
    }

    /**
     * output:[["1","batchcode","somepath","vendorname",
     * "2016-05-13 18:15:43.591"],["2"]]
     *
     * @param list
     * @return
     */
    public static <T> String toListValueJson(List<T> list) {
        if (list == null || list.size() > 0) {
            List<String> jsonStringList = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                jsonStringList.add(toSingleValueJson(list.get(i)));
            }
            String allJson = String.format("[%s]", StringUtils.join(jsonStringList, ","));
            return allJson;
        }
        return null;
    }

    /**
     * output:[["1","batchcode","somepath","vendorname","2016-05-13 19:22:13.34"
     * ], ["2"]]
     *
     * @param list
     * @return
     */
    public static <T> List<String> toListStringJson(List<T> list) {
        if (list == null || list.size() > 0) {
            List<String> jsonStringList = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                jsonStringList.add(toSingleValueJson(list.get(i)));
            }
            return jsonStringList;
        }
        return null;
    }

    /**
     * output:[["1","batchcode","somepath","vendorname",
     * "2016-05-13 18:15:43.591"],["2"]]
     *
     * @param list
     * @return
     */
    public static <T> List<List<String>> toListValueJsonList(List<T> list) {
        if (list != null && list.size() > 0) {
            List<List<String>> jsonStringList = new ArrayList<List<String>>();
            for (int i = 0; i < list.size(); i++) {
                jsonStringList.add(toSingleValueJsonList(list.get(i)));
            }
            return jsonStringList;
        }
        return null;
    }

    /**
     * output:["1","batchcode","somepath","vendorname","2016-05-13 18:09:06.244"
     * ]
     *
     * @param obj
     * @return
     */
    public static <T> String toSingleValueJson(T obj) {
        if (obj == null) {
            return null;
        }
        Class classInstanc = obj.getClass();
        Field[] fields = classInstanc.getDeclaredFields();
        StringBuilder bld = new StringBuilder(100);
        bld.append("[");
        if (fields != null && fields.length > 0) {
            boolean isHasPre = false;
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f != null) {
                    try {
                        f.setAccessible(true);
                        Object fieldValue = f.get(obj);
                        if (fieldValue != null) {
                            if (isHasPre) {
                                bld.append(",");
                            }
                            if (f.getType() == Date.class || f.getType() == Timestamp.class) {
                                Date dt = (Date) fieldValue;
                                SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                bld.append(String.format("%s,", sm.format(dt)));
                            } else {
                                bld.append(String.format("%s,", fieldValue.toString()));
                            }
                            isHasPre = true;
                        } else {
                            bld.append(",");
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        LOGGER.error(classInstanc.getName(), e);
                        bld.append((String) null);
                    }
                }
            }
        }
        bld.append("]");
        if (bld.length() > 2) {
            return bld.toString();
        }
        return null;
    }

    /**
     * @param obj
     * @return
     */
    public static <T> List<String> toSingleValueJsonList(T obj) {
        if (obj == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        Class classInstanc = obj.getClass();
        Field[] fields = classInstanc.getDeclaredFields();
        StringBuilder bld = new StringBuilder(100);
        bld.append("[");
        if (fields != null && fields.length > 0) {
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f != null) {
                    try {
                        f.setAccessible(true);
                        Object fieldValue = f.get(obj);
                        if (fieldValue != null) {
                            if (f.getType() == Date.class || f.getType() == Timestamp.class) {
                                Date dt = (Date) fieldValue;
                                SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                list.add(String.format("%s", sm.format(dt)));
                            } else {
                                list.add(String.format("%s", fieldValue.toString()));
                            }
                        } else {
                            list.add(null);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        LOGGER.error(classInstanc.getName(), e);
                        list.add(null);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 编码Base64
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static String encodeBase64(byte[] input) throws Exception {
        Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.concurrent.Base64");
        Method mainMethod = clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, new Object[]{input});
        return (String) retObj;
    }

    public static Set<Integer> getSetInt(String str) {
        try {
            return toJson("[" + str + "]", new TypeReference<Set<Integer>>() {});
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public static Set<String> getSetStr(String str) {
        try {
            return toJson("['" + str.replaceAll(",", "','") + "']", new TypeReference<Set<String>>() {});
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public static <T> List<T> getObjList(String str, Class<T> clazz) {
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(str, javaType);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getObj(String str, Class<T> clazz) {
        try {
            return toJson(str, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}