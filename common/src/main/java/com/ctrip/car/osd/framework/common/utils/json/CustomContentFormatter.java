package com.ctrip.car.osd.framework.common.utils.json;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.config.ContentFormatConfig;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.ctrip.soa.caravan.common.serializer.DateSerializer;
import com.ctrip.soa.caravan.common.serializer.SerializationException;
import com.ctrip.soa.caravan.common.value.XMLValues;
import com.ctrip.soa.caravan.util.serializer.date.WcfDateSerializer;
import com.ctrip.soa.caravan.util.serializer.ssjson.*;
import com.ctriposs.baiji.JsonSerializer;
import com.ctriposs.baiji.rpc.common.formatter.ContentFormatter;
import com.ctriposs.baiji.specific.SpecificRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import org.springframework.util.StreamUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CustomContentFormatter implements ContentFormatter {

    private final ObjectMapper mapper = new ObjectMapper();
    public static final String MEDIA_TYPE = "application/json";
    public static final String EXTENSION = "json";
    private static final JsonSerializer JSON_SERIALIZER =  new JsonSerializer();
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    public CustomContentFormatter(ContentFormatConfig formatConfig) {
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 自定义异常处理
        mapper.addHandler(new CustomDeserializationProblemHandler());
        // 自定义解析module
        mapper.registerModule(createCustomModule(formatConfig));
    }

    private static Module createCustomModule(ContentFormatConfig formatConfig) {
        List<DateSerializer> dateSerializers = Lists.newArrayList(SSJsonSerializerConfig.DEFAULT_CALENDAR_DESERIALIZERS);
        dateSerializers.add(TimestampDateSerializer.INSTANCE);
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(XMLGregorianCalendar.class, XMLValues.xmlGregorianCalendarType());
        module.addAbstractTypeMapping(Calendar.class, GregorianCalendar.class);
        module.addSerializer(GregorianCalendar.class, new GregorianCalendarSerializer(WcfDateSerializer.INSTANCE, false));
        module.addDeserializer(GregorianCalendar.class, new GregorianCalendarDeserializer(new ArrayList<>(dateSerializers)));
        module.addSerializer(XMLValues.xmlGregorianCalendarType(), new XMLGregorianCalendarSerializer(WcfDateSerializer.INSTANCE));
        module.addDeserializer(XMLValues.xmlGregorianCalendarType(), new XMLGregorianCalendarDeserializer(new ArrayList<>(dateSerializers)));

        if (formatConfig.isNumberCheckEnabled()) {
            module.addDeserializer(BigDecimal.class, new BigDecimalDeserializerWrapper(formatConfig.getBigNumberLength(), formatConfig.getBigDecimalMaxSignificandLength(), formatConfig.getBigDecimalMaxExponentLength()));
            module.addDeserializer(BigInteger.class, new BigIntegerDeserializerWrapper(formatConfig.getBigNumberLength()));
        }
        return module;
    }

    @Override
    public String getContentType() {
        return MEDIA_TYPE;
    }

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    public String getEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    @Override
    public <T> void serialize(OutputStream stream, T obj) throws IOException {
        if (obj instanceof SpecificRecord) {
            JSON_SERIALIZER.serialize((SpecificRecord) obj, stream);
        } else {
            try {
                mapper.writeValue(stream, obj);
            } catch (RuntimeException | Error ex) {
                throw ex;
            } catch (Exception ex) {
                throw new SerializationException(ex);
            }
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, InputStream stream) throws IOException {
        byte[] bytes = null;
        try {
            bytes = StreamUtils.copyToByteArray(stream);
            if (bytes == null || bytes.length == 0) {
                return mapper.readValue("{}", clazz);
            }
            return mapper.readValue(bytes, clazz);
        } catch (Exception e) {
            if (bytes != null) {
                LOGGER.warn("SOA反序列化报错",  e.getMessage() + "\n 原始报文：" + new String(bytes));
            } else {
                LOGGER.warn("SOA反序列化报错",  e.getMessage());
            }
            try {
                return clazz.newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
