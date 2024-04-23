package com.ctrip.car.osd.framework.dal;

/**
 * Created by dywang on 2016/11/8.
 */
public class DalRepositoryExcetion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DalRepositoryExcetion(Exception ex) {
		super(ex);
	}
}
