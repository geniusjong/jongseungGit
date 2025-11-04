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
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
				Properties props = new Properties();
				if (in != null) {
					props.load(in);
					url = props.getProperty("db.url");
					user = props.getProperty("db.user");
					password = props.getProperty("db.password");
				} else {
					// 기본값(로컬 개발 편의)
					url = "jdbc:mariadb://localhost:3306/lotto?useUnicode=true&characterEncoding=UTF-8";
					user = "root";
					password = "1234";
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
