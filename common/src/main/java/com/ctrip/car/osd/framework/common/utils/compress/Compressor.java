package com.ctrip.car.osd.framework.common.utils.compress;

import com.ctrip.car.osd.framework.common.exception.CompressionException;

public interface Compressor {

    /**
     * 压缩成字节数组
     */
    byte[] compress(byte[] bytes) throws CompressionException;

    /**
     * 解压成字节数组
     */
    byte[] decompress(byte[] bytes) throws CompressionException;

    /**
     * 压缩成字符串
     */
    String compress2String(String str);

    /**
     * 解压成字符串
     */
    String decompress2String(String str);

}
