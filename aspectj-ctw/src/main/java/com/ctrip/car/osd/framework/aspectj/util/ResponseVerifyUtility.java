package com.ctrip.car.osd.framework.aspectj.util;


import com.ctrip.car.osd.framework.aspectj.cache.VerifyUtilCache;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctriposs.baiji.rpc.common.HasResponseStatus;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.common.types.ErrorDataType;
import com.ctriposs.baiji.rpc.common.types.ResponseStatusType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResponseVerifyUtility<R extends HasResponseStatus> {
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final Map<Class<? extends HasResponseStatus>, List<Method>> RESPONSE_METHOD_CACHE = new ConcurrentHashMap<>(32);
    private static final Map<Class<? extends HasResponseStatus>, Optional<Method>> BASE_RESPONSE_METHOD_CACHE = new ConcurrentHashMap<>(32);
    private Class<R> cls;

    public final Class<R> getCls() {
        return cls;
    }

    public ResponseVerifyUtility() {
        Type genericSuperclass2 = this.getClass().getGenericSuperclass();
        if (genericSuperclass2 instanceof ParameterizedType) {
            ParameterizedType genericSuperclass = (ParameterizedType) genericSuperclass2;
            Type type = genericSuperclass.getActualTypeArguments()[0];
            cls = (Class<R>) type;
        }
    }

    @PostConstruct
    private void init() {
        VerifyUtilCache.putIfAbsent(cls, this);
    }

    /**
     * 校验返回的结果是否为空
     *
     * @param response
     * @return
     */
    public abstract boolean verify(R response);

    /**
     * 公共校验步骤,true代表校验为空
     * 1.通过反射获取可校验的字段,通常是List,否则自定义实现,契约里的字段目前无法加注解
     * 2.默认实现HasResponseStatus,有默认返回错误值
     * 3.如果契约里定义BaseResponse,将错误代码和结果复制进去
     * 4.忽略字段里包含error等list的校验
     *
     * @param response
     * @return
     */
    public final Pair<Boolean, String> commonVerify(R response) {
        if (response == null) {
            return Pair.of(true, "response is null");
        }
        boolean result = false;
        StringBuilder msg = new StringBuilder("  ");
        //1.校验已经设置的ResponseStatusType
        ResponseStatusType responseStatus = response.getResponseStatus();
        if (responseStatus != null) {
            AckCodeType ack = responseStatus.getAck();
            //ack为success的情况下,说明请求正常,是否有结果不在此验证中
            result = ack != null && ack != AckCodeType.Success && ack != AckCodeType.Warning;
            msg.append("ack->").append(ack).append("  ");
            //errors集合不为空说明有错误
            List<ErrorDataType> errors = responseStatus.getErrors();
            if (CollectionUtils.isNotEmpty(errors)) {
                String errorStr = errors.stream().map(error -> String.format("{errorCode:%s,message:%s}", error.getErrorCode(), error.getMessage())).collect(Collectors.joining(","));
                msg.append("errors->").append(errorStr).append("  ");
                result = true;
            }
        }
        Class<? extends HasResponseStatus> responseClass = response.getClass();

        //2.如果有BaseResponse,那也可以直接判断
        Optional<Method> baseResponseMethod = BASE_RESPONSE_METHOD_CACHE.get(responseClass);
        if (baseResponseMethod == null) {
            Field[] fields = responseClass.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(fields)) {
                baseResponseMethod = Stream.of(fields).filter(field -> {
                    Class<?> type = field.getType();
                    //如果是com.ctriposs.baiji.rpc.common.types.BaseResponse
                    return BaseResponse.class.isAssignableFrom(type);
                }).map(field -> {
                    String getMethodName = "get" + StringUtils.capitalize(field.getName());
                    try {
                        return responseClass.getMethod(getMethodName);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).findFirst();
                BASE_RESPONSE_METHOD_CACHE.putIfAbsent(responseClass, baseResponseMethod);
            }
        }

        BaseResponse baseResponse = null;
        if (baseResponseMethod != null && baseResponseMethod.isPresent()) {
            Method method = baseResponseMethod.get();
            try {
                Object invoke = method.invoke(response);
                baseResponse = (BaseResponse) invoke;
                Boolean isSuccess = baseResponse.isIsSuccess();
                if (isSuccess != null && !isSuccess) {
                    result = true;//请求失败,算空集
                }
            } catch (Exception e) {
                LOGGER.warn("获取baseResponse失败!");
            }
        }

        //3.通过反射获取可校验的字段,通常是List,判断该字段是否为空
        List<Method> methods = RESPONSE_METHOD_CACHE.get(responseClass);
        if (CollectionUtils.isEmpty(methods)) {
            Field[] fields = responseClass.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(fields)) {
                methods = Stream.of(fields).filter(field -> {
                    String name = field.getName();
                    //错误信息集合和结果集没有关联
                    if (StringUtils.contains(name.toUpperCase(), "ERROR")) {
                        return false;
                    }
                    Class<?> type = field.getType();
                    //如果是集合
                    if (Collection.class.isAssignableFrom(type)) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType genericSuperclass = (ParameterizedType) genericType;
                            Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
                            if (ArrayUtils.isNotEmpty(actualTypeArguments)) {
                                //泛型里包含error也认为是错误信息集合
                                return Stream.of(actualTypeArguments).noneMatch(type1 -> {
                                    String typeName = type1.getTypeName();
                                    return StringUtils.contains(typeName, "ERROR");
                                });
                            }

                        }
                    } else {
                        return false;
                    }
                    return true;
                }).map(field -> {
                    String getMethodName = "get" + StringUtils.capitalize(field.getName());
                    try {
                        return responseClass.getMethod(getMethodName);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
                RESPONSE_METHOD_CACHE.putIfAbsent(responseClass, methods);
            }


        }

        if (CollectionUtils.isNotEmpty(methods)) {
            try {
                for (Method method : methods) {
                    Object invoke = method.invoke(response);
                    if (invoke == null || CollectionUtils.isEmpty((Collection) invoke)) {
                        msg.append(method.getName()).append(" is nullOrEmpty. ");
                        result = true;//有空集
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("获取response里的集合失败!");
            }

        }

        if (result && baseResponse != null) {
            //将baseResponse的错误信息加入
            msg.append(baseResponse.toString());
        }
        return Pair.of(result, msg.toString());
    }

    //被复制到新类的方法,千万不可删除
    private Pair<Boolean, String> verifyCopy() {
        return null;
    }
}
