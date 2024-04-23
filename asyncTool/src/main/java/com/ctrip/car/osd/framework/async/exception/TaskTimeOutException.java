package com.ctrip.car.osd.framework.async.exception;

/**
 * @author xh.gao
 */
public class TaskTimeOutException extends RuntimeException {

    private String taskName;

    public TaskTimeOutException(String taskName) {
        this.taskName = taskName;
    }

    public TaskTimeOutException(String message, String taskName) {
        super(message);
        this.taskName = taskName;
    }

    public TaskTimeOutException(String message, Throwable cause, String taskName) {
        super(message, cause);
        this.taskName = taskName;
    }

    public TaskTimeOutException(Throwable cause, String taskName) {
        super(cause);
        this.taskName = taskName;
    }

    public TaskTimeOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String taskName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
}
