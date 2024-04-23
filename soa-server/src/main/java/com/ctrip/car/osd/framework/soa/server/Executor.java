package com.ctrip.car.osd.framework.soa.server;

public interface Executor<Req, Resp> {
	
	//before
	Resp execute(Req req) throws Exception;
	//after
	//exception	
}
