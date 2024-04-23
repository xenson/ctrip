package com.ctrip.car.osd.framework.common.exception;

public class CompressionException extends Exception {
    private CompressionExceptionCode code;

    public CompressionException(String message){
        this(CompressionExceptionCode.DEFAULT, message, null);
    }

    public CompressionException(Exception innerException) {
        this(CompressionExceptionCode.DEFAULT, null, innerException);
    }

    public CompressionException(CompressionExceptionCode code, String message) {
        this(code, message, null);
    }

    public CompressionException(CompressionExceptionCode code, Exception innerException) {
        this(code, null, innerException);
    }

    public CompressionException(CompressionExceptionCode code, String message, Exception innerException){
        super(getMessage(code, message, innerException),innerException);
        this.code = code;
    }

    public static String getMessage(CompressionExceptionCode code, String message, Exception exception){
        if(exception == null){
            return String.format("with code %s exception. %s",code.name(), message);
        }
        return String.format("with code %s exception. %s %n--->%s", code.name(), message, exception);
    }
}
