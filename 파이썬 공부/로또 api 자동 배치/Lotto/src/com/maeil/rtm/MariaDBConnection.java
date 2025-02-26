package com.maeil.rtm;
import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MariaDBConnection {
    public static void main(String[] args) {
        String dbUrl = "jdbc:mariadb://localhost:3306/lotto";
        String dbUser = "root";
        String dbPassword = "1234";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // 1. 최신 회차 번호 가져오기
            String query = "SELECT MAX(postgame) + 1 AS maxnumber FROM tb_lotto_number";
            int nextDrawNumber = 0;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    nextDrawNumber = rs.getInt("maxnumber");
                }
            }

            if (nextDrawNumber == 0) {
                System.out.println("회차 정보를 가져올 수 없습니다.");
                return;
            }

            // 2. API 요청
            String apiUrl = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + nextDrawNumber;
            HttpURLConnection connApi = (HttpURLConnection) new URL(apiUrl).openConnection();
            connApi.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connApi.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                // 3. JSON 파싱
                JSONObject json = new JSONObject(response.toString());
                if (!json.has("returnValue") || !json.getString("returnValue").equals("success")) {
                    System.out.println("로또 데이터를 가져오지 못했습니다.");
                    return;
                }

                int postgame = json.getInt("drwNo");
                int num1 = json.getInt("drwtNo1");
                int num2 = json.getInt("drwtNo2");
                int num3 = json.getInt("drwtNo3");
                int num4 = json.getInt("drwtNo4");
                int num5 = json.getInt("drwtNo5");
                int num6 = json.getInt("drwtNo6");
                int bonusnum = json.getInt("bnusNo");
                long firstWinamnt = json.getLong("firstWinamnt");
                long firstAccumamnt =  json.getLong("firstAccumamnt");
                long firstprizecount = firstAccumamnt/firstWinamnt;

                System.out.println("데이터 확인 : firstWinamnt : "+ firstWinamnt + "firstAccumamnt : "+firstAccumamnt+" firstprizecount : "+ firstprizecount );
                
                
                // 4. DB에 INSERT
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
