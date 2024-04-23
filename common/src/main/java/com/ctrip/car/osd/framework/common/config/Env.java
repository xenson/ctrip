package com.ctrip.car.osd.framework.common.config;

import com.google.common.collect.ImmutableSet;

public enum Env {

	prod {
		@Override
		public boolean isProd() {
			return true;
		}
	},
	uat {
		@Override
		public boolean isUat() {
			return true;
		}

	},
	fws {
		@Override
		public boolean isFws() {
			return true;
		}
	},
	fat {
		@Override
		public boolean isFat() {
			return true;
		}
	},

	lpt {
		@Override
		public boolean isLpt() {
			return true;
		}
	},
	dev {
		@Override
		public boolean isDev() {
			return true;
		}
	},
	local {
		@Override
		public boolean isLocal() {
			return true;
		}
	},
	test,
	all ;

	public boolean isAll() {
		return true;
	}

	public boolean isTest() {
		return isUat() || isFws() || isFat() || isFws() || isLpt() || isDev() || isLocal();
	}

	public boolean isProd() {
		return false;
	}

	public boolean isUat() {
		return false;
	}

	public boolean isFws() {
		return false;
	}

	public boolean isFat() {
		return false;
	}

	public boolean isLpt() {
		return false;
	}

	public boolean isDev() {
		return false;
	}

	public boolean isLocal() {
		return false;
	}

	public static Env of(String envType) {

		if (envType == null || envType.isEmpty()) {
			return dev;
		}
		
		envType = envType.toLowerCase();
		if (envType.startsWith("fws"))
			return fws;
		if (envType.startsWith("fat"))
			return fat;
		if (envType.startsWith("lpt"))
			return lpt;
		if (envType.startsWith("uat"))
			return uat;
		if (envType.startsWith("local")){
			return local;
		}
		if (ImmutableSet.of("prod", "prd", "pro").contains(envType.toLowerCase())) {
			return prod;
		}
		try {
			return valueOf(envType.toLowerCase());
		} catch (Exception ex) {
			throw ex;
		}
	}
}
