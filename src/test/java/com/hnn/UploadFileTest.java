package com.hnn;

import org.junit.jupiter.api.Test;

public class UploadFileTest {

    @Test
    public void test01(){
        String fileName="erererer.jpg";
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
