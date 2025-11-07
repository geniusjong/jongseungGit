package com.lottoweb.util;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
	private static String url;
	private static String user;
	private static String password;
	private static volatile boolean initialized = false;

	private static void init() {
		if (initialized) return;
		synchronized (DbConfig.class) {
			if (initialized) return;
			try {
				Properties props = new Properties();
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
				
				if (in != null) {
					// properties 파일에서 읽기
					props.load(in);
					in.close();
					url = props.getProperty("db.url");
					user = props.getProperty("db.user");
					password = props.getProperty("db.password");
				} else {
					// properties 파일이 없으면 환경 변수에서 읽기 시도
					url = System.getenv("DB_URL");
					user = System.getenv("DB_USER");
					password = System.getenv("DB_PASSWORD");
					
					// 환경 변수에도 없으면 기본 URL/User는 설정하되, 비밀번호는 필수
					if (url == null || url.trim().isEmpty()) {
						url = "jdbc:mariadb://localhost:3306/lotto?useUnicode=true&characterEncoding=UTF-8";
					}
					if (user == null || user.trim().isEmpty()) {
						user = "root";
					}
					if (password == null || password.trim().isEmpty()) {
						throw new RuntimeException("DB_PASSWORD 환경 변수 또는 db.properties 파일이 필요합니다.");
					}
				}
				
				// 필수 값 검증
				if (url == null || url.trim().isEmpty()) {
					throw new RuntimeException("데이터베이스 URL이 설정되지 않았습니다.");
				}
				if (user == null || user.trim().isEmpty()) {
					throw new RuntimeException("데이터베이스 사용자명이 설정되지 않았습니다.");
				}
				if (password == null || password.trim().isEmpty()) {
					throw new RuntimeException("데이터베이스 비밀번호가 설정되지 않았습니다.");
				}
				
				// JDBC 드라이버 로드 (안전)
				try { Class.forName("org.mariadb.jdbc.Driver"); } catch (ClassNotFoundException ignore) {}
				initialized = true;
			} catch (Exception e) {
				throw new RuntimeException("Failed to load DB configuration", e);
			}
		}
	}

	public static String getUrl() {
		if (!initialized) init();
		return url;
	}

	public static String getUser() {
		if (!initialized) init();
		return user;
	}

	public static String getPassword() {
		if (!initialized) init();
		return password;
	}
}
