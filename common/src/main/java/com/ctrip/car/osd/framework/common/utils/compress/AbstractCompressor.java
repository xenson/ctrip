package com.ctrip.car.osd.framework.common.utils.compress;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.exception.CompressionException;
import com.ctrip.car.osd.framework.common.exception.CompressionExceptionCode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 压缩工具抽象类
 * 需要实现以下方法：
 *   getOutputStream
 *   getInputStream
 *   compressExceptionCode
 *   uncompressExceptionCode
 */
public abstract class AbstractCompressor implements Compressor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCompressor.class);

    @Override
    public byte[] compress(byte[] bytes) throws CompressionException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStream os = getOutputStream(out)) {
            os.write(bytes);
            os.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressionException(compressExceptionCode(), e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) throws CompressionException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             InputStream is = getInputStream(in)) {
            byte[] buffer = new byte[256];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new CompressionException(decompressExceptionCode(), e);
        }
    }

    @Override
    public String compress2String(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        try {
            byte[] bytes = string2Bytes(str);
            return Base64.getEncoder().encodeToString(compress(bytes));
        } catch (CompressionException | UnsupportedEncodingException e) {
            LOGGER.warn(e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String decompress2String(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }

        try {
            byte[] bytes = Base64.getDecoder().decode(str);
            return StringUtils.toEncodedString(decompress(bytes), charset());
        } catch (CompressionException e) {
            return StringUtils.EMPTY;
        }
    }

    private byte[] string2Bytes(String str) throws UnsupportedEncodingException {
        return str.getBytes(charset().name());
    }

    protected Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * 具体的输出流，由实现类提供
     */
    protected abstract OutputStream getOutputStream(OutputStream os) throws IOException;

    /**
     * 具体的输入流，由实现类提供
     */
    protected abstract InputStream getInputStream(InputStream is) throws IOException;

    protected abstract CompressionExceptionCode compressExceptionCode();

    protected abstract CompressionExceptionCode decompressExceptionCode();

}
