package com.ctrip;

import com.ctriposs.baiji.rpc.server.BaijiServlet;
import com.dtp.core.spring.EnableDynamicTp;
import javassist.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Modifier;

@EnableDynamicTp
@SpringBootApplication
public class WebServiceSpringBootServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(WebServiceSpringBootServletInitializer.class);
	}

	@Bean
	public ServletRegistrationBean dispatcherServlet() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new BaijiServlet(), "/*");
		return servletRegistrationBean;
	}

//	@Bean
//	@ConfigurationProperties(prefix = "ctrip.osd.baiji")
//	public ServletRegistrationBean baijiServlet() {
//		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new BaijiServlet(), "/*");
//		servletRegistrationBean.setName("baijiServlet");
//		return servletRegistrationBean;
//	}

	private static final ClassPool pool = ClassPool.getDefault();
	private static CtClass CT_CLASS_PARENT;
	static {
		try {
			pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
			CT_CLASS_PARENT = pool.getCtClass("com.ctriposs.baiji.rpc.common.types.BaseResponse");
			CtField extraIndexTagsField = new CtField(pool.get("java.util.Map"), "extraIndexTags", CT_CLASS_PARENT);
			extraIndexTagsField.setModifiers(Modifier.PRIVATE);
			CT_CLASS_PARENT.addField(extraIndexTagsField);

			CT_CLASS_PARENT.addMethod(CtNewMethod.setter("setExtraIndexTags", extraIndexTagsField));
			CT_CLASS_PARENT.addMethod(CtNewMethod.getter("getExtraIndexTags", extraIndexTagsField));

			//CT_CLASS_PARENT.writeFile();
			CT_CLASS_PARENT.toClass();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {SpringApplication.run(WebServiceSpringBootServletInitializer.class, args); }

}
