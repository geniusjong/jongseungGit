package com.maeil.rtm;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import dsic.client.DsicPayloadClient;
import dsic.message.BaseMessage;
import dsic.message.ext.JcoJsonMessage;
import dsic.message.ext.ServiceMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class S4HanaConnection {
	private static final Logger log = LoggerFactory.getLogger(S4HanaConnection.class);
    public static void main(String[] args) {
        
    	//S4HANA CVO SAP 접속
    	String url = "10.227.1.144"; // 호스트와 포트
        int port = 9001;
        //String user = "RFSMLCVOS";
        //String password = "Maeil0123!";
        
        //현재 CVO SAP 접속
    	//String url = "10.0.10.7"; // 호스트와 포트
        //int port = 9001;
        //String user = "RFSMLCVOS";
        //String password = "Maeil0123!";
        
        DsicPayloadClient sapGwClient = new DsicPayloadClient(url, port);	//SAP Client
        
        try {
            // SAP RFC FunctionName 지정
            JcoJsonMessage reqMsg = new JcoJsonMessage();
            reqMsg.setServiceName("ZCVO_SMT_CRATE_QTY");

            // 로그에 요청 메시지 출력
            System.out.println("Request Message: " + reqMsg.getServiceName());
            // SAP 서버로 메시지 전송
            BaseMessage resMsg = null;
            String serviceName = reqMsg.getServiceName();

            Charset charset = Charset.forName("euc-kr");
            System.out.println("SAP 서버로 메시지 전송 try전: ");
            try {
                Gson gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().create();
                System.out.println("gson ");
                String strJson = gson.toJson(reqMsg);
                System.out.println("strJson ");
                byte[] recv = sapGwClient.sendReceive(strJson.getBytes(charset));
                System.out.println("recv ");
                if (recv == null) {
                    throw new Exception("Result is null.");
                }
                // 응답 메시지를 로그에 출력
                resMsg = gson.fromJson(new String(recv, charset), ServiceMessage.class);
                System.out.println("Request Message: " + reqMsg.getServiceName());

            } catch (Exception e) {
            	System.out.println("SAP Communication Error: " + e.getLocalizedMessage());
            }

        } catch (Exception e) {
        	System.out.println("Error during SAP communication: " + e.getMessage());
        }

    }
}
