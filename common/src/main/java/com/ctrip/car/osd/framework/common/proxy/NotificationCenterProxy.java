package com.ctrip.car.osd.framework.common.proxy;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.notificationcenter.api.CarOsdNotificationCenterClient;
import com.ctrip.car.osd.notificationcenter.dto.TrackerRequestType;
import com.ctrip.car.osd.notificationcenter.sdk.qmq.TrackProducers;

/**
 * Created by xiayx on 2021/1/13.
 */
public class NotificationCenterProxy {
    private static Logger LOGGER = LoggerFactory.getLogger(NotificationCenterProxy.class);
    private static CarOsdNotificationCenterClient notificationCenterClient = CarOsdNotificationCenterClient.getInstance();

    public static void trackerAsync(TrackerRequestType request) {
        try {
            notificationCenterClient.trackerAsync(request);
        } catch (Exception e) {
            LOGGER.warn("trackerAsync", e);
        }
    }

    /**
     * 服务端埋点发QMQ消息
     *
     * @param request
     */
    public static void trackerQmq(TrackerRequestType request) {
        try {
            TrackProducers.sendMessage(request.getExtendInfo());
        } catch (Exception e) {
            LOGGER.warn("trackerQmq", e);
        }
    }
}
