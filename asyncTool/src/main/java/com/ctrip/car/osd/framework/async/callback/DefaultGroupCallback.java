package com.ctrip.car.osd.framework.async.callback;

import com.ctrip.car.osd.framework.async.wrapper.WorkerWrapper;

import java.util.List;

public class DefaultGroupCallback implements IGroupCallback {

    @Override
    public void success(List<WorkerWrapper> workerWrappers) {
    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {
    }
}
