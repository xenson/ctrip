package com.ctrip.car.osd.framework.soa.server.exception;

import com.ctriposs.baiji.rpc.server.validation.results.ValidationResult;

public class ValidationException extends Exception {

	private static final long serialVersionUID = 1L;
	private ValidationResult result;

	public ValidationException(ValidationResult result) {
		super();
		this.result = result;
	}

	public ValidationResult getResult() {
		return result;
	}

}
