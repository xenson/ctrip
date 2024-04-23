package com.ctrip.car.osd.framework.common.utils;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.exception.CompressionException;
import com.ctrip.car.osd.framework.common.exception.CompressionExceptionCode;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {
    private static Logger logger = LoggerFactory.getLogger(GzipUtil.class);
    private static final String GZIP_ENCODE_UTF_8 = "UTF-8";


    public static byte[] compress(byte[] bytes) throws CompressionException, IOException {
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(bytes);
                gzip.close();
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e);
        }
    }

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str
     * @return
     */
    public static byte[] compress(String str) throws CompressionException {
        try {
            return compress(str, GZIP_ENCODE_UTF_8);
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e);
        }
    }

    /**
     * 字符串压缩为GZIP字节数组
     *
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] compress(String str, String encoding) throws CompressionException {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(str.getBytes(encoding));
                gzip.close();
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e);
        }
    }

    /**
     * 将字符串压缩成String
     *
     * @param str
     * @return
     */
    public static String compressToString(String str){
        try {
            return compressToString(str, GZIP_ENCODE_UTF_8);
        } catch (Exception e) {
            logger.error(new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 将字符串压缩成String
     *
     * @param str
     * @return
     */
    public static String compressToString(String str, String encoding) throws CompressionException {
        if (str == null || str.length() == 0) {
            return null;
        }

        try {
            return Base64.getEncoder().encodeToString(compress(str, encoding));
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e);
        }

    }

    /**
     * GZIP解压缩
     *
     * @param bytes
     * @return
     */
    public static byte[] uncompress(byte[] bytes) throws CompressionException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                 GZIPInputStream ungzip = new GZIPInputStream(in)) {
                byte[] buffer = new byte[256];
                int n;
                while ((n = ungzip.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_UNCOMPRESS_EXCEPTION, e);
        }
    }

    /**
     * 返回解压字符串
     *
     * @param inputString
     * @return
     */
    public static String uncompressToString(String inputString) {
        try {
            byte[] bytes = Base64.getDecoder().decode(inputString);
            return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
        } catch (Exception e) {
            logger.error(new CompressionException(CompressionExceptionCode.GZIP_COMPRESS_EXCEPTION, e));
        }
        return StringUtils.EMPTY;
    }

    /**
     * @param bytes
     * @return
     */
    public static String uncompressToString(byte[] bytes) throws CompressionException {
        try {
            return uncompressToString(bytes, GZIP_ENCODE_UTF_8);
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_UNCOMPRESS_EXCEPTION, e);
        }
    }

    /**
     * @param bytes
     * @param encoding
     * @return
     */
    public static String uncompressToString(byte[] bytes, String encoding) throws CompressionException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                 GZIPInputStream ungzip = new GZIPInputStream(in)) {
                byte[] buffer = new byte[256];
                int n;
                while ((n = ungzip.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                return out.toString(encoding);
            }
        } catch (Exception e) {
            throw new CompressionException(CompressionExceptionCode.GZIP_UNCOMPRESS_EXCEPTION, e);
        }
    }
}
