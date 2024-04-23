package com.ctrip.car.osd.framework.elasticsearch;


import org.apache.commons.lang3.StringUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.Preconditions;
import org.frameworkset.elasticsearch.client.ClientInterface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ctrip.car.osd.framework.elasticsearch.Constants.PATH_TEMPLATE;

public class EsClientFactory {

    private static final Map<String, ClientInterface> CONFIG_CLIENT_MAP = new ConcurrentHashMap<>();
    private static ClientInterface restClient;

    public static ClientInterface getInstance(String index) {
        Preconditions.checkArgument(StringUtils.isNotBlank(index), "index can't be blank");
        if (CONFIG_CLIENT_MAP.containsKey(index)) {
            return CONFIG_CLIENT_MAP.get(index);
        }
        ClientInterface client = ElasticSearchHelper.getConfigRestClientUtil(String.format(PATH_TEMPLATE, index));
        CONFIG_CLIENT_MAP.put(index, client);
        return client;
    }

    public static ClientInterface getInstance() {
        if (restClient == null) {
            synchronized (ClientInterface.class) {
                if (restClient == null) {
                    restClient = ElasticSearchHelper.getRestClientUtil();
                }
            }
        }
        return restClient;
    }
}
