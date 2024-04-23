package com.ctrip.car.osd.framework.soa.server.serviceinterceptor;

import com.ctrip.car.osd.framework.common.utils.BeanCopyUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ServiceContext {

	private Object request;
	private Object response;
	private Map<String, Object> attributes;
	private String currentName;
	private Object request4Log;

	public ServiceContext(Object request, Object response) {
		super();
		this.request = request;
		this.response = response;
		this.attributes = new HashMap<>();
		this.request4Log = BeanCopyUtils.clone(request);
	}

	public ServiceContext(Object request, Object response, Map<String, Object> attributes) {
		super();
		this.request = request;
		this.response = response;
		this.attributes = attributes;
		this.request4Log = BeanCopyUtils.clone(request);
	}
	
	void setCurrent(String currentName){
		this.currentName = currentName;
	}

	void global(){
		this.currentName = "";
	}
	
	public <T> T getRequest() {
		return (T) request;
	}

	public <T> T getResponse() {
		return (T) response;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Map<String, String> getStringAttributes() {
		return asStringAttributes(attributes);
	}

	public Map<String, Object> getAttributes(String name) {
		Map<String, Object> results = new HashMap<>();
		for (Entry<String, Object> entry : this.attributes.entrySet()) {
			if (entry.getKey().startsWith(name + ":")) {
				results.put(entry.getKey().substring(name.length() + 1), entry.getValue());
			}
		}
		return results;
	}

	public Map<String, String> getStringAttributes(String name) {
		return asStringAttributes(getAttributes(name));
	}
	
	
	public Map<String, Object> getCurrentAttributes() {
		return getAttributes(this.currentName);
	}

	public Map<String, String> getCurrentStringAttributes() {
		return getStringAttributes(this.currentName);
	}

	private Map<String, String> asStringAttributes(Map<String, Object> attributes) {
		Map<String, String> results = new HashMap<>();
		for (Entry<String, Object> entry : attributes.entrySet()) {
			if (entry.getValue() != null) {
				results.put(entry.getKey(), entry.getValue().toString());
			}
		}
		return results;
	}

	public ServiceContext attr(String name, String attr, Object value) {
		this.attributes.put(buildKey(name, attr), value);
		return this;
	}

	public ServiceContext attrs(String name, Map<String, Object> attrs) {
		for (Entry<String, Object> entry : attrs.entrySet()) {
			this.attributes.put(this.buildKey(name, entry.getKey()), entry.getValue());
		}
		return this;
	}
	
	public <T> T getAttribute(String name, String attr){
		 if( this.attributes.containsKey(buildKey(name, attr)) ) {
			 return (T) this.attributes.get(buildKey(name, attr));
		 }
		 return null;
	}

	private String buildKey(String name, String attr) {
		return name + ":" + attr;
	}

	public <T> T getRequest4Log() {
		return (T) request4Log;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

}
