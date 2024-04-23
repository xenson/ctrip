package com.ctrip.car.util.encrypt;

import com.ctrip.car.osd.framework.common.utils.encrypt.SecureHA;

import junit.framework.TestCase;

public class SecureHATest extends TestCase {
    public void testGetResult() throws Exception {

        String result = SecureHA.getResult("简单加密");
        System.out.println(result);


    }
}

