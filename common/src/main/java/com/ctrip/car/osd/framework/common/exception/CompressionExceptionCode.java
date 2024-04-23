package com.ctrip.car.osd.framework.common.exception;

public enum CompressionExceptionCode {
    UNKNOWN(0),
    GZIP_COMPRESS_EXCEPTION(1),
    SNAPPY_COMPRESS_EXCEPTION(2),
    GZIP_UNCOMPRESS_EXCEPTION(3),
    SNAPPY_UNCOMPRESS_EXCEPTION(4),
    DEFAULT(5),
    ZSTD_COMPRESS_EXCEPTION(6),
    ZSTD_UNCOMPRESS_EXCEPTION(7),
    ;

    public final int id;

    CompressionExceptionCode(int id) {
        this.id = id;
    }

    public static CompressionExceptionCode getErrorCode(int code){
        CompressionExceptionCode[] valueArray = values();
        for (CompressionExceptionCode value : valueArray) {
            if (value.id == code) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static String getErrorMessage(int code){
        return getErrorCode(code).name();
    }
}
