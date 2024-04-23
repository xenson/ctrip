package com.ctrip.car.osd.framework.common.utils.compress;

import com.ctrip.car.osd.framework.common.exception.CompressionExceptionCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP算法压缩实现
 */
public class GzipCompressor extends AbstractCompressor {

    public static final GzipCompressor INSTANCE = new GzipCompressor();

    @Override
    protected OutputStream getOutputStream(OutputStream os) throws IOException {
        return new GZIPOutputStream(os);
    }

    @Override
    protected InputStream getInputStream(InputStream is) throws IOException {
        return new GZIPInputStream(is);
    }

    @Override
    protected CompressionExceptionCode compressExceptionCode() {
        return CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION;
    }

    @Override
    protected CompressionExceptionCode decompressExceptionCode() {
        return CompressionExceptionCode.GZIP_UNCOMPRESS_EXCEPTION;
    }
}
