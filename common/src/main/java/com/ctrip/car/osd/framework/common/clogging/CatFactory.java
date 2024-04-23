package com.ctrip.car.osd.framework.common.clogging;

import com.ctrip.car.osd.framework.common.context.RequestContext;
import com.ctrip.car.osd.framework.common.context.RequestContextFactory;
import com.ctrip.car.osd.framework.common.utils.LogTagUtil;
import com.ctrip.car.osd.framework.common.utils.compress.CompressFactory;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.foundation.spi.provider.ServerProvider;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CatFactory {

	private static final String RESPONSE_NAME = "response";
	public static String ES_Scenario = "car-osd";

	public static CatTransaction createTransaction(String type, String name) {
		Transaction transaction = Cat.newTransaction(type, name);
		return new CatTransaction(transaction);
	}

	public static CatTransaction createForkedTransaction(String type, String name) {
		Transaction transaction = Cat.newForkedTransaction(type, name);
		return new CatTransaction(transaction);
	}

	public static void logError(String message, Throwable cause) {
		Cat.logError(message, cause);
	}

	public static void logError(Throwable cause) {
		Cat.logError(cause);
	}

	public static void logTags(LogContext logContext) {
		Map<String, String> indexTags = LogTagUtil.getIndexTags(logContext);
		Map<String, String> storeTags = buildStoreTags(logContext);

		indexTags.putAll(getCommonTags());
		if (MapUtils.isNotEmpty(logContext.getExtraTags())) {
			indexTags.putAll(logContext.getExtraTags());
		}
		Cat.logTags(ES_Scenario, indexTags, storeTags);
	}

	private static Map<String, String> buildStoreTags(LogContext logContext) {
		Map<String, String> storeTags = new HashMap<>(2);
		storeTags.put("request", logContext.getRequestBody());
		appentResponse(storeTags, logContext);
		return storeTags;
	}

    public static void appentResponse(Map<String, String> storeTags, LogContext logContext) {
        String compressResponse = logContext.getResponseBody();
        if (StringUtils.isNotBlank(compressResponse)) {
            int logLength = compressResponse.length();
            int compressThreshold = CompressFactory.MAX_SIZE;

            if (logLength <= compressThreshold) {
                storeTags.put(RESPONSE_NAME, compressResponse);
            } else {
                int count = logLength / compressThreshold + 1;
                for (int i = 1; i <= count; i++) {
                    if (i == count) {
                        storeTags.put(RESPONSE_NAME + i, compressResponse.substring(compressThreshold * (i - 1)));
                    } else {
                        storeTags.put(RESPONSE_NAME + i, compressResponse.substring(compressThreshold * (i - 1), compressThreshold * i));
                    }
                }
            }
        }
    }

	public static void logTags(Map<String, String> indexTags,String tagName, String msg) {
		Map<String, String> storeTags = new HashMap<>();
		storeTags.put(tagName, msg);
		Cat.logTags(ES_Scenario, indexTags, storeTags);
	}

	public static Map<String, String> getCommonTags() {
		Map<String, String> tags = new HashMap<>();
		RequestContext requestContext = RequestContextFactory.INSTANCE.getCurrent();
		if (Objects.isNull(requestContext)) {
			return tags;
		}

		Map<String, String> requestItems = requestContext.getRequestItems();
		if (MapUtils.isNotEmpty(requestItems)) {
			tags.putAll(requestItems);
		}

		Set<ThreadLocal<Map<String, String>>> customizeTagHolder = requestContext.getCustomizeTagHolder();
		if (CollectionUtils.isNotEmpty(customizeTagHolder)) {
			customizeTagHolder.stream().map(ThreadLocal::get).filter(MapUtils::isNotEmpty).forEach(tags::putAll);
		}

		if (!tags.containsKey("uid")) {
			tags.put("uid", requestContext.getUid());
		}
		tags.put("subEnv", Objects.isNull(Foundation.server().getSubEnv()) ?
				Optional.ofNullable(Foundation.server()).map(ServerProvider::getEnv).map(Env::getName).orElse(null) : Foundation.server().getSubEnv());
		return tags;
	}
}
