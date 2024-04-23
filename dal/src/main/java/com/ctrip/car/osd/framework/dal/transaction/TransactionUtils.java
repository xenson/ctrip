package com.ctrip.car.osd.framework.dal.transaction;

public class TransactionUtils {

	private static final ThreadLocal<Object> holder = new ThreadLocal<Object>();

	static void set(Object result) {
		holder.set(result);
	}

	static Object get() {
		Object result = holder.get();
		holder.remove();
		return result;
	}

}
