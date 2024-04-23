## framework 

### framework 组件
	common
	soa-server					对SOA2.0 服务端集成
	soa-client	 			对SOA2.0 客户端集成
	dal
	redis
	datasource

### 说明
	以maven构建的系统的多模块系统

### 公共约定
	1、META-INF/app.properties      配置appId必须
	2、application.properties  		配置spring等
	3、com.ctrip 为根节点扫描类 

###  安装编译
	mvn install -Dmaven.test.skip=true

### 组件链接 
	[common](common)
	[soa-server](soa-server)				
	[soa-client](soa-client)	 		
	[dal](dal)
	[redis](redis)
	[apollo](apollo)
	hermes	   

