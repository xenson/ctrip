package com.ctrip.car.util;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.car.osd.framework.common.utils.ValidUtil;

public class validTest extends TestCase {

    public void testIsValid() throws Exception {
        assertEquals(false, ValidUtil.valid(""));
        assertEquals(true, ValidUtil.valid("111"));
        assertEquals(true, ValidUtil.valid("111", "2222"));
        assertEquals(false, ValidUtil.valid("111", ""));
    }

    public void testIsValid1() throws Exception {
        assertEquals(true, ValidUtil.valid(new String("11")));

    }

    public void testIsValid2() throws Exception {
        Map map = new HashMap();
        assertEquals(false, ValidUtil.valid(map));
        map.put("1", "1");
        assertEquals(true, ValidUtil.valid(map));
        Map map1 = new HashMap();
        assertEquals(false, ValidUtil.valid(map, map1));
        map1.put("2", "2");
        assertEquals(true, ValidUtil.valid(map, map1));
    }

    public void testIsDate(){
        assertEquals(true,ValidUtil.isDate("2016-03-16 00:07:02",
                "yyyy-MM-dd HH:mm:ss"));
    }

    public void testSwith(){
        System.out.println(ValidUtil.valid(new Object()));
        System.out.println(ValidUtil.valid(""));
    }

}