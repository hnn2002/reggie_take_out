package com.hnn.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import java.util.*;
import com.aliyuncs.dysmsapi.model.v20170525.*;

public class SendSms {

    /**
     * 发送短信*
     * @param signName
     * @param templateCode
     * @param phoneNumbers
     * @param templateParam
     */
    public static void sendMessage(String signName,String templateCode,String phoneNumbers,String templateParam) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-wuhan", "LTAI5tHpmEvE3vyukaTzFDYW", "Q2Ul65qjTvjGzTWW8td8R1IjLABpel");
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);//接收短信的手机号码
        request.setSignName(signName);//短信签名名称
        request.setTemplateCode(templateCode);//短信模板CODE
        request.setTemplateParam("{\"code\":\""   +templateParam+   "\"}");//短信模板变量对应的实际值

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println("短信发送成功");
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }
}