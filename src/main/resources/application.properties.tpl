# spring boot settings

logging.level.root=ERROR

mybatis.mapper-locations=classpath*:mybatis/**.xml

ctrip.web.fx-config-service-url={$web.fxConfigServiceUrl}
ctrip.web.sub-env={$web.subEnv}
ctrip.web.app-id=100006077

# settings loggings
ctrip.logging.server = {$logging.server}
ctrip.logging.port  = {$logging.port}

#titan settings 
ctrip.datasource.allInOneKey = {$db.allInOneKey}
ctrip.datasource.svcUrl = {$db.svcUrl}