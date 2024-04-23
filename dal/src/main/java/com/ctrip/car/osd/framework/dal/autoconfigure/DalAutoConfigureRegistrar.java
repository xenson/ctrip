package com.ctrip.car.osd.framework.dal.autoconfigure;

import java.util.Set;

import com.ctrip.car.osd.framework.common.scan.ScanClassAutoConfigureRegister;
import com.ctrip.car.osd.framework.common.scan.Scanner;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;

public class DalAutoConfigureRegistrar extends ScanClassAutoConfigureRegister {

	@Override
	protected Set<Class<?>> doScan(Scanner scanner) {
		final String packageNameSuffix = this.environment.getProperty("ctrip.dal.package-suffix", "dao");
		final String classNameSuffix = this.environment.getProperty("ctrip.dal.class-name-suffix", "dao");
		Set<Class<?>> dalDaoClassess = scanner.scanWith(packageNameSuffix, classNameSuffix);
		dalDaoClassess.remove(DalTableDao.class);
		dalDaoClassess.remove(DalQueryDao.class);
		return dalDaoClassess;
	}

}
