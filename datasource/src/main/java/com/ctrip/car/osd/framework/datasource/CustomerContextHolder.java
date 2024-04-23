package com.ctrip.car.osd.framework.datasource;

public class CustomerContextHolder {

	private static final ThreadLocal<CustomerKey> contextHolder = new ThreadLocal<CustomerKey>();

	public static void setCustomerType(String customerType) {
		contextHolder.set(new CustomerKey(contextHolder.get()).setHighKey(customerType));
	}

	public static void setLowType(String customerType) {
		contextHolder.set(new CustomerKey(contextHolder.get()).setLowKey(customerType));
	}

	public static void setHighType(String customerType) {
		contextHolder.set(new CustomerKey(contextHolder.get()).setHighKey(customerType));
	}

	public static String getCustomerType() {
		CustomerKey key = contextHolder.get();
		if (key == null) {
			return null;
		}
		return key.getKey();
	}

	public static void clearCustomerType() {
		contextHolder.remove();
	}

	static class CustomerKey {

		String lowKey;
		String highKey;

		private CustomerKey(CustomerKey customerKey) {
			if (customerKey != null) {
				setHighKey(customerKey.highKey);
				setLowKey(customerKey.lowKey);
			}
		}

		public CustomerKey setLowKey(String lowKey) {
			this.lowKey = lowKey;
			return this;
		}

		public CustomerKey setHighKey(String highKey) {
			this.highKey = highKey;
			return this;
		}

		public String getKey() {

			if (highKey != null && !highKey.isEmpty()) {
				return this.highKey;
			}

			if (lowKey != null && !lowKey.isEmpty()) {
				return this.lowKey;
			}
			return null;
		}

	}

}
