package com.ctrip.car.osd.framework.soa.server.interceptors;

import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import com.ctrip.car.osd.framework.common.clogging.LogContext;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.common.proxy.NotificationCenterProxy;
import com.ctrip.car.osd.framework.common.utils.BeanCopyUtils;
import com.ctrip.car.osd.framework.common.utils.LogTagUtil;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory.ExecutorDescriptor;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.GlobalServiceInterceptor;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceContext;
import com.ctrip.car.osd.framework.soa.server.util.BaseInfoHelper;
import com.ctrip.car.osd.notificationcenter.dto.TrackerRequestType;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.ibu.platform.gdpr.bean.GDPRType;
import com.ctrip.ibu.platform.gdpr.util.IbuGDPRUtils;
import com.ctriposs.baiji.rpc.server.HttpRequestContext;
import com.ctriposs.baiji.rpc.server.HttpRequestWrapper;
import com.dianping.cat.Cat;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * 访问日志
 *
 * @author xiayx@Ctrip.com
 */
@Component
public class AccessLoggingInterceptor implements GlobalServiceInterceptor {

    public static final String ACCESS_LOG = "access-log";
    public static final String ACCESS_LOG_TIME = "access-log-time";
    public static final String BEGIN = "begin";
    public static final String REQUEST_ID = "requestId";
    public static final String CLIENT_APPID = "clientAppId";
    public static final String IS_INFO_SEC = "isInfoSec";
    public static final String CLOSED_LOG = "LOG HAS BEEN CLOSED";

    private static final String MASK_SWITCH = "mask_switch";
    private static ExecutorService LOG_POOL = ThreadPoolUtil.singleRunPool("LOG-SERVICE-%d");
    private static Map<String, MaskType> maskMap = new HashMap<>();
    private static Map<String, MaskType> originalDataMap = new HashMap<>();
    private static List<String> maskMethodList;

    private final Logger logger = LoggerFactory.getLogger("SOA-SERVER");
    @Autowired
    private ExecutorFactory executorFactory;
    @Autowired
    private SDFrameworkQConfig sdFrameworkQConfig;

    static {
        //init maskMap
        maskMap.put("firstName", MaskType.NAME);
        maskMap.put("lastName", MaskType.NAME);
        maskMap.put("clientName", MaskType.NAME);
        maskMap.put("name", MaskType.NAME);
        maskMap.put("mobilePhone", MaskType.PHONE);
        maskMap.put("telephone", MaskType.PHONE);
        maskMap.put("telphone", MaskType.PHONE);
        maskMap.put("email", MaskType.EMAIL);
        maskMap.put("idCardNo", MaskType.ID_NUMBER);

        //init maskMethodList
        maskMethodList = Arrays.asList("oSDUpdateTempOrder", "oSDQueryOrder","oSDQueryRentalVoucher","oSDQueryElectronInvoice","shield");
    }

    @Override
    public void before(ServiceContext context) {
        setRequestId(context);
        context.attr(ACCESS_LOG, CLIENT_APPID, Optional.ofNullable(HttpRequestContext.getInstance()).map(HttpRequestContext::request).map(HttpRequestWrapper::clientAppId).orElse(null));
        context.attr(ACCESS_LOG_TIME, BEGIN, System.currentTimeMillis());
        context.attr(ACCESS_LOG, IS_INFO_SEC, Cat.isFromInfoSec() ? "1" : "0");
    }

    @Override
    public void after(ServiceContext context) {
        LOG_POOL.submit(() -> {
            try {
                Long t = context.getAttribute(ACCESS_LOG_TIME, BEGIN);
                if (t != null) {
                    context.attr(ACCESS_LOG, "begin-time", DateFormatUtils.format(t, "yyyy-MM-dd HH:mm:ss"));
                    context.attr(ACCESS_LOG, "time", System.currentTimeMillis() - t);
                }

                recordLog(context);
            } catch (Exception e) {
                logger.error("序列化信息错误", e);
            }
        });
    }

    private void recordLog(ServiceContext context) {
        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(context.getRequest4Log());
        String method = descriptor.getName();
        context.attr(ACCESS_LOG, "method", method);
        Object request = sdFrameworkQConfig.isLogOriginRequest() ? context.getRequest4Log() : context.getRequest();
        Object response = context.getResponse();
        if (maskMethodList.contains(method)) {
            //clone对象
            request = BeanCopyUtils.clone(request);
            response = BeanCopyUtils.clone(response);
            maskSensitiveInfo(request, response, descriptor);
        }

        LogContext logContext = buildLogContext(context, request, response);

        // diablelog配置3 日志不记录
        if (logContext.getDisableLog() != 3) {
            CatFactory.logTags(logContext);
            logger.info(logContext.getTitle(), buildLogContent(logContext), logContext.getLogTags());
        }
        //track stability targets
        recordTracker(logContext);
    }

    private LogContext buildLogContext(ServiceContext context, Object request, Object response) {

        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(context.getRequest4Log());
        String serviceName = descriptor.getServiceName();
        String method = descriptor.getName();

        int disableLog = sdFrameworkQConfig.disableLog(method);
        boolean needCompress = sdFrameworkQConfig.needCompressMethod(method);
        int compressAlgorithm = sdFrameworkQConfig.compressAlgorithm();

        String title = String.format("%s_%s", descriptor.getServiceName(), descriptor.getName());
        Map<String, String> extraTags = getExtraTags(context);
        Map<String, String> logTags = buildLogTags(context);

        LogContext logContext = new LogContext(disableLog, needCompress, compressAlgorithm);
        logContext.setServiceName(serviceName);
        logContext.setMethod(method);
        logContext.setTitle(title);
        logContext.setRequest(request);
        logContext.setResponse(response);
        logContext.setExtraTags(extraTags);
        logContext.setLogTags(logTags);
        return logContext;
    }

    private Map<String, String> buildLogTags(ServiceContext context) {
        Map<String, String> tags = CatFactory.getCommonTags();
        tags.putAll(context.getCurrentStringAttributes());
        return tags;
    }

    private String buildLogContent(LogContext logContext) {
        String compressType = "";
        if (logContext.isNeedCompress()) {
            compressType = "压缩算法 ：" + (logContext.getCompressAlgorithm()== 0 ? "GZIP" : "ZSTD");
        }
        return compressType + "\n请求报文 ：\n" +
                logContext.getRequestBody() +
                "\n响应报文 ：\n" +
                logContext.getResponseBody() +
                "\n异常堆栈 ：\n" +
                (Objects.isNull(logContext.getException()) ? null : ExceptionUtils.getStackTrace(logContext.getException()));
    }

    private void recordTracker(LogContext logContext) {
        try {
            String appId = Foundation.app().getAppId();
            //reject self service circle invoke
            if (appId.equals("100025206")) {
                return;
            }

            Object request = logContext.getRequest();
            Object response = logContext.getResponse();

            Map<String, String> trackerMaps = new HashMap<>();
            trackerMaps.put("key", "183828");
            trackerMaps.put("keyId", "183828");
            trackerMaps.put("keyName", "car_trace_api_topalert");
            trackerMaps.put("keyfrom", "trackFramework");
            trackerMaps.put("appId", appId);
            trackerMaps.put("serviceName", logContext.getServiceName());
            trackerMaps.put("apiName", logContext.getMethod());
            trackerMaps.put("clientIP", Foundation.net().getHostAddress());
            trackerMaps.put("clientIDC", Foundation.server().getDataCenter());

            trackerMaps.putAll(logContext.getExtraTags());
            trackerMaps.putAll(LogTagUtil.getindexTags("blueprint_track", request, response));
            trackerMaps.putAll(LogTagUtil.getExtMaps(request, response));

            TrackerRequestType trackRequest = new TrackerRequestType();
            trackRequest.setExtendInfo(trackerMaps);
            //配置服务端埋点只发QMQ消息-替代原有调Client方式(过渡)
            if (!sdFrameworkQConfig.isOnlyTrackQMQ()) {
                NotificationCenterProxy.trackerAsync(trackRequest);
            }
            NotificationCenterProxy.trackerQmq(trackRequest);
        } catch (Exception e) {
            logger.warn("recordTracker", e);
        }
    }

    private Map<String, String> getExtraTags(ServiceContext context) {
        Map<String,String> extraTags = new HashMap<>();
        Long t = context.getAttribute(ACCESS_LOG_TIME, BEGIN);
        if (t != null) {
            long l = System.currentTimeMillis() - t;
            extraTags.put("cost", Long.toString(l));
        }
        String requestId = context.getAttribute(ACCESS_LOG, REQUEST_ID);
        if (StringUtils.isNotEmpty(requestId)) {
            extraTags.put("requestId", requestId);
        }
        String clientAppId = context.getAttribute(ACCESS_LOG, CLIENT_APPID);
        if (StringUtils.isNotEmpty(clientAppId)) {
            extraTags.put("clientAppId", clientAppId);
        }
        String isInfoSec = context.getAttribute(ACCESS_LOG, IS_INFO_SEC);
        if (StringUtils.isNotEmpty(isInfoSec)) {
            extraTags.put("isInfoSec", isInfoSec);
        }
        extraTags.put("log_type", "server");
        return extraTags;
    }

    @Override
    public void exception(ServiceContext context, Exception exception) {
        Long t = context.getAttribute(ACCESS_LOG_TIME, BEGIN);
        if (t != null) {
            context.attr(ACCESS_LOG, "begin-time", DateFormatUtils.format(t, "yyyy-MM-dd HH:mm:ss"));
            context.attr(ACCESS_LOG, "time", System.currentTimeMillis() - t);
        }

        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(context.getRequest4Log());
        LogContext logContext = buildExceptionLogContext(context, exception);
        context.attr(ACCESS_LOG, "method", descriptor.getName());

        logger.info(logContext.getTitle(), buildLogContent(logContext), context.getCurrentStringAttributes());
        // ESLog
        CatFactory.logTags(logContext);
    }

    private LogContext buildExceptionLogContext(ServiceContext context, Exception exception) {
        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(context.getRequest4Log());
        String method = descriptor.getName();
        LogContext logContext = new LogContext(0, sdFrameworkQConfig.needCompressMethod(method), sdFrameworkQConfig.compressAlgorithm());
        logContext.setTitle(String.format("%s_%s", descriptor.getServiceName(), descriptor.getName()));
        logContext.setRequest(context.getRequest4Log());
        logContext.setResponse(context.getResponse());
        logContext.setException(exception);
        logContext.setExtraTags(getExtraTags(context));
        return logContext;
    }

    @Override
    public String name() {
        return ACCESS_LOG;
    }

    private void setRequestId(ServiceContext context) {
        Object request = context.getRequest4Log();
        if (request != null) {
            try {
                String baseRequestName = "baseRequest";
                Field baseRequestField = null;
                boolean hasBaseRequest = PropertyUtils.isReadable(request, baseRequestName);
                if (hasBaseRequest) {
                    baseRequestField = request.getClass().getDeclaredField(baseRequestName);
                } else {
                    String requestHeader = "requestHeader";
                    boolean hasRequestHeader = PropertyUtils.isReadable(request, requestHeader);
                    if (hasRequestHeader) {
                        baseRequestField = request.getClass().getDeclaredField(requestHeader);
                    }
                }

                if (baseRequestField != null) {
                    baseRequestField.setAccessible(true);
                    Object baseRequest = baseRequestField.get(request);
                    if (baseRequest != null) {
                        setAccessLog(context, baseRequest, "requestId");
                        setAccessLog(context, baseRequest, "sessionId");
                    }
                }
            } catch (Exception e) {

            }
        }

        if (Objects.isNull(context.getAttribute(ACCESS_LOG, REQUEST_ID))) {
            String requestId = BaseInfoHelper.getRequestId(request);
            if (StringUtils.isNotEmpty(requestId)) {
                context.attr(ACCESS_LOG, REQUEST_ID, requestId);
            }
        }
    }

    private void setAccessLog(ServiceContext context, Object baseRequest, String name) {
        try {
            boolean hasProperty = PropertyUtils.isWriteable(baseRequest, name);
            if (hasProperty) {
                Field declaredField = baseRequest.getClass().getDeclaredField(name);
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    Object requestId = declaredField.get(baseRequest);
                    if (requestId != null && requestId instanceof String) {
                        context.attr(ACCESS_LOG, name, requestId.toString());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void maskSensitiveInfo(Object request, Object response, ExecutorDescriptor descriptor) {
        //更新订单
        processMask(request, "requestInfo", "orderUserInfo");
        //查询订单详情返回
        processMask(response, "responseInfo", "driverInfo");
        //ibu restful 更新订单
        processMask(request, "orderUserInfo");
        //ibu restful 查询订单
        processMask(response, "driverInfo");
        //ibu restful 提车凭证
        processMask(response, "userInfo");
        //ibu restful 电子发票
        processMask(response, "clientName",MaskType.NAME);
        //ibu app restful 更新订单
        processMask(request, "updateTempOrderRequest","orderUserInfo");

    }

    /*
    //多级目录脱敏
    private void processMask(Object obj, String... fieldNames) {
        try {
            Object subInstance = obj;
            for (String fieldName : fieldNames) {
                if(PropertyUtils.isReadable(subInstance, fieldName)){
                    Field field = obj.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    subInstance = field.get(obj);
                }else{
                    return;
                }
            }
            Iterator<Map.Entry<String, MaskType>> entries = maskMap.entrySet().iterator();
            while (entries.hasNext()){
                Map.Entry<String, MaskType> entry = entries.next();
                doMask(subInstance, entry.getKey(), entry.getValue());
            }
        }catch (Exception e){
        }
    }*/

    private void processMask(Object obj, String fieldName, String subFieldName) {
        boolean hasField = PropertyUtils.isReadable(obj, fieldName);
        if (hasField) {
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldInstance = field.get(obj);
                if (PropertyUtils.isReadable(fieldInstance, subFieldName)) {
                    Field subField = fieldInstance.getClass().getDeclaredField(subFieldName);
                    subField.setAccessible(true);
                    Object subInstance = subField.get(fieldInstance);
                    maskMap.forEach((k,v) -> doMask(subInstance, k, v));
                }
            } catch (Exception e) {
            }
        }
    }

    private void processMask(Object obj, String fieldName) {
        boolean hasField = PropertyUtils.isReadable(obj, fieldName);
        if (hasField) {
            try {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldInstance = field.get(obj);
                maskMap.forEach((k,v) -> doMask(fieldInstance, k, v));
            } catch (Exception e) {
            }
        }
    }

    private void processMask(Object obj, String fieldName ,MaskType maskType) {
        boolean hasField = PropertyUtils.isReadable(obj, fieldName);
        if (hasField) {
            try {
                doMask(obj, fieldName, maskType);
            } catch (Exception e) {
            }
        }
    }

    private void doMask(Object obj, String fieldName, MaskType maskType) {
        if (PropertyUtils.isReadable(obj, fieldName)) {
            try {
                Class<?> objClass = obj.getClass();
                Field targetField = objClass.getDeclaredField(fieldName);
                targetField.setAccessible(true);
                String target = (String) targetField.get(obj);
                Method targetMethod = objClass.getDeclaredMethod(getSetMethod(fieldName), targetField.getType());
                switch (maskType) {
                    case EMAIL:
                        targetMethod.invoke(obj, IbuGDPRUtils.hashString(null, GDPRType.gdpr_contact_email,target));
                        break;
                    case PHONE:
                        targetMethod.invoke(obj, IbuGDPRUtils.hashString(null, GDPRType.gdpr_contact_mobilephone,target));
                        break;
                    case NAME:
                        targetMethod.invoke(obj, IbuGDPRUtils.hashString(null, GDPRType.gdpr_contact_name,target));
                        break;
                    case ID_NUMBER:
                        targetMethod.invoke(obj, IbuGDPRUtils.hashString(null, GDPRType.gdpr_contact_email,target));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
            }
        }
    }

    private String getSetMethod(String name) {
        StringBuilder setMethod = new StringBuilder("set");
        setMethod.append(name.substring(0, 1).toUpperCase());
        setMethod.append(name.substring(1));
        return setMethod.toString();
    }

    enum MaskType {
        EMAIL, PHONE, NAME, ID_NUMBER
    }

}
