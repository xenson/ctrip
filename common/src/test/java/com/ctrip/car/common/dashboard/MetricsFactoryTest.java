package com.ctrip.car.common.dashboard;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.car.osd.framework.common.clogging.MetricsFactory;

public class MetricsFactoryTest {

	static {
		// set AppId and collector address
		LogConfig.setAppID("100006077"); // 你的appid
		LogConfig.setLoggingServerIP("collector.logging.fws.qa.nt.ctripcorp.com"); // 配置Collector的域名,发往FWS环境
		LogConfig.setLoggingServerPort("63100"); // 配置Collector的端口号
	}

	@Test
	public void testCreateMetricsString() throws InterruptedException {
		
		
		for( int i = 0 ; i < 1000 ; i++ ) {
			MetricsFactory.createMetrics("dashboard.oday.val" , 10 * i).build();
			System.out.println(i);
			Thread.sleep(100);
		}
		
		 Thread.sleep(1000000);
	}

	@Test
	public void testCreateMetricsStringDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateMetricsStringLong() {
		fail("Not yet implemented");
	}

}
