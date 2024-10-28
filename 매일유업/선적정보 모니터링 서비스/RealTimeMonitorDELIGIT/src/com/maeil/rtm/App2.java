package com.maeil.rtm;

import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.maeil.rtm.constants.MailConst;
import com.maeil.rtm.database.DatabaseAccessor;


/**
 * Hello world!
 *
 */
public class App2
{
	private static final Logger logger = LogManager.getLogger(App2.class);
	

    public static void main( String[] args )
    {
        App2 proc = new App2();
		proc.startRealTimeMonitorTask();    
    }
    String user = MailConst.USER;
    String password = MailConst.PASSWORD;
 // 실시간 데이터 수신 모니터링 타이머
	private Timer realTimeMonitorTimer = new Timer();
    	
	// 실시간 데이터 수신 모니터링 타이머 시작
	public void startRealTimeMonitorTask() {
		gmailSend("Monitoring Start!!");
		realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 0);
	}
	
	private class TmRealTimeMonitorTask extends TimerTask {
		@Override
		public void run() {
			//realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 1000*60*1); //1분에 1번씩 체크(테스트용)
			realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 1000*60*60);  // 1시간에 1번씩 체크
			String message = "[선적정보]";
			String DELI = "";
			//체크로직
			try {
			// DELI를 가져옵니다.
			DELI = DatabaseAccessor.getInstance().selectDELI();

			// DELI를 정수로 변환합니다.
				int deliCount;
			    deliCount = Integer.parseInt(DELI); // 문자열을 정수로 변환
			    //deliCount = 20001; // 2만건 넘었을때 테스트용
			    // DELI가 2만 건을 초과하는지 확인
			    if (deliCount > 20000) {
			        // 2만 건을 초과할 때의 로직
			        System.out.println("DELI는 2만 건을 초과합니다.");
			        
					message = message + "선적정보 2만건 초과합니다. 확인해주시기 바랍니다.";
					message = message +"\n\n DELI :" + DELI;

					int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
					//int curMin = Calendar.getInstance().get(Calendar.MINUTE);
					if(7<=curHour && curHour<=23){
							gmailSend(message);
							System.out.println("message 확인 :" +message);
					}
			        // 추가적인 처리 로직을 여기에 작성하세요.
			    } else {
			        System.out.println("DELI는 2만 건 이하입니다.");
			        // 다른 처리 로직을 여기에 작성하세요.
			    }
				System.out.println("selectDELI result : " + DELI);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("DELI 값이 유효한 정수가 아닙니다: " + DELI);
			}
		}
	}
	
	public void gmailSend(String str) {
        
        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
        prop.put("mail.smtp.host", MailConst.HOST); 
        prop.put("mail.smtp.port", MailConst.PORT); 
        prop.put("mail.smtp.auth", MailConst.AUTH); 
        prop.put("mail.smtp.ssl.enable", MailConst.AUTH); 
        prop.put("mail.smtp.ssl.trust", MailConst.HOST);
             
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            //수신자메일주소
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("ito_web8@maeil.com")); 
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("jongseung.park@metanetglobal.com"));

            // Subject
            message.setSubject("선적정보 알림입니다."); //메일 제목을 입력

            // Text
            message.setText(str);    //메일 내용을 입력

            // send the message
            Transport.send(message); ////전송
            System.out.println("message sent successfully...");
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	} 
}
