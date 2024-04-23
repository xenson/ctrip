package com.ctrip.car.osd.framework.common.clogging;

import com.dianping.cat.Cat;
import com.dianping.cat.message.ForkedTransaction;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class CatTransaction {

	private Transaction transaction;

	public CatTransaction(Transaction transaction) {
		super();
		this.transaction = transaction;
	}

	public CatTransaction add(String key, Object value) {
		transaction.addData(key, value);
		return this;
	}

	public CatTransaction log(String type, String name) {
		Cat.logEvent(type, name);
		return this;
	}

	public CatTransaction fork() {
		if (this.transaction instanceof ForkedTransaction) {
			ForkedTransaction forkedTransaction = (ForkedTransaction) this.transaction;
			forkedTransaction.fork();
		}
		return this;
	}

	public CatTransaction success() {
		transaction.setStatus(Message.SUCCESS);
		return this;
	}

	public CatTransaction error(Exception exception) {
		Cat.logError(exception);
		transaction.setStatus(exception);
		return this;
	}

	public CatTransaction complete() {
		transaction.complete();
		return this;
	}

}
