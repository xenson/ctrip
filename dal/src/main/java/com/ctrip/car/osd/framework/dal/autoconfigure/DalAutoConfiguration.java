package com.ctrip.car.osd.framework.dal.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.ctrip.car.osd.framework.dal.DalClientFactoryInitializing;
import com.ctrip.car.osd.framework.dal.query.DalQuery;

@Configuration
@Import({ DalRepositoryAutoConfigureRegister.class })
@ConditionalOnClass(DalClientFactoryInitializing.class)
public class DalAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DalClientFactoryInitializing dalClientFactoryInitializing() {
		return new DalClientFactoryInitializing();
	}
	
	@Bean
	public DalQuery dalQuery(){
		return new DalQuery();
	}

}
