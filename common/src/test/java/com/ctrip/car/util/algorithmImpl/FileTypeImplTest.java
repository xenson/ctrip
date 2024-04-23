package com.ctrip.car.util.algorithmImpl;

import junit.framework.TestCase;
import org.junit.Test;

import com.ctrip.car.util.TestUtil;
import com.ctrip.car.osd.framework.common.utils.algorithmImpl.FileTypeImpl;

import java.io.File;

public class FileTypeImplTest extends TestCase {

    @Test
    public void testFileType() {
        assertEquals("gif", FileTypeImpl.getFileType(new File(TestUtil.path + "ali.gif")));
        assertEquals("png", FileTypeImpl.getFileType(new File(TestUtil.path + "tgepng")));
    }

}