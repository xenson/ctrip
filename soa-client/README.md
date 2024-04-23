## soa2. client 集成

### 功能
	ServiceClient

### ServiceClient
	1、声明一个接口，将要使用的服务添加到接口签名上
	2、在接口上添加 @ServiceClient 注解 （需指定具体的ServiceCLient） 
	3、可以在方法级别上添加@ServicveClient
	4、添加参数 @ServiceClientConfigOptions


	```
		@ServiceClient(CountryServiceClient.class)
		public interface CountryServiceProxy {
			public CountryResponseType getCountries(CountryRequestType request , String url) throws Exception;	//指定路径调用
			public CountryResponseType getCountries(CountryRequestType request) throws Exception;
			public CountryResponseType getCountries(CountryRequestType request, Func<CountryResponseType> fallbackProvider) throws Exception;
			public ListenableFuture<CountryResponseType> getCountriesAsync(CountryRequestType request) throws Exception;
		}

	```

###  公司SOA client 
	1、基于契约生成客户端代码
	2、配置 soa.properties， 需要指定子环境、uat和prod不需要指定
		```
			#特定环境
			soa.client.registry.sub-env=fat485
		```

####   组件

	```
		<dependency>
			<groupId>com.ctrip.car.och.framework</groupId>
			<artifactId>soa-client</artifactId>
		</dependency>
	```



