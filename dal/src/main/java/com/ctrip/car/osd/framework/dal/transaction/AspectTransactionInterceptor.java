package com.ctrip.car.osd.framework.dal.transaction;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.car.osd.framework.common.AssertionConcern;
import com.ctrip.car.osd.framework.dal.query.Dal;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;

/**
 * 
 * 事务拦截器
 * 
 * @author xiayx@Ctrip.com
 *
 */
@Aspect
public class AspectTransactionInterceptor extends AssertionConcern {

	private static final Logger LOGGER = LoggerFactory.getLogger("Transaction");

	@Around("@annotation(transactional)")
	public Object doDalTransaction(ProceedingJoinPoint point, Transactional transactional) throws Throwable {

		String databaseName = getDatabaseName(point, transactional);
		this.assertArgumentNotEmpty(databaseName, "The Transcational value is required.");

		DalClient client = DalClientFactory.getClient(databaseName);
		DalCommand command = newCommand(point);
		DalHints hints = new DalHints();
		if (transactional.isolation() > Transactional.ISOLATION_DEFAULT) {
			hints.setIsolationLevel(transactional.isolation());
		}
		LOGGER.debug("begin transcation {}", point.getSignature().getName());
		client.execute(command, hints);
		LOGGER.debug("end transcation {}", point.getSignature().getName());

		return TransactionUtils.get();
	}

	private String getDatabaseName(ProceedingJoinPoint point, Transactional transactional) {
		String databaseName = transactional.value();
		if (StringUtils.isBlank(databaseName)) {
			Class<?> declaringType = point.getSignature().getDeclaringType();
			if (declaringType.isAnnotationPresent(Dal.class)) {
				databaseName = declaringType.getAnnotation(Dal.class).value();
			} else if (declaringType.isAnnotationPresent(Transactional.class)) {
				databaseName = declaringType.getAnnotation(Transactional.class).value();
			}
		}
		return databaseName;
	}

	private DalCommand newCommand(ProceedingJoinPoint point) {
		return new DalCommand() {
			@Override
			public boolean execute(DalClient client) throws SQLException {
				try {
					Object result = point.proceed();
					TransactionUtils.set(result);
					return true;
				} catch (Throwable e) {
					throw new IllegalArgumentException(e);
				}
			}
		};
	}

}
