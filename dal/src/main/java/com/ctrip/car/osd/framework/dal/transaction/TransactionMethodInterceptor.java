package com.ctrip.car.osd.framework.dal.transaction;

import java.sql.SQLException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.car.osd.framework.common.AssertionConcern;
import com.ctrip.car.osd.framework.dal.query.Dal;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;

public class TransactionMethodInterceptor extends AssertionConcern implements MethodInterceptor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("Transaction");

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(	!invocation.getMethod().isAnnotationPresent(Transactional.class) ) {
			return invocation.proceed();
		}
		Transactional transactional = invocation.getMethod().getAnnotation(Transactional.class);
		String databaseName = transactional.value();
		
		if (StringUtils.isBlank(databaseName)) {
			Class<?> declaringType = invocation.getMethod().getDeclaringClass();
			if (declaringType.isAnnotationPresent(Dal.class)) {
				databaseName = declaringType.getAnnotation(Dal.class).value();
			} else if (declaringType.isAnnotationPresent(Transactional.class)) {
				databaseName = declaringType.getAnnotation(Transactional.class).value();
			}
		}
		
		this.assertArgumentNotEmpty(databaseName, "The Transcational value is required.");
		
		DalClient client = DalClientFactory.getClient(databaseName);
		DalCommand command =  newCommand(invocation);
		DalHints hints = new DalHints();
		if (transactional.isolation() > Transactional.ISOLATION_DEFAULT) {
			hints.setIsolationLevel(transactional.isolation());
		}
		LOGGER.debug("begin transcation {}", invocation.getMethod().getName());
		client.execute(command, hints);
		LOGGER.debug("end transcation {}", invocation.getMethod().getName());
		return TransactionUtils.get();
	}
	
	private DalCommand newCommand(MethodInvocation point) {
		return new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				try {
					Object result = point.proceed();
					TransactionUtils.set(result);
					return true;
				} catch (Throwable e) {
					LOGGER.error("Transaction Error.", e);
					throw new IllegalArgumentException(e);
				}
			}
		};
	}

}
