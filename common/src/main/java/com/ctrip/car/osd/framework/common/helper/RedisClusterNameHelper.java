package com.ctrip.car.osd.framework.common.helper;

import com.ctrip.car.osd.framework.common.config.ConfigKeys;
import com.ctrip.car.osd.framework.common.entity.co.RedisClusterNameMapConfigCO;
import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import com.ctrip.car.osd.framework.common.utils.QconfigProperty;
import com.ctrip.framework.foundation.Foundation;
import com.dianping.cat.Cat;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author by xiayx on 2019/7/29 10:04
 */
public class RedisClusterNameHelper {
    public static String getClusterName(String redisDbName){
        Map<String, List<RedisClusterNameMapConfigCO>> map = getConfigMap();

        if (MapUtils.isEmpty(map) || !map.containsKey(redisDbName)) {
            return redisDbName;
        }

        String dataCenter = Foundation.server().getDataCenter();
        if (StringUtils.isNotBlank(dataCenter)) {
            List<RedisClusterNameMapConfigCO> configList = map.get(redisDbName);
            for (RedisClusterNameMapConfigCO config : configList) {
                if (dataCenter.equalsIgnoreCase(config.getDataCenter())) {
                    return config.getTargetName();
                }
            }
        }

        return redisDbName;
    }

    private static Map<String, List<RedisClusterNameMapConfigCO>> getConfigMap() {
        String config = QconfigProperty.getFrameworkQconfig(ConfigKeys.REDIS_CLUSTER_NAME_MAP);
        return JsonUtil.toJson(config, new TypeReference<Map<String, List<RedisClusterNameMapConfigCO>>>() {});
    }

}
