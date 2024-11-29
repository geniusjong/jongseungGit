package com.maeil.rtm;

import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

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

public class App3
{
	private static final Logger logger = LogManager.getLogger(App3.class);
	private static final int START_HOUR = 7; //시작시간
	private static final int END_HOUR = 23; //종료시간
	private static final int DELI_THRESHOLD = 5000; //DELI 건수
	private static final String EMAIL_START = "Monitoring Start!!"; //실행 했을때 이메일 제목
	private static final String EMAIL_SUBJECT = "CVO DELI Verification Email"; //이메일 제목
	private static final long ONE_HOUR_IN_MILLISECONDS = 1000 * 60 * 60; //배치 실행 시간(1시간)

	StringBuilder emailMessageBuilder = new StringBuilder(); //EMAIL 내용 추가할 StringBuilder 
	// 메인 메소드: 프로그램의 진입점
    public static void main( String[] args )
    {
        final App3 proc = new App3();
		proc.startRealTimeMonitorTask(); // 모니터링 작업 시작

		// 프로그램 종료 시 리소스 정리를 위한 셧다운 훅 추가
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                proc.stopRealTimeMonitorTask();
				LogManager.shutdown();
            }
        });
 }
 	// 이메일 인증 정보
    String user = MailConst.USER;
    String password = MailConst.PASSWORD;

    // 실시간 데이터 수신 모니터링 타이머
	private Timer realTimeMonitorTimer = new Timer();

	// 실시간 데이터 수신 모니터링 타이머 시작
	public void startRealTimeMonitorTask() {
		gmailSend(EMAIL_START); // 시작 알림 이메일 전송
		realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 0);
	}
	//실시간 데이터 수신 모니터링 종료시 리소스 해제
    public void stopRealTimeMonitorTask() {
        if (realTimeMonitorTimer != null) {
            realTimeMonitorTimer.cancel();
            realTimeMonitorTimer.purge();
            realTimeMonitorTimer = null;
            logger.info("RealTimeMonitorTask stopped and Timer resources released.");
        }
    }
	// 실제 모니터링 작업을 수행하는 내부 클래스
	private class TmRealTimeMonitorTask extends TimerTask {
		@Override
		public void run() {
			//realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 1000*60*1); //1분에 1번씩 체크(테스트용)
			realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), ONE_HOUR_IN_MILLISECONDS);  // 1시간에 1번씩 체크

			String message = EMAIL_SUBJECT;
			String DELI = "";

			//체크로직
			try {
			// 데이터베이스에서 DELI 값 조회
			DELI = DatabaseAccessor.getInstance().selectDELI();

			// DELI 값 검증 및 처리
				int deliCount;
			    deliCount = Integer.parseInt(DELI); // 문자열을 정수로 변환
			    // DELI가 5천 건을 초과하는지 확인
			    if (deliCount > DELI_THRESHOLD) {
			        // 5천 건을 초과할 때의 로직
			        logger.info("DELI exceeds 5,000 records.");

					message = message + "CVO DEIL exceeds 5,000 records. Kindly check and confirm.";
					message = message +"\n\n DELI :" + DELI;

					int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
					// 현재 시간이 지정된 범위 내인 경우에만 이메일 전송
					if(START_HOUR<=curHour && curHour<=END_HOUR){
							gmailSend(message);
							logger.info("message 확인 :" +message);
					}
			    } else {
			        logger.info("DELI is 5,000 records or less.");
			    }
				logger.info("selectDELI result : " + DELI);
			} catch (NumberFormatException e) {
				// DELI 값 파싱 오류 처리
				logger.error("DELI 값을 정수로 변환하는 데 실패했습니다. DELI: {}", DELI, e);
				logger.info("DELI 값이 유효한 정수가 아닙니다: " + DELI);
			}catch (Exception e) {
				// 기타 예외 처리
                logger.error("DELI 처리 중 예외가 발생했습니다.", e);
            }
		}
	}
	// Gmail을 통한 이메일 전송 메소드
	public void gmailSend(String str) {
        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
        prop.put("mail.smtp.host", MailConst.HOST); 
        prop.put("mail.smtp.port", MailConst.PORT); 
        prop.put("mail.smtp.auth", MailConst.AUTH); 
        prop.put("mail.smtp.ssl.enable", MailConst.AUTH);
        prop.put("mail.smtp.ssl.trust", MailConst.HOST);

		// 이메일 세션 생성 및 인증
        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        
        try {
			// 이메일 메시지 생성 및 설정
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));

            //수신자메일주소
            for (String recipient : MailConst.RECIPIENTS) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            // Subject
            message.setSubject(EMAIL_SUBJECT); //메일 제목을 입력

            // Text
            message.setText(str);    //메일 내용을 입력

            // send the message
            Transport.send(message); ////전송
            logger.info("message sent successfully...");
			logger.info("이메일이 성공적으로 전송되었습니다.");
        } catch (AddressException e) {
            // 주소 형식 오류 처리
            e.printStackTrace();
			logger.error("잘못된 이메일 주소 형식입니다.", e);
        } catch (MessagingException e) {
            // 메시지 전송 오류 처리
            e.printStackTrace();
			logger.error("이메일 전송 중 오류가 발생했습니다.", e);
        }
	}
}

