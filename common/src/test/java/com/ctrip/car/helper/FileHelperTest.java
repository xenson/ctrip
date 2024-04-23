package com.ctrip.car.helper;

import com.ctrip.car.osd.framework.common.functions.ObjectHandler;
import com.ctrip.car.osd.framework.common.functions.ObjectProcess;
import com.ctrip.car.osd.framework.common.helper.CollectionHelper;
import com.ctrip.car.osd.framework.common.helper.FileHelper;
import com.ctrip.car.util.TestUtil;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by monsoon on 18/06/2017.
 */
public class FileHelperTest {
    private  String file =  TestUtil.path+ "EnglishWrite.txt";

    /**
     * 逐行打印
     */
    @Test
    public void handlerWithLine(){
        FileHelper.handlerWithLine(new File(file), "UTF-8", new ObjectHandler<String>() {
            @Override
            public void handler(String s) {
                System.out.println(s);
            }
        }) ;
    }

    /**
     * 逐行读取并获取长度大于15的行
     */
    @Test
    public void processWithLine(){
        List<String> lines = new ArrayList<String>();
        FileHelper.processWithLine(new File(file), "UTF-8", lines, new ObjectProcess<String,String>() {
            @Override
            public String process(String o) {
                if(o != null && o.length() > 15){
                    return o;
                }
                return null;
            }
        });

        CollectionHelper.handler(lines, new ObjectHandler<String>() {
            @Override
            public void handler(String s) {
                System.out.println(s);
            }
        });

    }
}