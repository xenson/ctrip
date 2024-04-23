package com.ctrip.car.osd.framework.soa.client.invoker;

import com.google.common.util.concurrent.ListenableFuture;

public interface ClientInvoker {

	<TReq, TResp> TResp invokeDirect(String operation, TReq request, String url, Class<TResp> responseClass)
			throws Exception;

	<TReq, TResp> TResp invoke(String operation, TReq request, Class<TResp> responseClass) throws Exception;

	<TReq, TResp> TResp invokeFallback(String operation, TReq request, Object fallbackProvider,
			Class<TResp> responseClass) throws Exception;

	<TReq, TResp> ListenableFuture<TResp> invokeAsync(String operation, TReq request, Class<TResp> respClass)
			throws Exception;
	
	void setRequestConfig(String operation, ClientConfig clientConfig );

}