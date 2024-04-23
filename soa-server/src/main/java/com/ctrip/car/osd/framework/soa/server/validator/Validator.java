package com.ctrip.car.osd.framework.soa.server.validator;

import com.ctriposs.baiji.rpc.server.validation.AbstractValidator;

/**
 * 初始化验证框架  将SOA validator合并 
 * 
 * @author xiayx@ctrip.com
 *
 */
public interface Validator<T> {

	void validate(AbstractValidator<T> validator);
	
}
