package com.maeil.rtm;
import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MariaDBConnection {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000; // 5초

    public static void main(String[] args) {
        String dbUrl = "jdbc:mariadb://localhost:3306/lotto";
        String dbUser = "root";
        String dbPassword = "1234";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // 1. 최신 회차 번호 가져오기
            int nextDrawNumber = getNextDrawNumber(conn);
            if (nextDrawNumber == 0) {
                System.out.println("회차 정보를 가져올 수 없습니다.");
                return;
            }

            // 2. API 요청 및 데이터 처리
            JSONObject json = requestLottoData(nextDrawNumber);
            if (json == null) {
                System.out.println("로또 데이터를 가져오지 못했습니다.");
                return;
            }

            // 3. JSON 파싱 및 DB에 INSERT
            processAndSaveLottoData(conn, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNextDrawNumber(Connection conn) throws SQLException {
        String query = "SELECT MAX(postgame) + 1 AS maxnumber FROM tb_lotto_number";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("maxnumber");
            }
        }
        return 0;
    }

    private static JSONObject requestLottoData(int drawNumber) {
        String apiUrl = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + drawNumber;
        
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                HttpURLConnection connApi = (HttpURLConnection) new URL(apiUrl).openConnection();
                connApi.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connApi.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("returnValue") && json.getString("returnValue").equals("success")) {
                        return json;
                    }
                }
            } catch (Exception e) {
                System.out.println("API 요청 실패. 재시도 중... (시도 " + (attempt + 1) + "/" + MAX_RETRIES + ")");
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
        }
        System.out.println("최대 재시도 횟수 초과. API 요청 실패.");
        return null;
    }

    private static void processAndSaveLottoData(Connection conn, JSONObject json) throws SQLException {
        int postgame = json.getInt("drwNo");
        int num1 = json.getInt("drwtNo1");
        int num2 = json.getInt("drwtNo2");
        int num3 = json.getInt("drwtNo3");
        int num4 = json.getInt("drwtNo4");
        int num5 = json.getInt("drwtNo5");
        int num6 = json.getInt("drwtNo6");
        int bonusnum = json.getInt("bnusNo");
        long firstWinamnt = json.getLong("firstWinamnt");
        long firstAccumamnt = json.getLong("firstAccumamnt");
        long firstprizecount = firstAccumamnt / firstWinamnt;

        System.out.println("데이터 확인 : firstWinamnt : " + firstWinamnt + " firstAccumamnt : " + firstAccumamnt + " firstprizecount : " + firstprizecount);

        String insertQuery = "INSERT INTO tb_lotto_number (postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, postgame);
            pstmt.setInt(2, num1);
            pstmt.setInt(3, num2);
            pstmt.setInt(4, num3);
            pstmt.setInt(5, num4);
            pstmt.setInt(6, num5);
            pstmt.setInt(7, num6);
            pstmt.setInt(8, bonusnum);
            pstmt.setLong(9, firstWinamnt);
            pstmt.setLong(10, firstprizecount);

            pstmt.executeUpdate();
            System.out.println("로또 데이터가 성공적으로 저장되었습니다.");
        }
    }
}