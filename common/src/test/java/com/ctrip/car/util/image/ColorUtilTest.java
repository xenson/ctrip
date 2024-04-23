package com.ctrip.car.util.image;

import junit.framework.TestCase;

import java.awt.*;

import com.ctrip.car.osd.framework.common.utils.image.ColorUtil;

/**
 * 测试
 */
public class ColorUtilTest extends TestCase {

    public void testString2Color() throws Exception {
        Color color = ColorUtil.String2Color("#FD6E10");
        System.out.println(color);
    }

    public void testColor2String() throws Exception {
        Color color = new Color(255,255,255);
        System.out.println(ColorUtil.Color2String(color));
    }
}