package com.ctrip.car.osd.framework.soa.server;

import com.ctrip.basebiz.accounts.mobile.request.filter.util.AccountsMobileRequestUtils;
import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.common.exception.BizException;
import com.ctrip.car.osd.framework.common.spring.SpringApplicationContextHolder;
import com.ctrip.car.osd.framework.common.utils.TypeUtils;
import com.ctrip.car.osd.framework.soa.server.exception.ValidationException;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceContext;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceContextUtils;
import com.ctrip.car.osd.framework.soa.server.validator.ValidatorRunner;
import com.ctriposs.baiji.rpc.common.HasMobileRequestHead;
import com.ctriposs.baiji.rpc.common.util.ServiceUtils;
import com.ctriposs.baiji.rpc.server.HttpRequestContext;
import com.ctriposs.baiji.rpc.server.HttpRequestWrapper;
import com.ctriposs.baiji.rpc.server.HttpResponseWrapper;


/**
 * 默认服务处理器
 *
 * @param <Req>
 * @param <Resp>
 * @author xiayx@Ctrip.com
 */
@SuppressWarnings("unchecked")
public abstract class ServiceExecutor<Req, Resp> extends BaseService implements Executor<Req, Resp> {

    public static final String MobileAuthLoginTypeExtensionKey = "mobile-auth-login-type";
    public static final String MobileSecondAuthExtensionKey = "sauth";
    public static final String MobileAuthTokenExtensionKey = "auth";
    public static final String MobileUserIdExtensionKey = "uid";
    public static final String MobileUserPhoneExtensionKey = "uphone";
    public static final String MobileIsNonMemberAuthExtensionKey = "IsNonMemberAuth";
    public static final String MobileIsMemberAuthExtensionKey = "IsMemberAuth";
    public static final String MobileAuthCookieKey = "cticket";
    public static final String NonMemberAuthType = "GetOrder";

    private Class<Req> requestServiceType;
    private Class<Resp> responseServiceType;

    protected Class<Req> getRequestType() {
        if (requestServiceType == null) {
            requestServiceType = (Class<Req>) TypeUtils.getGenericType(getClass(), Executor.class);
        }
        return requestServiceType;
    }

    protected Class<Resp> getResponseType() {
        if (responseServiceType == null) {
            responseServiceType = (Class<Resp>) TypeUtils.getGenericType(getClass(), Executor.class, 1);
        }

        return responseServiceType;
    }

    protected HttpRequestWrapper getHttpRequest() {
        return HttpRequestContext.getInstance().request();
    }

    protected HttpResponseWrapper getHttpResponse() {
        return HttpRequestContext.getInstance().response();
    }

    protected String getExtensionData(HasMobileRequestHead request, String key) {
        return ServiceUtils.getExtensionData(request, key);
    }

    protected boolean isMemberAuth(HasMobileRequestHead request) {
        return AccountsMobileRequestUtils.isMemberAuthNew(request);
    }

    protected boolean isNonMemberAuth(HasMobileRequestHead request) {
        return AccountsMobileRequestUtils.isNonMemberAuthNew(request);
    }

    protected boolean isNonMemberAuthLoginType(String loginType) {
        return AccountsMobileRequestUtils.isNonMemberAuthLoginTypeNew(loginType);
    }

    protected String getMobileAuthLoginType(HasMobileRequestHead request) {
        return AccountsMobileRequestUtils.getMobileAuthLoginType(request);
    }

    protected String getSauth(HasMobileRequestHead mobileRequest) {
        return AccountsMobileRequestUtils.getSauth(mobileRequest, HttpRequestContext.getInstance().request());
    }

    protected String getAuth(HasMobileRequestHead mobileRequest) {
        return AccountsMobileRequestUtils.getAuth(mobileRequest);
    }

    @Override
    public Resp execute(Req req) throws Exception {
        Resp resp = null;
        try {
            validate(req, new ValidatorRunner());
            before(req);
            resp = (Resp) SpringApplicationContextHolder.getBean(this.getClass()).service(req);
            after(req, resp);
            return resp;
        } catch (Exception e) {
            resp = exception(req, e);
            if (resp != null) {
                return resp;
            }
            throw e;
        } finally {
            doFinally(req, resp);
        }
    }

    protected void validate(Req req, ValidatorRunner vr) throws ValidationException {

    }

    protected abstract Resp service(Req req) throws Exception;

    /**
     * 前置处理
     *
     * @param req
     */
    protected void before(Req req) throws Exception {
        ServiceContext context = ServiceContextUtils.getContext();
        before(req, context);
    }

    protected void before(Req req, ServiceContext context) {
    }

    /**
     * 后置处理
     *
     * @param req
     * @param resp
     */
    protected void after(Req req, Resp resp) throws Exception {
        ServiceContext context = ServiceContextUtils.getContext();
        after(req, resp, context);
    }

    protected void after(Req req, Resp resp, ServiceContext context) {
    }

    /**
     * @param req
     * @param resp
     */
    protected void doFinally(Req req, Resp resp) {
    }

    /**
     * 异常处理
     *
     * @param req
     * @param ex
     * @return
     */
    protected Resp exception(Req req, Exception ex) {
        ServiceContext context = ServiceContextUtils.getContext();
        Resp resp = null;
        if (ex instanceof BizException) {
            resp = exception(req, (BizException) ex, context);
        } else if (ex instanceof RuntimeException) {
            resp = exception(req, (RuntimeException) ex, context);
        }
        if (resp != null) {
            return resp;
        }
        return exception(req, ex, context);
    }

    protected Resp exception(Req req, Exception ex, ServiceContext context) {
        return null;
    }

    protected Resp exception(Req req, BizException ex, ServiceContext context) {
        if (ex.getExceptionObj() != null) {
            return (Resp) ex.getExceptionObj();
        } else {
            return null;
        }
    }

    protected Resp exception(Req req, RuntimeException ex, ServiceContext context) {
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("service executor");
        sb.append("[").append(getRequestType()).append(",");
        sb.append(getResponseType()).append("]");
        return sb.toString();
    }

}
