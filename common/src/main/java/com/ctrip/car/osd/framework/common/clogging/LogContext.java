package com.ctrip.car.osd.framework.common.clogging;

import com.ctrip.car.osd.framework.common.utils.compress.CompressFactory;

import java.util.Map;

public class LogContext {

    private int disableLog;
    private boolean needCompress;
    private int compressAlgorithm;

    private String serviceName;
    private String method;

    private String title;
    private Object request;
    private Object response;
    private Exception exception;
    private String requestBody;
    private String responseBody;
    private Map<String, String> extraTags;
    private Map<String, String> logTags;

    public LogContext(int disableLog, boolean needCompress, int compressAlgorithm) {
        this.disableLog = disableLog;
        this.needCompress = needCompress;
        this.compressAlgorithm = compressAlgorithm;
    }

    public int getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(int disableLog) {
        this.disableLog = disableLog;
    }

    public boolean isNeedCompress() {
        return needCompress;
    }

    public void setNeedCompress(boolean needCompress) {
        this.needCompress = needCompress;
    }

    public int getCompressAlgorithm() {
        return compressAlgorithm;
    }

    public void setCompressAlgorithm(int compressAlgorithm) {
        this.compressAlgorithm = compressAlgorithm;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
        this.requestBody = CompressFactory.compressBody(request, needCompress, compressAlgorithm, disableLog == 2 || disableLog == 3);
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
        this.responseBody = CompressFactory.compressBody(response, needCompress, compressAlgorithm, disableLog != 0);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Map<String, String> getExtraTags() {
        return extraTags;
    }

    public void setExtraTags(Map<String, String> extraTags) {
        this.extraTags = extraTags;
    }

    public Map<String, String> getLogTags() {
        return logTags;
    }

    public void setLogTags(Map<String, String> logTags) {
        this.logTags = logTags;
    }
}
