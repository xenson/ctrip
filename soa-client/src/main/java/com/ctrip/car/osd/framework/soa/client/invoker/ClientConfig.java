package com.ctrip.car.osd.framework.soa.client.invoker;

public class ClientConfig {

    private String format;
    private int requestTimeout;
    private int socketTimeout;
    private int maxConnectionPerRoute;
    private int connectTimeout;
    private int idleTime;
    private boolean logEnable;
    private boolean ignoreError;

    public boolean isLogEnable() {
        return logEnable;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }

    public ClientConfig() {
        super();
    }

    public ClientConfig(String format, int requestTimeout, int socketTimeout, int maxConnectionPerRoute,
                        int connectTimeout, int idleTime, boolean logEnable, boolean ignoreError) {
        super();
        this.format = format;
        this.requestTimeout = requestTimeout;
        this.socketTimeout = socketTimeout;
        this.maxConnectionPerRoute = maxConnectionPerRoute;
        this.connectTimeout = connectTimeout;
        this.idleTime = idleTime;
        this.logEnable = logEnable;
        this.ignoreError = ignoreError;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxConnectionPerRoute() {
        return maxConnectionPerRoute;
    }

    public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public boolean isIgnoreError() {
        return ignoreError;
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }

}
