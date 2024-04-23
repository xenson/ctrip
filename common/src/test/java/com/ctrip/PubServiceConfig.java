package com.ctrip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;

@SpringBootApplication
public class PubServiceConfig implements CommandLineRunner {

	@Autowired
	private CtripConfigProperties ctripServiceProperties;

	public static void main(String[] args) {
		SpringApplication.run(PubServiceConfig.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		
	}



}
