## 与soa2.0 server 集成

### 功能
	Executor
	Interceptor
	Validator
	多服务

### Executor
	1、继承ServiceExecutor 或者直接实现 Executor 接口
	2、在接口上添加 @Component 注入Spring

	```
		@Component
		public class CountryExecutor extends ServiceExecutor<CountryRequestType, CountryResponseType> {
			public CountryResponseType execute(CountryRequestType arg0) throws Exception {
				return new CountryResponseType();
			}
		}

	```
### Interceptor
	Interceptor 			前置拦截器
	AfterInterceptor 		后置拦截器
	ExceptionInterceptor	异常拦截器

### Validator
	数据校验接口与 SOA2.0 集成

	```
	@Component
	public class CountryExecutor extends ServiceExecutor<CountryRequestType, CountryResponseType> implements Validator<CountryRequestType> {
		@Override
		public CountryResponseType execute(CountryRequestType arg0) throws Exception {
			return new CountryResponseType();
		}
		@Override
		public void validate(AbstractValidator<CountryRequestType> validator) {
			//数据验证
		}
	}
	```

###  多服务
	1、基于多个契约生成服务端代码
	2、在 application.properties 配置路径 ctrip.soa.path.xxx=服务名


###  soa2.0 server
	1、基于契约生成服务端端代码

####   组件

	```
		<dependency>
			<groupId>com.ctrip.car.och.framework</groupId>
			<artifactId>soa-server</artifactId>
		</dependency>
	```



