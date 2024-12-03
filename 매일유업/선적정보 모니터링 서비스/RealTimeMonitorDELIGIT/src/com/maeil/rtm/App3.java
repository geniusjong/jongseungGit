package com.maeil.rtm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	//private static final String LOG_FILE_PATH = "C:/log/cvo/batch.log"; // 로컬로그 파일 경로
	private static final String LOG_FILE_PATH = "F:/log/cvo/batch.log"; // 로그 파일 경로
    private static final String[] BATCH_TASKS = {
		"SAP IF 하차, 회수 수량 전송 스케쥴러 수행 완료",
		"첨부파일정리 스케쥴러 수행 완료",
		"미등록 차량/대리점 데이터 삭제 스케쥴러 수행 완료",
		"포장재출고회수통계 등록 스케쥴러 수행 완료",
		"거래처정보 병합 스케쥴러 수행 완료",
		"창고통계 등록 스케쥴러 수행 완료",
		"14일 이전 미전송된 SAP IF 하차, 회수 수량 전송 스케쥴러 수행 완료",
		"차량통계 등록 스케쥴러 수행 완료",
		"수송차량통계 등록 스케쥴러 수행 완료"
	};

    private void scheduleLogCheckTask() {
        TimerTask logCheckTask = new TimerTask() {
            @Override
            public void run() {
                checkLogFileAndSendEmail();
            }
        };
        
        // 매일 8시 50분에 실행
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 0);

        // 현재 시간이 이미 지났다면 다음 날로 설정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        /* 
        realTimeMonitorTimer.scheduleAtFixedRate(logCheckTask, 
                0, // 지연 없이 즉시 시작
                5 * 60 * 1000);
		*/
        
        realTimeMonitorTimer.scheduleAtFixedRate(logCheckTask, 
            calendar.getTime(), 
            24 * 60 * 60 * 1000); // 24시간마다 반복
    }

    private void checkLogFileAndSendEmail() {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        // 월요일(2)부터 금요일(6)까지만 실행
        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
            try {
                if (checkLogFileForCompletion()) {
                    logger.info("배치 완료 메일을 전송했습니다.");
                } else {
                    logger.info("로그 파일에서 완료 메시지를 찾지 못했습니다.");
                }
            } catch (IOException e) {
                logger.error("로그 파일 확인 중 오류 발생", e);
            }
        } 
	   /*
		try{
			checkLogFileForCompletion();
		}	catch (IOException e) {
			logger.error("로그 파일 확인 중 오류 발생", e);
		}
		*/
    }
    private boolean checkLogFileForCompletion() throws IOException {
        String logFilePath = LOG_FILE_PATH; // 로그 파일 경로
		boolean[] taskCompleted = new boolean[BATCH_TASKS.length];
		int completedTasks = 0;
		StringBuilder fileContent = new StringBuilder(); // 파일 내용을 저장할 StringBuilder

		try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				fileContent.append(line).append("\n"); // 각 줄을 StringBuilder에 추가
				for (int i = 0; i < BATCH_TASKS.length; i++) {
					if (!taskCompleted[i] && line.contains(BATCH_TASKS[i])) {
						taskCompleted[i] = true;
						completedTasks++;
					}
				}
			}
    	}
	    boolean allTasksCompleted = (completedTasks == BATCH_TASKS.length);
    if (allTasksCompleted) {
        logger.info("모든 배치 작업이 완료되었습니다.");
		gmailSend(fileContent.toString());
    } else {
        logger.info("완료되지 않은 배치 작업이 있습니다.");
        for (int i = 0; i < BATCH_TASKS.length; i++) {
            if (!taskCompleted[i]) {
                logger.info("미완료 작업: " + BATCH_TASKS[i]);
            }
        }
		gmailSend(fileContent.toString());
    }
    return allTasksCompleted;
}
 	// 이메일 인증 정보
    String user = MailConst.USER;
    String password = MailConst.PASSWORD;

    // 실시간 데이터 수신 모니터링 타이머
	private Timer realTimeMonitorTimer = new Timer();

	// 실시간 데이터 수신 모니터링 타이머 시작
	public void startRealTimeMonitorTask() {
		gmailSend(EMAIL_START); // 시작 알림 이메일 전송
		realTimeMonitorTimer.scheduleAtFixedRate(new TmRealTimeMonitorTask(), 0, ONE_HOUR_IN_MILLISECONDS); // 1시간에 1번씩 체크
		scheduleLogCheckTask(); // 새로운 로그 체크 작업 스케줄링
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
			//realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), ONE_HOUR_IN_MILLISECONDS);  // 1시간에 1번씩 체크

			logger.info("RealTimeMonitorTask started. Will run every hour.");

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
            //message.setText(str);    //메일 내용을 입력
			message.setContent("<html><body>" + str + "</body></html>", "text/html; charset=UTF-8");  //메일 내용을 입력
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

