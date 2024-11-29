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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.time.DayOfWeek;

import com.maeil.rtm.constants.MailConst;
import com.maeil.rtm.constants.ServerConfigConst;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;


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
		checkFileAndSendEmail();
		//scheduleTasks();
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
	    String server = ServerConfigConst.CRM_SERVER;
	    int port = ServerConfigConst.CRM_SFTP_PORT;
	    String user = ServerConfigConst.CRM_USER;
	    String pass = ServerConfigConst.CRM_PASSWORD;
	    String BASE_PATH = ServerConfigConst.CRM_BASE_PATH;

	    // 빌더 초기화
	    emailMessageBuilder.setLength(0);

	    // 오늘 날짜 가져오기
	    LocalDate today = LocalDate.now();
	    DayOfWeek dayOfWeek = today.getDayOfWeek();
	    int dayOfMonth = today.getDayOfMonth();

	    String[] logFiles = {
	        "OutputDMMILKPOWDER.log." + getFormattedDate("yyyyMMdd", today) + "0800",
	        "OutputDMITEM.log." + getFormattedDate("yyyyMMdd", today) + "0900",
	        "SendDSClaimMail.log." + getFormattedDate("yyyyMMdd", today) + "0630",
	        "OutputDMALL.log." + getFormattedDate("yyyyMMdd", today) + "0600"
	    };

	    SSHClient sshClient = null;  // SSHClient 객체를 try-with-resources 밖에서 선언
	    SFTPClient sftpClient = null; // SFTPClient 객체를 try-with-resources 밖에서 선언
	    
	    try {
	        sshClient = new SSHClient();
	        sshClient.setConnectTimeout(60000);
	        // SSH 연결 설정
	        sshClient.addHostKeyVerifier((hostname, port1, key) -> true);
	        sshClient.connect(server, port);
	        sshClient.authPassword(user, pass);
	        // SFTP 클라이언트 생성 및 파일 확인
	        sftpClient = sshClient.newSFTPClient();

	        emailMessageBuilder.append("CRM 배치파일 확인\n");

	        // 로그 파일 확인
	        for (String logFile : logFiles) {
	            String filePath = BASE_PATH + logFile;
	            boolean exists = checkFileExistence(sftpClient, filePath);
	            // 존재 여부에 따라 메시지 추가
	            if (exists) {
	                emailMessageBuilder.append("파일이 존재합니다: ").append(filePath).append("\n");
	            } else {
	                emailMessageBuilder.append("파일이 존재하지 않습니다: ").append(filePath).append("\n");
	            }
	        }

	        // 추가 파일 확인 로직
	        if (dayOfMonth == 1 || dayOfMonth == 16 || dayOfWeek == DayOfWeek.MONDAY) {
	            checkFileAndSendEmailPRM(emailMessageBuilder);
	        } else {
	            System.out.println("1일, 16일, 월요일이 아닙니다.");
	        }

	        gmailSend(emailMessageBuilder.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        gmailSend("파일 체크 중 오류 발생: " + e.getMessage());
	    } finally {
	        // 리소스를 닫기 위해 finally 블록 사용
	        try {
	            if (sftpClient != null) {
	                sftpClient.close();
	            }
	            if (sshClient != null) {
	                sshClient.disconnect();
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
    
	private void checkFileAndSendEmailPRM(StringBuilder emailMessageBuilder) {
	    String server = ServerConfigConst.PRM_SERVER;
	    int port = ServerConfigConst.PRM_SFTP_PORT;
	    String user = ServerConfigConst.PRM_USER;
	    String pass = ServerConfigConst.PRM_PASSWORD;
	    String BASE_PATH = ServerConfigConst.PRM_BASE_PATH;
	 
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

	    SSHClient sshClient = null;  // SSHClient 객체를 try-with-resources 밖에서 선언
	    SFTPClient sftpClient = null; // SFTPClient 객체를 try-with-resources 밖에서 선언

	    try {
	        sshClient = new SSHClient();
	        // SSH 연결 설정
	        sshClient.addHostKeyVerifier((hostname, port1, key) -> true);
	        sshClient.connect(server, port);
	        sshClient.authPassword(user, pass);

	        // SFTP 클라이언트 생성 및 파일 확인
	        sftpClient = sshClient.newSFTPClient();
	        emailMessageBuilder.append("낙농 배치파일 확인(매달 1, 16일, 매주 주말)").append("\n");

	        // 로그 파일 확인
	        for (String logFile : logFiles) {
	            String filePath = BASE_PATH + logFile;
	            boolean exists = checkFileExistence(sftpClient, filePath);
	            // 존재 여부에 따라 메시지 추가
	            if (exists) {
	                emailMessageBuilder.append("파일이 존재합니다: ").append(filePath).append("\n");
	            } else {
	                emailMessageBuilder.append("파일이 존재하지 않습니다: ").append(filePath).append("\n");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        gmailSend("파일 체크 중 오류 발생: " + e.getMessage());
	    } finally {
	        // 리소스를 닫기 위해 finally 블록 사용
	        try {
	            if (sftpClient != null) {
	                sftpClient.close();
	            }
	            if (sshClient != null) {
	                sshClient.disconnect();
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}

    private boolean checkFileExistence(SFTPClient sftpClient, String filePath) {
        try {
        	sftpClient.stat(filePath); // 파일 존재 여부 확인
        	return true;
        } catch (IOException e) {
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
            for (String recipient : MailConst.RECIPIENTS) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            // Subject
            message.setSubject("CRM, 낙농 배치파일 확인용 메일"); //메일 제목을 입력

            // Text
            message.setText(str); //메일 내용을 입력

            // send the message
            Transport.send(message); ////전송
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	} 
}
