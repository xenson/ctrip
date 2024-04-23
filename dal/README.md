## dal 集成

### 功能
	Repository
	自定义SQL查询

### Repository
	1、声明一个接口，继承DalRepository

### 自定义SQL查询
	1、在接口添加 @DAL注解 配置数据库
	2、添加 @SQL 注解在方法签名上
	3、方面签名上必须有 DalHints 字段
	4、可以在方法上添加 @DAL支持多个数据库

	```
		@Dal("MySqlSimpleDbShard")
		public interface PersonRepository extends DalRepository<Person> {
			@Sql("SELECT * FROM Person WHERE name LIKE ? and CityId in (?) ORDER BY name")
			public FreeEntityPojo findPersonName(DalHints hints , String name , List<Integer> cityIds);
		}

	```

### 其他特定配置
	dal.xml 			逻辑数据库，分表分库配置
	datasource.xml 	 	配置数据源，真实数据源

###  公司 dal
	1、基于DAL生成器，生成Entity、Dao和相关的配置 dal.xml , datasource.xml

####   组件

	```
		<dependency>
			<groupId>com.ctrip.car.och.framework</groupId>
			<artifactId>dal</artifactId>
		</dependency>
	```



