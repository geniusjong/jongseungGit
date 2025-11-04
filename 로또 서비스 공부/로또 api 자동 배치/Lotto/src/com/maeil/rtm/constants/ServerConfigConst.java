package com.maeil.rtm.constants;

import java.io.InputStream;
import java.util.Properties;

public class ServerConfigConst {
	private static Properties props = new Properties();
	private static boolean initialized = false;
	
	static {
		loadProperties();
	}
	
	private static void loadProperties() {
		try {
			InputStream inputStream = ServerConfigConst.class.getClassLoader()
					.getResourceAsStream("server.properties");
			if (inputStream == null) {
				// properties 폴더에서 직접 읽기 시도
				inputStream = ServerConfigConst.class.getClassLoader()
						.getResourceAsStream("../properties/server.properties");
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
			System.err.println("server.properties 파일을 읽을 수 없습니다: " + e.getMessage());
			loadFromEnvironment();
		}
	}
	
	private static void loadFromEnvironment() {
		// 환경 변수에서 읽기 시도
		if (System.getenv("CRM_PASSWORD") != null) {
			props.setProperty("server.crm.password", System.getenv("CRM_PASSWORD"));
		}
		if (System.getenv("PRM_PASSWORD") != null) {
			props.setProperty("server.prm.password", System.getenv("PRM_PASSWORD"));
		}
	}
	
	private static String getProperty(String key, String defaultValue) {
		String value = props.getProperty(key);
		if (value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		return value.trim();
	}
	
	private static int getIntProperty(String key, int defaultValue) {
		String value = getProperty(key, String.valueOf(defaultValue));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	// CRM 서버 설정
	public static String getCRM_SERVER() {
		return getProperty("server.crm.host", "10.0.12.4");
	}
	
	public static int getCRM_SFTP_PORT() {
		return getIntProperty("server.crm.sftp.port", 20100);
	}
	
	public static String getCRM_USER() {
		return getProperty("server.crm.user", "weblogic");
	}
	
	public static String getCRM_PASSWORD() {
		return getProperty("server.crm.password", 
			System.getenv("CRM_PASSWORD") != null ? System.getenv("CRM_PASSWORD") : "");
	}
	
	public static String getCRM_BASE_PATH() {
		return getProperty("server.crm.base.path", 
			"/app/weblogic/myserver/public_html/WEB-INF/classes/log/");
	}
	
	// PRM 서버 설정
	public static String getPRM_SERVER() {
		return getProperty("server.prm.host", "172.16.10.82");
	}
	
	public static int getPRM_SFTP_PORT() {
		return getIntProperty("server.prm.sftp.port", 22);
	}
	
	public static String getPRM_USER() {
		return getProperty("server.prm.user", "weblogic");
	}
	
	public static String getPRM_PASSWORD() {
		return getProperty("server.prm.password", 
			System.getenv("PRM_PASSWORD") != null ? System.getenv("PRM_PASSWORD") : "");
	}
	
	public static String getPRM_BASE_PATH() {
		return getProperty("server.prm.base.path", 
			"/app/weblogic/bea/domains/prm_domain/wodr_lib/scripts/log2/");
	}
	
	// 하위 호환성을 위한 final 필드 (deprecated - 사용하지 말 것)
	@Deprecated
	public static final String CRM_SERVER = getCRM_SERVER();
	@Deprecated
	public static final int CRM_SFTP_PORT = getCRM_SFTP_PORT();
	@Deprecated
	public static final String CRM_USER = getCRM_USER();
	@Deprecated
	public static final String CRM_PASSWORD = getCRM_PASSWORD();
	@Deprecated
	public static final String CRM_BASE_PATH = getCRM_BASE_PATH();
	@Deprecated
	public static final String PRM_SERVER = getPRM_SERVER();
	@Deprecated
	public static final int PRM_SFTP_PORT = getPRM_SFTP_PORT();
	@Deprecated
	public static final String PRM_USER = getPRM_USER();
	@Deprecated
	public static final String PRM_PASSWORD = getPRM_PASSWORD();
	@Deprecated
	public static final String PRM_BASE_PATH = getPRM_BASE_PATH();
}
