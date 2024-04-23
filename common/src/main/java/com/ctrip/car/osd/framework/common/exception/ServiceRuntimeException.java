package com.ctrip.car.osd.framework.common.exception;

public class ServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceRuntimeException() {
		super();
	}

	public ServiceRuntimeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ServiceRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceRuntimeException(String arg0) {
		super(arg0);
	}

	public ServiceRuntimeException(Throwable arg0) {
		super(arg0);
	}
	
	

}
