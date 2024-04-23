package com.ctrip.car.osd.framework.aspectj.proxy;


import com.ctrip.car.osd.common.methodtype.OsdcommonserviceClient;
import com.ctrip.car.osd.common.methodtype.TrackPageRequestType;
import com.ctrip.car.osd.common.methodtype.TrackPageResponseType;
import com.ctrip.car.osd.framework.soa.client.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OsdCommonServiceProxy {
     @Autowired
     private OsdCommonServiceProxyAdapt adapt;

    public TrackPageResponseType trackPage(TrackPageRequestType request) throws Exception {
        return adapt.trackPage(request);
    }

    @ServiceClient(value = OsdcommonserviceClient.class, socketTimeout = 100000)
    private interface  OsdCommonServiceProxyAdapt {
        TrackPageResponseType trackPage(TrackPageRequestType request) throws Exception;
    }
}

