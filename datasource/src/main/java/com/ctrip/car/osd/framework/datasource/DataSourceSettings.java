package com.ctrip.car.osd.framework.datasource;

public class DataSourceSettings {

	private String allInOneKey;
	private String svcUrl;
	private String appId;
	private String driver;
	private String url;
	private String user;
	private String pass;
	private int min;
	private int max;
	
	private String dbType;

	public String getAllInOneKey() {
		return allInOneKey;
	}

	public void setAllInOneKey(String allInOneKey) {
		this.allInOneKey = allInOneKey;
	}

	public String getSvcUrl() {
		return svcUrl;
	}

	public void setSvcUrl(String svcUrl) {
		this.svcUrl = svcUrl;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

}
