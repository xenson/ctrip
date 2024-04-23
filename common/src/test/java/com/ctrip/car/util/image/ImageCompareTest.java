package com.ctrip.car.util.image;

import junit.framework.TestCase;

import com.ctrip.car.util.TestUtil;
import com.ctrip.car.osd.framework.common.utils.image.ImageCompare;

import java.io.File;


public class ImageCompareTest extends TestCase {

    public void testCompareImage() throws Exception {
        String path = TestUtil.path+"/image";
        float v = ImageCompare.compareImage(new File(path + "/1.jpg"), new File(path + "/1.jpg"));
        System.out.println(v);
    }
}