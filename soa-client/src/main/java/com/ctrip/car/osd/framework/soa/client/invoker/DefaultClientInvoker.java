package com.ctrip.car.osd.framework.soa.client.invoker;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.common.config.ContentFormatConfig;
import com.ctrip.car.osd.framework.common.utils.json.CustomContentFormatter;
import com.ctrip.flight.intl.common.callformat.ZstdGoogleProtobuf2ContentFormatter;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctriposs.baiji.rpc.client.ServiceClientBase;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

public class DefaultClientInvoker extends BaseService implements ClientInvoker {

	private final ServiceClientBase<? extends ServiceClientBase<?>> serviceClient;
	private final ClientConfig clientConfig;
	private final ContentFormatConfig formatConfig;

	public DefaultClientInvoker(ServiceClientBase<?> client, ClientConfig clientConfig, ContentFormatConfig formatConfig) {
		super();
		this.serviceClient = client;
		this.clientConfig = clientConfig;
		this.formatConfig = formatConfig;
		this.initClient(this.serviceClient, this.clientConfig, formatConfig);
	}

	private ServiceClientBase<?> getServiceClient(String url) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		ServiceClientBase<?> client = (ServiceClientBase<?>) MethodUtils
				.invokeStaticMethod(this.serviceClient.getClass(), "getInstance", new Object[] { url });

		if (clientConfig != null) {
			initClient(client, clientConfig, formatConfig);
		}
		return client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ctrip.car.common.client.proxy.ServiceClientInvoke#invokeDirect(java.
	 * lang.Class, java.lang.String, TReq, java.lang.Object, java.lang.Class)
	 */
	public <TReq, TResp> TResp invokeDirect(String operation, TReq request, String url, Class<TResp> responseClass)
			throws Exception {
		return getServiceClient(url).invoke(operation, request, responseClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ctrip.car.common.client.proxy.ServiceClientInvoke#invoke(java.lang.
	 * Class, java.lang.String, TReq, java.lang.Class)
	 */
	public <TReq, TResp> TResp invoke(String operation, TReq request, Class<TResp> responseClass) throws Exception {
		return serviceClient.invoke(operation, request, responseClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ctrip.car.common.client.proxy.ServiceClientInvoke#invokeFallback(java
	 * .lang.Class, java.lang.String, TReq, java.lang.Object, java.lang.Class)
	 */
	public <TReq, TResp> TResp invokeFallback(String operation, TReq request, Object fallbackProvider,
			Class<TResp> responseClass) throws Exception {
		Func<TResp> provider = (Func<TResp>) fallbackProvider;
		return serviceClient.invoke(operation, request, provider, responseClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ctrip.car.common.client.proxy.ServiceClientInvoke#invokeAsync(java.
	 * lang.Class, java.lang.String, TReq, java.lang.Class)
	 */
	public <TReq, TResp> ListenableFuture<TResp> invokeAsync(String operation, TReq request, Class<TResp> respClass)
			throws Exception {
		return serviceClient.invokeAsync(operation, request, respClass);
	}

	private void initClient(ServiceClientBase<? extends ServiceClientBase<?>> serviceClient,
			ClientConfig clientConfig, ContentFormatConfig formatConfig) {

		ServiceClientBase.registerContentFormatter(new CustomContentFormatter(formatConfig), true);
		ServiceClientBase.registerContentFormatter(ZstdGoogleProtobuf2ContentFormatter.INSTANCE);

		if (clientConfig.getRequestTimeout() > 0) {
			serviceClient.setRequestTimeout(clientConfig.getRequestTimeout());
		}
		if (clientConfig.getFormat() != null && !clientConfig.getFormat().isEmpty()) {
			serviceClient.setFormat(clientConfig.getFormat());
		}
		if (clientConfig.getConnectTimeout() > 0) {
			serviceClient.setConnectTimeout(clientConfig.getConnectTimeout());
		}
		if (clientConfig.getMaxConnectionPerRoute() > 0) {
			serviceClient.setMaxConnectionPerRoute(clientConfig.getMaxConnectionPerRoute());
		}
		if (clientConfig.getIdleTime() > 0) {
			serviceClient.setIdleTime(clientConfig.getIdleTime());
		}
		if (clientConfig.getSocketTimeout() > 0) {
			serviceClient.setSocketTimeout(clientConfig.getSocketTimeout());
		}
		if (clientConfig.isIgnoreError()) {
			serviceClient.setLogCServiceExceptionAsError(false);
			serviceClient.setLogWebExceptionAsError(false);
		}
	}

	@Override
	public void setRequestConfig(String operation, ClientConfig clientConfig) {
		if (clientConfig.getRequestTimeout() > 0) {
			serviceClient.setRequestTimeout(operation, clientConfig.getRequestTimeout());
		}
		if (clientConfig.getSocketTimeout() > 0) {
			serviceClient.setSocketTimeout(operation, clientConfig.getSocketTimeout());
		}
		if (clientConfig.getConnectTimeout() > 0) {
			serviceClient.setConnectTimeout(operation, clientConfig.getConnectTimeout());
		}
	}

}
