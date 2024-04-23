package com.ctrip.car.osd.framework.common.utils.compress;

import com.ctrip.car.osd.framework.common.exception.CompressionException;
import com.ctrip.car.osd.framework.common.exception.CompressionExceptionCode;
import com.ctrip.flight.intl.common.zstd.ZstdInputStreamEx;
import com.ctrip.flight.intl.common.zstd.ZstdOutputStreamEx;
import com.github.luben.zstd.Zstd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ZSTD算法压缩实现
 */
public class ZstdCompressor extends AbstractCompressor {

    public static final ZstdCompressor INSTANCE = new ZstdCompressor();

    @Override
    protected OutputStream getOutputStream(OutputStream os) throws IOException {
        return new ZstdOutputStreamEx(os);
    }

    @Override
    protected InputStream getInputStream(InputStream is) throws IOException {
        return new ZstdInputStreamEx(is);
    }

    @Override
    protected CompressionExceptionCode compressExceptionCode() {
        return CompressionExceptionCode.ZSTD_COMPRESS_EXCEPTION;
    }

    @Override
    protected CompressionExceptionCode decompressExceptionCode() {
        return CompressionExceptionCode.ZSTD_UNCOMPRESS_EXCEPTION;
    }

}
