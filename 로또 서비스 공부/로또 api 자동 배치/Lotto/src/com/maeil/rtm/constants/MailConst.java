package com.maeil.rtm.constants;

import java.io.InputStream;
import java.util.Properties;

public class MailConst {
	public enum InsertType {
		LAST, LOG
	}
	
	private static Properties props = new Properties();
	private static boolean initialized = false;
	
	static {
		loadProperties();
	}
	
	private static void loadProperties() {
		try {
			InputStream inputStream = MailConst.class.getClassLoader()
					.getResourceAsStream("mail.properties");
			if (inputStream == null) {
				// properties 폴더에서 직접 읽기 시도
				inputStream = MailConst.class.getClassLoader()
						.getResourceAsStream("../properties/mail.properties");
			}
			if (inputStream != null) {
				props.load(inputStream);
				inputStream.close();
				initialized = true;
			} else {
				// 파일을 찾을 수 없으면 환경 변수에서 읽기 시도
				loadFromEnvironment();
			}
		} catch (Exception e) {
			System.err.println("mail.properties 파일을 읽을 수 없습니다: " + e.getMessage());
			loadFromEnvironment();
		}
	}
	
	private static void loadFromEnvironment() {
		// 환경 변수에서 읽기 시도
		if (System.getenv("MAIL_PASSWORD") != null) {
			props.setProperty("mail.password", System.getenv("MAIL_PASSWORD"));
		}
		if (System.getenv("MAIL_USER") != null) {
			props.setProperty("mail.user", System.getenv("MAIL_USER"));
		}
	}
	
	private static String getProperty(String key, String defaultValue) {
		String value = props.getProperty(key);
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		return value.trim();
	}
	
	/*mail info*/	
//	public static final int EVENT_WAKEUP_HOUR = 9;
//	public static final String RES_MSG_UNAUTHORIZED = "유효하지 않은 요청입니다";  // 유효하지 않은 요청입니다.
	
	public static String getHOST() {
		return getProperty("mail.host", "smtp.gmail.com");
	}
	
	public static String getPORT() {
		return getProperty("mail.port", "465");
	}
	
	public static String getUSER() {
		return getProperty("mail.user", "itoweb9");
	}
	
	public static String getPASSWORD() {
		return getProperty("mail.password", System.getenv("MAIL_PASSWORD") != null 
			? System.getenv("MAIL_PASSWORD") : "");
	}
	
	public static String getAUTH() {
		return getProperty("mail.auth", "true");
	}
	
	public static String[] getRECIPIENTS() {
		String recipientsStr = getProperty("mail.recipients", 
			"ito_web8@maeil.com,jongseung.park@metanetglobal.com");
		return recipientsStr.split(",");
	}
	
	// 하위 호환성을 위한 final 필드 (deprecated - 사용하지 말 것)
	@Deprecated
	public static final String HOST = getHOST();
	@Deprecated
	public static final String PORT = getPORT();
	@Deprecated
	public static final String USER = getUSER();
	@Deprecated
	public static final String PASSWORD = getPASSWORD();
	@Deprecated
	public static final String AUTH = getAUTH();
	@Deprecated
	public static final String[] RECIPIENTS = getRECIPIENTS();
}
