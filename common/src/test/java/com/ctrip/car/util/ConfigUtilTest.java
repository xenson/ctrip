package com.ctrip.car.util;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.ctrip.car.osd.framework.common.utils.ConfigUtil;


public class ConfigUtilTest extends TestCase {

    public void testFindAsResource(){
        URL url = ConfigUtil.findAsResource("appconfig.properties");
        System.out.println(url);

        url = ConfigUtil.findAsResource("0opslab-default.properties");
        System.out.println(url);

        url = ConfigUtil.findAsResource("ali.gif");
        System.out.println(url);
    }

    public void testResourcePath(){
        System.out.println(ConfigUtil.resourcePath(""));
        System.out.println(ConfigUtil.resourcePath("appconfig.properties"));

    }


}