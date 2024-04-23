package com.ctrip.car.osd.framework.aspectj.proxy;

import com.ctrip.car.osd.framework.common.exception.BizException;
import com.ctrip.car.osd.framework.soa.client.ServiceClient;
import com.ctrip.car.osd.message.dto.MessageRequestType;
import com.ctrip.car.osd.message.dto.MessageResponseType;
import com.ctrip.car.osd.message.methodtype.OsdMessageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OsdMessageServiceProxy {

    @Autowired
    private OsdMessageServiceProxyAdapt adapt;

    public MessageResponseType sendMessage(MessageRequestType request) {
        try {
            return adapt.sendMessage(request/*, "http://10.5.162.91:8080"*/);
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    @ServiceClient(value = OsdMessageClient.class, socketTimeout = 100000)
    private interface OsdMessageServiceProxyAdapt {
        MessageResponseType sendMessage(MessageRequestType request) throws Exception;

        MessageResponseType sendMessage(MessageRequestType request, String baseUrl) throws Exception;
    }
}

