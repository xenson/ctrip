package com.ctrip.car.osd.framework.elasticsearch;

import com.ctrip.car.osd.framework.common.utils.json.CustomDeserializationProblemHandler;
import com.ctrip.car.osd.framework.common.utils.json.TimestampDateSerializer;
import com.ctrip.soa.caravan.common.serializer.DateSerializer;
import com.ctrip.soa.caravan.util.serializer.ssjson.GregorianCalendarDeserializer;
import com.ctrip.soa.caravan.util.serializer.ssjson.SSJsonSerializerConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.frameworkset.util.SimpleStringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.frameworkset.elasticsearch.boot.ElasticSearchBoot;
import org.frameworkset.json.Jackson2ObjectMapper;
import org.frameworkset.json.JacksonObjectMapperWrapper;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

public class ElasticSearchInitializer {

    private static final String CONFIG_FILE = "sd-elasticsearch.properties";

    public final Map<String, String> config = MapConfig.get(CONFIG_FILE).asMap();

    @PostConstruct
    public void init() throws IllegalAccessException {
        Preconditions.checkArgument(MapUtils.isNotEmpty(config), "未发现[" + CONFIG_FILE + "]文件，或者配置列表为空");
        String username = config.get("username");
        String password = config.get("password");
        String host = config.get("host");
        Preconditions.checkArgument(!StringUtils.isAnyBlank(username, password, host), CONFIG_FILE + "中，username password host必须都不能为空");
        final Map<String, String> params = new HashMap<>();
        params.put("elasticUser", username);
        params.put("elasticPassword", password);
        params.put("elasticsearch.rest.hostNames", host);
        ElasticSearchBoot.boot(params);
        initJackson();
    }

    private void initJackson() throws IllegalAccessException {
        configureObjectMapper(SimpleStringUtil.getObjectMapper());

        JacksonObjectMapperWrapper jacksonObjectMapperWrapper = SimpleStringUtil.getJacksonObjectMapper();
        Jackson2ObjectMapper jackson2ObjectMapper = (Jackson2ObjectMapper) FieldUtils.readDeclaredField(jacksonObjectMapperWrapper, "jacksonObjectMapper", true);
        configureObjectMapper(jackson2ObjectMapper.getObjectMapper());

        ObjectMapper NOT_ALLOW_SINGLE_QUOTES_mapper = (ObjectMapper) FieldUtils.readDeclaredField(jackson2ObjectMapper, "NOT_ALLOW_SINGLE_QUOTES_mapper", true);
        configureObjectMapper(NOT_ALLOW_SINGLE_QUOTES_mapper);
        NOT_ALLOW_SINGLE_QUOTES_mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, false);

        ObjectMapper ALLOW_SINGLE_QUOTES_mapper = (ObjectMapper) FieldUtils.readDeclaredField(jackson2ObjectMapper, "ALLOW_SINGLE_QUOTES_mapper", true);
        configureObjectMapper(ALLOW_SINGLE_QUOTES_mapper);
    }

    private void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.addHandler(new CustomDeserializationProblemHandler());
        objectMapper.registerModule(createCalendarModule());
    }

    private Module createCalendarModule() {
        List<DateSerializer> dateSerializers = Lists.newArrayList(SSJsonSerializerConfig.DEFAULT_CALENDAR_DESERIALIZERS);
        dateSerializers.add(TimestampDateSerializer.INSTANCE);
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Calendar.class, GregorianCalendar.class);
        module.addDeserializer(GregorianCalendar.class, new GregorianCalendarDeserializer(new ArrayList<>(dateSerializers)));
        return module;
    }
}
