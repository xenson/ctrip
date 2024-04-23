package com.ctrip.car.osd.framework.soa.server.exception;

public class SOAServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SOAServiceException() {
		super();
	}

	public SOAServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SOAServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SOAServiceException(String message) {
		super(message);
	}

	public SOAServiceException(Throwable cause) {
		super(cause);
	}

}
