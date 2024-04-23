package com.ctrip.car.osd.framework.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author by xiayx on 2019/8/12 20:21
 */
public class HashTwoUtil {
    public static String getHash(String data) {
        String md5Hex = DigestUtils.md5Hex(data);
        String sha1Hex = DigestUtils.sha1Hex(data);
        return md5Hex + sha1Hex;
    }
}
