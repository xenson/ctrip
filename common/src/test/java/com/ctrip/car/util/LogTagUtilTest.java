package com.ctrip.car.util;


import com.ctrip.car.osd.framework.common.utils.LogTagUtil;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import org.junit.Test;

import java.util.Map;

public class LogTagUtilTest {
    @Test
    public void testbuildExtraIndexTags(){
        new LogTagUtil();
        Map<String, String> stringStringMap = LogTagUtil.buildExtraIndexTags(new BaseResponse());
        stringStringMap.put("aabbcc","ddeeff");
    }
}