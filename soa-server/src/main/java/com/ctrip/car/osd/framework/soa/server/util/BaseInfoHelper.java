package com.ctrip.car.osd.framework.soa.server.util;

import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class BaseInfoHelper {

    private static final String BASE_REQUEST_PACKAGE = BaseRequest.class.getName();
    private static final Set<String> BASE_REQUEST_NAME_SET = new CopyOnWriteArraySet<>();

    private static final String BASE_RESPONSE_PACKAGE = BaseResponse.class.getName();
    private static final Set<String> BASE_RESPONSE_NAME_SET = new CopyOnWriteArraySet<>();

    public static <Req> String getRequestId(Req req) {
        String requestid = "";
        try {
            String baseInfoName = getBaseInfoName(req, BASE_REQUEST_NAME_SET, BASE_REQUEST_PACKAGE);
            if (StringUtils.isNotEmpty(baseInfoName)) {
                Object requestObj = PropertyUtils.getProperty(req, baseInfoName);
                if (requestObj != null && requestObj instanceof BaseRequest) {
                    BaseRequest baseRequest = (BaseRequest) requestObj;
                    requestid = baseRequest.getRequestId();
                }
            }
        } catch (Exception e) {

        }
        return requestid;
    }

    public static <Resp> void setBaseResponse(Resp resp, long beforeTime,Boolean success,String msg,String requestid){
        try {
            BaseResponse baseResponse = null;
            long cost = System.currentTimeMillis() - beforeTime;
            String baseResponseName = getBaseInfoName(resp, BASE_RESPONSE_NAME_SET, BASE_RESPONSE_PACKAGE);
            if (StringUtils.isNotEmpty(baseResponseName)) {
                Object baseResponseObj = PropertyUtils.getProperty(resp, baseResponseName);
                if (baseResponseObj == null) {
                    baseResponseObj = new BaseResponse();
                }
                if (baseResponseObj instanceof BaseResponse) {
                    baseResponse = (BaseResponse) baseResponseObj;
                    baseResponse.setCost(cost);
                    if (baseResponse.isIsSuccess() == null) {
                        baseResponse.setIsSuccess(success);
                    }
                    if (baseResponse.getCode() == null) {
                        baseResponse.setCode("unknown");
                    }
                    String returnMsg = baseResponse.getReturnMsg();
                    if (StringUtils.isBlank(returnMsg)) {
                        baseResponse.setReturnMsg(msg);
                    } else {
                        String successString = "success";
                        if (!successString.equalsIgnoreCase(msg)) {
                            baseResponse.setReturnMsg(msg + returnMsg);
                        }
                    }
                    baseResponse.setRequestId(requestid);
                    PropertyUtils.setProperty(resp,baseResponseName,baseResponse);
                }
            }
        } catch (Exception e) {

        }
    }

    private static <T> String getBaseInfoName(T clz, Set<String> nameSet, String packageName) {
        for (String baseReqName : nameSet) {
            if (PropertyUtils.isWriteable(clz, baseReqName)) {
                return baseReqName;
            }
        }
        for (Field declaredField : clz.getClass().getDeclaredFields()) {
            if (!Objects.equals(declaredField.getType().getName(), packageName)) {
                continue;
            }
            nameSet.add(declaredField.getName());
            return declaredField.getName();
        }
        return null;
    }
}
