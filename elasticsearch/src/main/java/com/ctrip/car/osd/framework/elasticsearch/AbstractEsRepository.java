package com.ctrip.car.osd.framework.elasticsearch;

import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ctrip.car.osd.framework.elasticsearch.Constants.*;

public abstract class AbstractEsRepository<T> {

    private Class<T> clazz;
    protected final String index;
    protected final ClientInterface client;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        ParameterizedType type = (ParameterizedType) (this.getClass().getGenericSuperclass());
        this.clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    public AbstractEsRepository(String index) {
        this.index = index;
        this.client = EsClientFactory.getInstance(index);
    }

    public List<T> queryByCondition(T criteria) {
        return queryByCondition(criteria, DEFAULT_QUERY_DSL);
    }

    public List<T> queryByCondition(T criteria, String dslPath) {
        ESDatas<T> esResult = client.searchList(index + SEARCH_PATH, dslPath, criteria, clazz);
        return Optional.ofNullable(esResult.getDatas()).orElseGet(ArrayList::new);
    }

    public List<T> queryByCondition(Map<String, Object> param) {
        return queryByCondition(param, DEFAULT_QUERY_DSL);
    }

    public List<T> queryByCondition(Map<String, Object> param, String dslPath) {
        ESDatas<T> esResult = client.searchList(index + SEARCH_PATH, dslPath, param, clazz);
        return Optional.ofNullable(esResult.getDatas()).orElseGet(ArrayList::new);
    }

}
