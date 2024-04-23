package com.ctrip.car.osd.framework.common.utils.compress;

import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

public class CompressFactory {

    public static final Map<Integer, Compressor> INSTANCE = ImmutableMap.of(0, GzipCompressor.INSTANCE, 1, ZstdCompressor.INSTANCE);

    public static final String CLOSED_LOG = "LOG HAS BEEN CLOSED";

    public static final int MAX_SIZE = 100000;

    public static String compressBody(Object origin, boolean needCompress, int compressAlgorithm, boolean disableLog) {

        if (Objects.isNull(origin)) {
            return StringUtils.EMPTY;
        }

        boolean isExceptionObject = origin instanceof Exception;

        if (disableLog && !isExceptionObject) {
            return CLOSED_LOG;
        }

        String result = !isExceptionObject ? JsonUtil.toJSONStringExcludeSchema(origin) : getExceptionBody((Exception) origin);

        if (needCompress || result.length() > MAX_SIZE) {
            result = INSTANCE.get(compressAlgorithm).compress2String(result);
        }
        return result;
    }


    private static String getExceptionBody(Exception exception) {
        String body;
        try {
            StringWriter sw = new StringWriter(1024);
            PrintWriter pw = new PrintWriter(sw);
            if (exception != null) {
                exception.printStackTrace(pw);
            }
            return sw.toString();
        } catch (Exception e) {
            body = "parse exception body failed";
        }
        return body;
    }
}
