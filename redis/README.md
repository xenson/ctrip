## 与credis 集成

### 功能
	Redis

### Reids
	1、声明一个CacheProvider变量
	2、添加RedisProvider
	3、redis环境自动加载 

	```
		@Component
		public class CacheProviderWrapper {

			@RedisProvider("SentinelTest0")
			public CacheProvider provider;

			public CacheProvider getProvider() {
				return provider;
			}
		}
	```

####   组件

	```
		<dependency>
			<groupId>com.ctrip.car.och.framework</groupId>
			<artifactId>redis</artifactId>
		</dependency>
	```



