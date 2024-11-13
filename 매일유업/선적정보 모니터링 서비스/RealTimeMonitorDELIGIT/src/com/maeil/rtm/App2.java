package com.maeil.rtm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;

import com.maeil.rtm.constants.MailConst;
import com.maeil.rtm.database.DatabaseAccessor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.Session;

public class App2
{
	private static final Logger logger = LogManager.getLogger(App2.class);
	StringBuilder emailMessageBuilder = new StringBuilder();

    public static void main( String[] args )
    {
        App2 proc = new App2();
		proc.startRealTimeMonitorTask();    
    }
    String user = MailConst.USER;
    String password = MailConst.PASSWORD;
 // 실시간 데이터 수신 모니터링 타이머
	private Timer realTimeMonitorTimer = new Timer();
	private boolean emailSentToday = false; // 오늘 이메일이 전송되었는지 확인하는 플래그	
	
	// 실시간 데이터 수신 모니터링 타이머 시작
	public void startRealTimeMonitorTask() {
		gmailSend("Monitoring Start!!");
		//checkFileAndSendEmail();
		scheduleTasks();
		realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 0);
	}
    private void scheduleTasks() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkFileAndSendEmailAtSpecificTime(); // 특정 시간에 체크 메서드 호출
            }
        };

        // 9시에 실행
        scheduleTaskAt(task, 9);
    }
    private void scheduleTaskAt(TimerTask task, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 현재 시간이 설정한 시간보다 늦으면 다음 날로 설정
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 다음 실행 시간까지의 딜레이 계산
        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        realTimeMonitorTimer.schedule(task, delay, 1000 * 60 * 60 * 24); // 24시간 간격으로 반복
    }
    
	private class TmRealTimeMonitorTask extends TimerTask {
		@Override
		public void run() {
			//realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 1000*60*1); //1분에 1번씩 체크(테스트용)
			realTimeMonitorTimer.schedule(new TmRealTimeMonitorTask(), 1000*60*60);  // 1시간에 1번씩 체크

			String message = "CVO 확인용 메일";
			String DELI = "";
			//체크로직
			try {
			// DELI를 가져옵니다.
			DELI = DatabaseAccessor.getInstance().selectDELI();

			// DELI를 정수로 변환합니다.
				int deliCount;
			    deliCount = Integer.parseInt(DELI); // 문자열을 정수로 변환
			    // DELI가 2만 건을 초과하는지 확인
			    if (deliCount > 5000) {
			        // 2만 건을 초과할 때의 로직
			        System.out.println("DELI는 5천 건을 초과합니다.");
			        
					message = message + "선적정보 5천 건 초과합니다. 확인해주시기 바랍니다.";
					message = message +"\n\n DELI :" + DELI;

					int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
					//int curMin = Calendar.getInstance().get(Calendar.MINUTE);
					if(7<=curHour && curHour<=23){
							gmailSend(message);
							System.out.println("message 확인 :" +message);
					}
			    } else {
			        System.out.println("DELI는 5천 건 이하입니다.");
			    }
				System.out.println("selectDELI result : " + DELI);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("DELI 값이 유효한 정수가 아닙니다: " + DELI);
			}
			// 오전 9시 5분에 파일 체크
	        checkFileAndSendEmailAtSpecificTime();
		}
	}
	
	// 특정 시간에 파일 체크 및 이메일 전송
	private void checkFileAndSendEmailAtSpecificTime() {
	    Calendar now = Calendar.getInstance();
	    int hour = now.get(Calendar.HOUR_OF_DAY);
	    int minute = now.get(Calendar.MINUTE);
	    int dayOfWeek = now.get(Calendar.DAY_OF_WEEK); // 요일 가져오기
	    
	 // 현재 시간이 9시부터 9시 5분 사이인지 확인
	    if (hour == 9 && minute >= 0 && minute <= 5 && !emailSentToday) {
	        checkFileAndSendEmail(); // 파일 체크 및 이메일 전송
	        emailSentToday = true; // 오늘 이메일이 전송되었음을 기록
	    } else if (hour != 9) {
	        emailSentToday = false; // 시간이 9시가 아닐 경우 리셋
	    }
	}
	
	
	private void checkFileAndSendEmail() {
        String server = "10.0.12.4";
        int port = 20100; // SFTP 포트
        String user = "weblogic"; // 사용자 이름
        String pass = "aodlf!1234789"; // 비밀번호
        String BASE_PATH = "/app/weblogic/myserver/public_html/WEB-INF/classes/log/"; // 확인할 파일의 경로

        com.jcraft.jsch.Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
 

        
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek(); // 타입은 DayOfWeek로 설정
        int dayOfMonth = today.getDayOfMonth();
        //int dayOfMonth = 1;
        
        String[] logFiles = {
                "OutputDMMILKPOWDER.log." + getFormattedDate("yyyyMMdd",today) + "0800",
                "OutputDMITEM.log." + getFormattedDate("yyyyMMdd",today) + "0900",
                "SendDSClaimMail.log." + getFormattedDate("yyyyMMdd",today) + "0630",
                "OutputDMALL.log." + getFormattedDate("yyyyMMdd",today) + "0600"
            };
         
	    try {
	    	// JSch 객체 생성 및 세션 설정(CRM)
	        JSch jsch = new JSch();
	        session = jsch.getSession(user, server, port);
	        session.setPassword(pass);

	        // 보안 설정
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000); // 타임아웃 설정
            
            // SFTP 채널 연결
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            //빌더 초기화
            emailMessageBuilder.setLength(0);        
            emailMessageBuilder.append("CRM 배치파일 확인").append("\n");
            
            // 각 로그 파일에 대해 존재 여부 확인
            for (String logFile : logFiles) {
                String filePath = BASE_PATH + logFile;
                boolean exists = checkFileExistence(channelSftp, filePath);
                // 존재 여부에 따라 메시지 추가
                if (exists) {
                    emailMessageBuilder.append("파일이 존재합니다: ").append(filePath).append("\n");
                } else {
                    emailMessageBuilder.append("파일이 존재하지 않습니다: ").append(filePath).append("\n");
                }
            }
            
            // 매달 1일과 16일 확인하는 파일
            if (dayOfMonth == 1 || dayOfMonth == 16) {
            	checkFileAndSendEmailPRM(emailMessageBuilder);
            } 
            // (토요일, 일요일 데이터를 월요일날 확인) 확인하는 파일
            else if (dayOfWeek == DayOfWeek.MONDAY) {
            	checkFileAndSendEmailPRM(emailMessageBuilder);
            } else {
                // 평일이나 확인이 필요 없는 날에는 로직을 생략하도록 설정
                System.out.println("1일 16일 토요일 일요일이 아닙니다.");
            }
            gmailSend(emailMessageBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            gmailSend("파일 체크 중 오류 발생: " + e.getMessage());
        } finally {
        	emailMessageBuilder.setLength(0); // 항상 초기화
            try {
                if (channelSftp != null) {
                    channelSftp.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
	
	private void checkFileAndSendEmailPRM(StringBuilder emailMessageBuilder) {
        
		System.out.println("PRM시작합니다. ");
		
		String server = "172.16.10.82";
        //int port = 22; // SFTP 포트
        String user = "weblogic"; // 사용자 이름
        String pass = "!Maeilito2019"; // 비밀번호
        String BASE_PATH = "/app/weblogic/bea/domains/prm_domain/wodr_lib/scripts/log2/"; // 확인할 파일의 경로

        com.jcraft.jsch.Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek(); // 타입은 DayOfWeek로 설정
        int dayOfMonth = today.getDayOfMonth();
        
        String[] logFiles;

        if (dayOfWeek == DayOfWeek.MONDAY) {
            // 월요일일 경우, 전날(일요일)의 날짜로 변경
        	LocalDate sunday = today.minusDays(1); //일요일
            LocalDate saturday = today.minusDays(2); //토요일
            logFiles = new String[] {
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", sunday) + "_1200.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", sunday) + "_1700.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", saturday) + "_1200.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", saturday) + "_1700.log" //주말데이터
            };
        } else if (dayOfMonth == 1 || dayOfMonth == 16){
            logFiles = new String[] {
                "FMD_VACCINATION_MONITOR_" + getFormattedDate("yyMMdd", today) + "_0830.log" //1, 16일 데이터
            };
        } else if (dayOfWeek == DayOfWeek.MONDAY && dayOfMonth == 1 || dayOfMonth == 16){
        	LocalDate sunday = today.minusDays(1); //일요일
            LocalDate saturday = today.minusDays(2); //토요일
            logFiles = new String[] {
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", sunday) + "_1200.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", sunday) + "_1700.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", saturday) + "_1200.log", //주말데이터
                "VETERINARIAN_CONFIRM_MAIL_" + getFormattedDate("yyMMdd", saturday) + "_1700.log", //주말데이터
                "FMD_VACCINATION_MONITOR_" + getFormattedDate("yyMMdd", today) + "_0830.log" //1, 16일 데이터
            };
        } else {
            // 그 외의 경우 기본 파일
            logFiles = new String[] {
                "FMD_VACCINATION_MONITOR_" + getFormattedDate("yyyyMMdd", today) + "_0830.log" // 기본 데이터
            };
        }

	    try {
	    	// JSch 객체 생성 및 세션 설정(PRM)
	        JSch jsch = new JSch();
	        session = jsch.getSession(user, server);
	        session.setPassword(pass);

	        // 보안 설정
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000); // 타임아웃 설정
            
            // SFTP 채널 연결
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            
            emailMessageBuilder.append("낙농 배치파일 확인(매달 1, 16일, 매주 주말)").append("\n");
            
            // 각 로그 파일에 대해 존재 여부 확인
            for (String logFile : logFiles) {
                String filePath = BASE_PATH + logFile;
                boolean exists = checkFileExistence(channelSftp, filePath);
                // 존재 여부에 따라 메시지 추가
                if (exists) {
                    emailMessageBuilder.append("파일이 존재합니다: ").append(filePath).append("\n");
                } else {
                    emailMessageBuilder.append("파일이 존재하지 않습니다: ").append(filePath).append("\n");
                }
            }
           //gmailSend(emailMessageBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            gmailSend("파일 체크 중 오류 발생: " + e.getMessage());
        } finally {
            try {
                if (channelSftp != null) {
                    channelSftp.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
	
	
    private boolean checkFileExistence(ChannelSftp channelSftp, String filePath) {
        try {
            Vector<?> fileList = channelSftp.ls(filePath);
            return (fileList != null && !fileList.isEmpty());
        } catch (Exception e) {
            System.out.println("파일을 찾을 수 없습니다: " + filePath);
            return false;
        }
    }
	
 // 날짜 포맷 함수
    private static String getFormattedDate(String pattern, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
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
            message.setSubject("CVO 확인용 메일"); //메일 제목을 입력

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
