package com.maeil.rtm;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import dsic.client.DsicPayloadClient;
import dsic.message.BaseMessage;
import dsic.message.ext.DataSet;
import dsic.message.ext.JcoJsonMessage;
import dsic.message.ext.ServiceMessage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cvo.web.common.data.Box;
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

        // SAP 클라이언트 생성
        DsicPayloadClient sapGwClient = new DsicPayloadClient(url, port);	//SAP Client
        
        // SAP에 전송할 데이터 준비
        String name = "ZSDS4103";
        String[] columns = { "SEQNO", "VBELN", "WADAT", "KUNNR", "MATNR", "WERKS", "IF_FLG", "LFIMG", "LFIMG_C", "ZUSER", "SGTXT" };
        String[][] rows = {
            { "3586196291", "1016235861", "20241121", "0000013823", "816435", "1200", "1", "48", "0", "SAP_IF", "" },
            { "3586196291", "1016235861", "20241121", "0000013823", "816444", "1200", "1", "25", "0", "SAP_IF", "" }
        };
		
        // DataSet 생성
        DataSet dataSet = new DataSet(name, columns, rows);
		boolean success = true;

		// 배송 번호 리스트 준비
		Box delParam = new Box();
		Set<String> deliNoList = new HashSet<>();
		deliNoList.add("1016235861");
		int beginIdx = 0;
        
        try {
            // SAP RFC FunctionName 지정
            JcoJsonMessage reqMsg = new JcoJsonMessage();
            reqMsg.setServiceName("ZCVO_SMT_CRATE_QTY");
            
			//데이타셋 추가
			reqMsg.addDataSet(dataSet);
			dataSet = null;
            
            // 로그에 요청 메시지 출력
            System.out.println("Request Message: " + reqMsg.getServiceName());
            // SAP 서버로 메시지 전송
            BaseMessage resMsg = null;
            String serviceName = reqMsg.getServiceName();

            Charset charset = Charset.forName("euc-kr");
            System.out.println("SAP 서버로 메시지 전송 try전: ");
            try {
                // JSON 변환 및 SAP 서버로 전송
                Gson gson = (new GsonBuilder()).setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().create();
                System.out.println("gson ");
                String strJson = gson.toJson(reqMsg);
                System.out.println("strJson ");
                //SAP 서버로 전송
                byte[] recv = sapGwClient.sendReceive(strJson.getBytes(charset));
                System.out.println("recv ");
                if (recv == null) {
                    throw new Exception("Result is null.");
                }
                // 응답 메시지를 로그에 출력
                resMsg = gson.fromJson(new String(recv, charset), ServiceMessage.class);
                System.out.println("Request Message: " + reqMsg.getServiceName());
                
				if (resMsg == null) {
					resMsg = new ServiceMessage();
				}

                // SAP 인터페이스 결과 저장 준비
				Box ifStatBox = new Box();
				ifStatBox.put("shipNo", "0015266498");
				ifStatBox.put("deliNoList", deliNoList);

				Box sapIfHist = new Box();
				sapIfHist.put("ifSeq", 1);
				sapIfHist.put("sendDiv", "U");
				sapIfHist.put("shipNo", "0015266498");
				sapIfHist.put("deliNoList", deliNoList);

				delParam.put("deliNoList", deliNoList);
               
                
            } catch (Exception e) {
            	System.out.println("SAP Communication Error: " + e.getLocalizedMessage());
            }

        } catch (Exception e) {
        	System.out.println("Error during SAP communication: " + e.getMessage());
        }

    }
}