package com.ctrip.car.osd.framework.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import com.ctriposs.baiji.rpc.common.types.MobileDTO;
import com.dianping.cat.Cat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author by xiayx on 2019/5/10 10:22
 */
public class RequestContextFactory {
    public static final RequestContextFactory INSTANCE = new RequestContextFactory();
    private final ThreadLocal<RequestContext> slot = new TransmittableThreadLocal<>();

    public RequestContext getCurrent() {
        return slot.get();
    }

    public RequestContext create(){
        RequestContext rc = new RequestContext();
        rc.setIgnoreCache(false);
        slot.set(rc);
        return rc;
    }

    public RequestContext create(BaseRequest baseRequest){
        RequestContext rc = new RequestContext();
        rc.put("catMessageId", Cat.getCurrentMessageId());
        if (baseRequest != null) {
            String requestId = baseRequest.getRequestId();
            if (StringUtils.isNotBlank(requestId)) {
                rc.put("requestId",requestId);
                rc.setRequesetId(requestId);
            }
            String sessionId = baseRequest.getSessionId();
            if (StringUtils.isNotBlank(sessionId)) {
                rc.put("sessionId",sessionId);
                rc.setSessionId(sessionId);
            }
            String locale = baseRequest.getLocale();
            if (StringUtils.isNotBlank(locale)) {
                rc.put("locale",locale);
                rc.setLocale(locale);
            }
            String language = baseRequest.getLanguage();
            if (StringUtils.isNotBlank(language)) {
                rc.put("language",language);
                rc.setLanguage(language);
            }
            String currencyCode = baseRequest.getCurrencyCode();
            if (StringUtils.isNotBlank(currencyCode)) {
                rc.put("currencyCode",currencyCode);
                rc.setCurrencyCode(currencyCode);
            }
            Integer sourceCountryId = baseRequest.getSourceCountryId();
            if (sourceCountryId != null) {
                rc.put("sourceCountryId", sourceCountryId.toString());
                rc.setSourceCountryId(sourceCountryId);
            }
            String sourceFrom = baseRequest.getSourceFrom();
            if (StringUtils.isNotBlank(sourceFrom)) {
                rc.put("sourceFrom",sourceFrom);
                rc.setSourceFrom(sourceFrom);
            }
            Integer invokeFrom = baseRequest.getInvokeFrom();
            if (invokeFrom != null) {
                rc.put("invokeFrom", invokeFrom.toString());
                rc.setInvokeFrom(invokeFrom);
            }
            Integer channelType = baseRequest.getChannelType();
            if (channelType != null && channelType != 0) {
                rc.put("channelType", channelType.toString());
                rc.setChannelId(channelType);
            }
            Integer channelId = baseRequest.getChannelId();
            if (channelId != null && channelId != 0) {
                rc.put("channelId", channelId.toString());
                rc.setChannelId(channelId);
            }
            String uid = baseRequest.getUid();
            if (StringUtils.isNotBlank(uid)) {
                rc.put("uid",uid);
                rc.setUid(uid);
            }
            MobileDTO mobileInfo = baseRequest.getMobileInfo();
            if (mobileInfo != null) {
                String customerIP = mobileInfo.getCustomerIP();
                if (StringUtils.isNotBlank(customerIP)) {
                    rc.put("cip",customerIP);
                }
            }
            rc.setIgnoreCache(false);
            Map<String, String> extraTags = baseRequest.getExtraTags();
            if (MapUtils.isNotEmpty(extraTags)) {
                if (extraTags.containsKey("ignoreCache") && "true".equalsIgnoreCase(extraTags.get("ignoreCache"))) {
                    rc.setIgnoreCache(true);
                }
                rc.putAll(extraTags);
            }
        }
        slot.set(rc);
        return rc;
    }

    public void release() {
        preRemoveThreadLocalWithInSlot();
        slot.remove();
    }

    private void preRemoveThreadLocalWithInSlot() {
        RequestContext requestContext = slot.get();
        if (Objects.isNull(requestContext)) {
            return;
        }

        Set<ThreadLocal<Map<String, String>>> customizeTagHolder = requestContext.getCustomizeTagHolder();
        if (CollectionUtils.isEmpty(customizeTagHolder)) {
            return;
        }

        customizeTagHolder.forEach(ThreadLocal::remove);
    }

    public void setCurrent(RequestContext requestContext) {
        this.slot.set(requestContext);
    }
}
