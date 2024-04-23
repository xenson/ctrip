package com.ctrip.car.osd.framework.lock.helper;

import com.ctrip.car.osd.framework.lock.entity.RedisClusterNameMapConfigCO;
import com.ctrip.framework.foundation.Foundation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xh.gao
 * @date 2023/6/27 22:50
 */
public class RedisClusterNameHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String FILE_NAME = "sd-framework.properties";

    private static final String QCONFIG_KEY = "redis.distribute.lock.cluster.name.map";

    /**
     * 上海集群默认namespace
     */
    private static final String NAME_SPACE_SHANGHAI = "com.ctrip.car.sd.ctqrestfulshopping";

    /**
     * 新加坡集群默认namespace
     */
    private static final String NAME_SPACE_SIN_AWS = "com.ctrip.car.framework.lock.sin.aws";

    private static final String DATA_CENTER_SIN_AWS = "SIN-AWS";

    public static String getClusterName() {
        String defaultRedisDbName = getDefaultRedisDbName();

        List<RedisClusterNameMapConfigCO> configList = getConfigList();
        if (CollectionUtils.isEmpty(configList)) {
            return defaultRedisDbName;
        }

        Map<String, String> clusterMap = configList.stream().collect(Collectors.toMap(
                RedisClusterNameMapConfigCO::getDataCenter, RedisClusterNameMapConfigCO::getTargetName, (o, n) -> n));
        if (clusterMap.isEmpty()) {
            return defaultRedisDbName;
        }

        String dataCenter = Foundation.server().getDataCenter();
        if (StringUtils.isEmpty(dataCenter)) {
            return defaultRedisDbName;
        }

        String dataCenterRedisDbName = clusterMap.get(dataCenter);
        if (StringUtils.isEmpty(dataCenterRedisDbName)) {
            return defaultRedisDbName;
        }

        return dataCenterRedisDbName;
    }

    private static String getDefaultRedisDbName() {
        String dataCenter = Foundation.server().getDataCenter();
        if (StringUtils.isEmpty(dataCenter)) {
            return NAME_SPACE_SHANGHAI;
        }
        if (Objects.equals(dataCenter, DATA_CENTER_SIN_AWS)) {
            return NAME_SPACE_SIN_AWS;
        }
        return NAME_SPACE_SHANGHAI;
    }

    private static List<RedisClusterNameMapConfigCO> getConfigList() {
        try {
            MapConfig mapConfig = MapConfig.get(FILE_NAME, Feature.create().setFailOnNotExists(false).build());
            String config = mapConfig.asMap().get(QCONFIG_KEY);
            if (StringUtils.isEmpty(config)) {
                return null;
            }
            return MAPPER.readValue(config, new TypeReference<List<RedisClusterNameMapConfigCO>>() {
            });
        } catch (Exception e) {
            return null;
        }
    }
}
