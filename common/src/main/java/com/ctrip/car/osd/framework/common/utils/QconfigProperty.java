package com.ctrip.car.osd.framework.common.utils;

import org.apache.commons.lang3.StringUtils;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author geyh
 * @create 2018-08-07 15:33
 */
public class QconfigProperty {

    public static String getBasicQconfig(String key) {
        return getQconfigString("basic.properties", key);
    }

    public static String getQconfigString(String fileName, String key) {
        String str = "";
        Map<String, String> map = getQconfigMap(fileName);
        if (map != null) {
            str = map.get(key);
        }

        return str;
    }

    private static Map<String, String> getQconfigMap(String fileName) {
        Map<String, String> map = null;
        MapConfig config = MapConfig.get(fileName, Feature.create().setFailOnNotExists(false).build());
        if (config != null) {
            map = config.asMap();
        }

        return map;
    }

    public static String getFrameworkQconfig(String key) {
        return getQconfigString("sd-framework.properties", key);
    }

    public static List<String> getFrameworkTrackNodes() {
        List<String> nodes = new ArrayList<>();
        String nodesKey = "ctrip.soa.service.track.nodes";
        if (StringUtils.isBlank(getFrameworkQconfig(nodesKey))) {
            return nodes;
        }
        nodes = JsonUtil.getObjList(getFrameworkQconfig(nodesKey), String.class);
        return nodes;
    }

}
