package com.ctrip.car.osd.framework.soa.server;

import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.common.utils.json.CustomContentFormatter;
import com.ctrip.flight.intl.common.callformat.ZstdGoogleProtobuf2ContentFormatter;
import com.ctrip.soa.caravan.common.value.StringValues;
import com.ctriposs.baiji.rpc.common.formatter.ContentFormatter;
import com.ctriposs.baiji.rpc.server.ContentFormatConfig;
import com.ctriposs.baiji.rpc.server.CustomServerConfigurer;
import com.ctriposs.baiji.rpc.server.HostConfig;
import com.ctriposs.baiji.rpc.server.plugin.ratelimiter.data.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomServerConfigurerImpl implements CustomServerConfigurer {

    @Autowired
    private SDFrameworkQConfig sdFrameworkQConfig;

    @Override
    public void config(HostConfig hostConfig) {

        // 设置自定义序列化器
        configContentFormat(hostConfig);

        // 设置SOA限流告警监听器
        configRateLimiterListener(hostConfig);

    }

    private void configRateLimiterListener(HostConfig hostConfig) {
        RateLimitDataChangeListener dataChangeListener = new DashboardRateLimitDataChangeListener(sdFrameworkQConfig.getPreWarningPercentage());

        RateLimitDataChangeNotifier realTimeDataChangeNotifier = new RealTimeRateLimitDataChangeNotifier(dataChangeListener);

        hostConfig.getRateLimiterNotifierConfig().setAllPluginNotifier(realTimeDataChangeNotifier);
    }

    @SuppressWarnings("unchecked")
    private void configContentFormat(HostConfig hostConfig) {
        ContentFormatConfig contentFormatConfig = hostConfig.contentFormatConfig;
        ContentFormatter formatter = new CustomContentFormatter(sdFrameworkQConfig.getContentFormatConfig());
        try {
            Map<String, ContentFormatter> formatters = (Map<String, ContentFormatter>) FieldUtils
                    .readDeclaredField(contentFormatConfig, "_registeredFormatters", true);
            Map<String, String> contentTypes = (Map<String, String>) FieldUtils.readDeclaredField(contentFormatConfig,
                    "_extContentTypes", true);
            String extension = StringValues.toLowerCase(formatter.getExtension());
            formatters.put(extension, formatter);
            contentFormatConfig.getRegisteredFormatters().add(formatter);
            contentTypes.put(extension, formatter.getContentType());

            if (extension.equalsIgnoreCase(ContentFormatConfig.JSON_EXTENSION)) {
                FieldUtils.writeDeclaredField(contentFormatConfig, "_defaultFormatter", formatter, true);
            }
            if (extension.equalsIgnoreCase(ContentFormatConfig.JSON_EXTENSION)) {
                FieldUtils.writeDeclaredField(contentFormatConfig, "_defaultJsonFormatter", formatter, true);
            }
        } catch (Exception e) {
            contentFormatConfig.registerFormatter(formatter, true);
        }

        contentFormatConfig.registerFormatter(ZstdGoogleProtobuf2ContentFormatter.INSTANCE);
    }
}
