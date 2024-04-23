package com.ctrip.car.osd.framework.common.config;

public interface ConfigKeys {

    String CONFIG_FILE = "sd-framework.properties";

    // 日志压缩功能配置key
    String COMPRESS_CONFIG_KEY = "ctrip.soa.service.compress.method";

    // 客户端日志压缩功能配置key
    String CLIENT_COMPRESS_CONFIG_KEY = "ctrip.soa.service.client.compress.method";

    // 日志关闭功能配置key 0：不关闭日志 1：只关响应 2：请求响应都关 3：都关-无痕模式
    String DISABLE_LOG_CONFIG_KEY = "ctrip.soa.service.log.disable.method";

    // 客户端日志关闭功能配置key 0：不关闭日志 1：只关响应
    String CLIENT_DISABLE_LOG_CONFIG_KEY = "ctrip.soa.service.client.log.disable.method";

    // 日志压缩算法key 0：gzip 1：zstd
    String COMPRESS_ALGORITHM_KEY = "ctrip.soa.service.compress.algorithm";

    // 是否启用BigDecimal和BigInteger校验
    String BIG_NUMBER_LENGTH_CHECK_ENABLE_KEY = "ctrip.soa.json.big.number.length.check.enable";

    // BigDecimal和BigInteger总长度校验
    String BIG_NUMBER_LENGTH_KEY = "ctrip.soa.json.big.number.length";

    // BigDecimal实数位校验
    String BIG_DECIMAL_SIGNIFICAND_MAX_LENGTH_KEY = "ctrip.soa.json.big.decimal.significand.max.length";

    // BigDecimal指数位校验
    String BIG_DECIMAL_EXPONENT_MAX_LENGTH_KEY = "ctrip.soa.json.big.decimal.exponent.max.length";

    // 过滤掉指定interceptor
    String EXCLUDE_INTERCEPTORS_KEY = "ctrip.soa.exclude.interceptors";

    // 是否记录原始请求 0:否  1：是  默认为是
    String LOG_ORIGINAL_REQUEST_KEY = "ctrip.soa.log.original.request";

    // SOA限流打点阈值
    String PRE_WARNING_PERCENTAGE = "ctrip.soa.rate.limit.warning.percentage";

    // redis cluster mapping 配置
    String REDIS_CLUSTER_NAME_MAP = "redis.cluster.name.map";

    // 服务端埋点是否只写QMQ
    String TRACK_QMQ_ONLY_KEY = "ctrip.soa.service.track.qmq";
}
