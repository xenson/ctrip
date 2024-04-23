package com.ctrip.car.osd.framework.common.config;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SDFrameworkQConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CONFIG_FILE = "sd-framework.properties";

    // 不关闭日志
    private static final int NOT_CLOSE = 0;

    // 日志压缩算法
    private static final int COMPRESS_ALGORITHM_GZIP = 0;
    private static final int COMPRESS_ALGORITHM_ZSTD = 1;
    private static final List<Integer> SUPPORTED_COMPRESS_ALGORITHM = ImmutableList.of(COMPRESS_ALGORITHM_GZIP, COMPRESS_ALGORITHM_ZSTD);

    private final Map<String, String> config = MapConfig.get(CONFIG_FILE, Feature.create().setFailOnNotExists(false).build()).asMap();

    public Map<String, String> getQconfig() {
        return ImmutableMap.copyOf(config);
    }

    public boolean needCompressMethod(String methodName) {
        return contains(methodName, ConfigKeys.COMPRESS_CONFIG_KEY);
    }

    public boolean clientNeedCompressMethod(String methodName) {
        return contains(methodName, ConfigKeys.CLIENT_COMPRESS_CONFIG_KEY);
    }

    public int disableLog(String methodName) {
        return disableLog(methodName, ConfigKeys.DISABLE_LOG_CONFIG_KEY);
    }

    public int clientDisableLog(String methodName) {
        return disableLog(methodName, ConfigKeys.CLIENT_DISABLE_LOG_CONFIG_KEY);
    }

    public int disableLog(String methodName, String keyName) {
        if (configNotExist(keyName)) {
            return NOT_CLOSE;
        }

        String json = config.get(keyName);
        Map<String, Integer> map = JsonUtil.toJson(json, new TypeReference<Map<String, Integer>>() {});
        if (MapUtils.isEmpty(map) || !map.containsKey(methodName)) {
            return NOT_CLOSE;
        }
        Integer result = map.get(methodName);
        return result != null ? result : NOT_CLOSE;
    }

    private boolean contains(String target, String configKey) {
        if (configNotExist(configKey)) {
            return false;
        }
        String config = this.config.get(configKey);
        Set<String> configValue = Sets.newHashSet(config.split(","));
        if (CollectionUtils.isEmpty(configValue)) {
            return false;
        }
        return configValue.contains(target);
    }

    private boolean configNotExist(String configKey) {
        return MapUtils.isEmpty(config) || !config.containsKey(configKey);
    }

    public int compressAlgorithm() {
        if (configNotExist(ConfigKeys.COMPRESS_ALGORITHM_KEY)) {
            return COMPRESS_ALGORITHM_GZIP;
        }
        try {
            int configValue = Integer.parseInt(this.config.get(ConfigKeys.COMPRESS_ALGORITHM_KEY));
            if (!SUPPORTED_COMPRESS_ALGORITHM.contains(configValue)) {
                throw new IllegalArgumentException();
            }
            return configValue;
        } catch (Exception e) {
            logger.warn("QConfig File[sd-framework.properties] KEY[ctrip.soa.service.compress.algorithm] has problem, plz check");
            return COMPRESS_ALGORITHM_GZIP;
        }
    }

    public ContentFormatConfig getContentFormatConfig() {
        ContentFormatConfig formatConfig = new ContentFormatConfig();
        if (config.containsKey(ConfigKeys.BIG_NUMBER_LENGTH_CHECK_ENABLE_KEY)) {
            formatConfig.setNumberCheckEnabled(config.get(ConfigKeys.BIG_NUMBER_LENGTH_CHECK_ENABLE_KEY).equals("1"));
        }
        try {
            if (config.containsKey(ConfigKeys.BIG_NUMBER_LENGTH_KEY)) {
                formatConfig.setBigNumberLength(Integer.parseInt(config.get(ConfigKeys.BIG_NUMBER_LENGTH_KEY)));
            }
            if (config.containsKey(ConfigKeys.BIG_DECIMAL_SIGNIFICAND_MAX_LENGTH_KEY)) {
                formatConfig.setBigDecimalMaxSignificandLength(Integer.parseInt(config.get(ConfigKeys.BIG_DECIMAL_SIGNIFICAND_MAX_LENGTH_KEY)));
            }
            if (config.containsKey(ConfigKeys.BIG_DECIMAL_EXPONENT_MAX_LENGTH_KEY)) {
                formatConfig.setBigDecimalMaxExponentLength(Integer.parseInt(config.get(ConfigKeys.BIG_DECIMAL_EXPONENT_MAX_LENGTH_KEY)));
            }
        } catch (Exception e) {
            logger.warn("QConfig File[sd-framework.properties] KEY[ctrip.soa.json.*] has problem, plz check");
        }

        return formatConfig;
    }

    public boolean isInterceptorExclude(String simpleName) {
        return contains(simpleName, ConfigKeys.EXCLUDE_INTERCEPTORS_KEY);
    }

    public boolean isLogOriginRequest() {
        String value = config.getOrDefault(ConfigKeys.LOG_ORIGINAL_REQUEST_KEY, "1");
        return Objects.equals(value, "1");
    }

    public int getPreWarningPercentage() {
        String defaultPercentage = "80";
        String value = config.getOrDefault(ConfigKeys.PRE_WARNING_PERCENTAGE,  defaultPercentage);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.warn("QConfig File[sd-framework.properties] KEY[ctrip.soa.rate.limit.warning.percentage] has problem, plz check");
        }

        return Integer.parseInt(defaultPercentage);
    }

    /**
     * 配置服务端埋点只发QMQ消息-替代原有调Client方式(过渡)
     *
     * @return
     */
    public boolean isOnlyTrackQMQ() {
        String value = config.getOrDefault(ConfigKeys.TRACK_QMQ_ONLY_KEY, "0");
        return Objects.equals(value, "1");
    }
}
