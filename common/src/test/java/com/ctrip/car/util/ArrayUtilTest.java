package com.ctrip.car.util;


import org.junit.Test;

import com.ctrip.car.osd.framework.common.utils.ArrayUtil;

public class ArrayUtilTest  {
    @Test
    public void testdoubleBitCount(){
        int size = 1000000;
        double[] arr = new double[size];
        for (int i = 0; i < size; i++) {
            arr[i] = ((double) 1)/(i+1);
        }
        ArrayUtil.doubleBitCount(arr);
    }
}