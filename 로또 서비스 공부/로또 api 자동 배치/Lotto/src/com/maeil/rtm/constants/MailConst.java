package com.maeil.rtm.constants;

public class MailConst {
	public enum InsertType {
		LAST, LOG
	}
	/*mail info*/	
//	public static final int EVENT_WAKEUP_HOUR = 9;
//	public static final String RES_MSG_UNAUTHORIZED = "유효하지 않은 요청입니다";  // 유효하지 않은 요청입니다.
	public static final String HOST = "smtp.gmail.com";
	public static final String PORT = "465";
	public static final String USER = "itoweb9";  // ID
	public static final String PASSWORD = "vjjadehbenqrbsyg";  // PW
	public static final String AUTH = "true";
	public static final String[] RECIPIENTS = {
        "ito_web8@maeil.com",
        "jongseung.park@metanetglobal.com"
    };
}
