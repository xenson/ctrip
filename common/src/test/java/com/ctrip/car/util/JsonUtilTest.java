package com.ctrip.car.util;

import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class JsonUtilTest {
    @Test
    public void testGetSetInt(){
        String testStr = "30926,32751,32752";
        Set<Integer> setInt = JsonUtil.getSetInt(testStr);
        Assert.assertEquals(3,setInt.size());
    }

    @Test
    public void testGetSetStr(){
        String testStr = "app,Online";
        Set<String> set = JsonUtil.getSetStr(testStr);
        Assert.assertEquals(2,set.size());
    }

    @Test
    public void testGetObjList(){
        String testStr = "[{\"vendorId\":13015,\"beginDate\":\"2019-02-05 00:00:00\",\"endDate\":\"2019-02-07 23:59:59\"}]";
        List<VendorFilterCO> objList = JsonUtil.getObjList(testStr, VendorFilterCO.class);
        Assert.assertEquals(1,objList.size());
    }

    @Test
    public void testGetObj(){
        String testStr = "{\"vendorId\":13015,\"beginDate\":\"2019-02-05 00:00:00\",\"endDate\":\"2019-02-07 23:59:59\"}";
        VendorFilterCO obj = JsonUtil.getObj(testStr, VendorFilterCO.class);
        Assert.assertNotNull(obj);
    }

    @Test
    public void testSerializeCalendar(){
        Calendar calendar = Calendar.getInstance();
        String s = JsonUtil.toJson(calendar);
        System.out.println(s);
    }

    public static class VendorFilterCO {

        private int vendorId;

        private Calendar beginDate;

        private Calendar endDate;

        public int getVendorId() {
            return vendorId;
        }

        public void setVendorId(int vendorId) {
            this.vendorId = vendorId;
        }

        public Calendar getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(Calendar beginDate) {
            this.beginDate = beginDate;
        }

        public Calendar getEndDate() {
            return endDate;
        }

        public void setEndDate(Calendar endDate) {
            this.endDate = endDate;
        }
    }
}
