package com.lottoweb.dao;

import com.lottoweb.model.LottoNumber;
import com.lottoweb.model.LottoWeightCalculator;
import com.lottoweb.util.DbConfig;

import java.sql.*;
import java.util.*;

public class LottoDAO {

	// 최신 로또 번호 가져오기
	public LottoNumber getLottoNumber() {
		LottoNumber lottoNumber = null;
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM tb_lotto_number ORDER BY postgame DESC LIMIT 1")) {

		if (rs.next()) {
			lottoNumber = new LottoNumber();
			lottoNumber.setPostgame(rs.getInt("postgame"));
			lottoNumber.setNum1(rs.getInt("num1"));
			lottoNumber.setNum2(rs.getInt("num2"));
			lottoNumber.setNum3(rs.getInt("num3"));
			lottoNumber.setNum4(rs.getInt("num4"));
			lottoNumber.setNum5(rs.getInt("num5"));
			lottoNumber.setNum6(rs.getInt("num6"));
			lottoNumber.setBonusnum(rs.getInt("bonusnum"));
			lottoNumber.setFirstprize(rs.getLong("firstprize"));
			lottoNumber.setFirstprizecount(rs.getLong("firstprizecount"));
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lottoNumber;
	}

	// 로또 번호들 가져오기 (가중치 계산용)
	public List<Integer> getAllLottoNumbers() {
		List<Integer> allNumbers = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT num1, num2, num3, num4, num5, num6 FROM tb_lotto_number")) {

			while (rs.next()) {
				for (int i = 1; i <= 6; i++) {
					allNumbers.add(rs.getInt("num" + i));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return allNumbers;
	}

	// 로또 번호 추첨 (가중치 기반)
	public Map<String, Object> drawLottoNumbers() {
		List<Integer> allNumbers = getAllLottoNumbers();
		Map<Integer, Double> weights = LottoWeightCalculator.calculateWeights(allNumbers);
		return LottoWeightCalculator.drawLotto(weights, 6);
	}

    // 특정 번호를 반드시 포함하여 추첨 (가중치 기반, 중복 없음)
    public Map<String, Object> drawLottoNumbersIncluding(int includeNumber) {
        if (includeNumber < 1 || includeNumber > 45) return drawLottoNumbers();
        List<Integer> allNumbers = getAllLottoNumbers();
        Map<Integer, Double> weights = LottoWeightCalculator.calculateWeights(allNumbers);

        // 포함 번호는 미리 선택하고, 가중치 목록에서 제거한 뒤 나머지 5개를 뽑는다
        Map<Integer, Double> filtered = new LinkedHashMap<>(weights);
        filtered.remove(includeNumber);

        Map<String, Object> partial = LottoWeightCalculator.drawLotto(filtered, 5);
        @SuppressWarnings("unchecked")
        List<Integer> selected = (List<Integer>) partial.get("drawnNumbers");
        if (!selected.contains(includeNumber)) selected.add(includeNumber);
        Collections.sort(selected);

        // 보너스는 중복 없이 선택
        Random r = new Random();
        int bonus;
        do { bonus = r.nextInt(45) + 1; } while (selected.contains(bonus));

        Map<String, Object> result = new HashMap<>();
        result.put("drawnNumbers", selected);
        result.put("bonusNumber", bonus);
        return result;
    }

	// 전체 히스토리 건수
	public int countLottoHistory() {
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tb_lotto_number")) {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 페이징 히스토리 조회 (postgame DESC)
	public List<LottoNumber> getLottoHistory(int offset, int limit) {
		List<LottoNumber> list = new ArrayList<>();
		String sql = "SELECT postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount FROM tb_lotto_number ORDER BY postgame DESC LIMIT ? OFFSET ?";
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapRow(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 필터링된 전체 건수 (회차범위/번호 포함)
	public int countLottoHistory(Integer startPostgame, Integer endPostgame, Integer includeNumber) {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM tb_lotto_number WHERE 1=1");
		List<Object> params = new ArrayList<>();
		if (startPostgame != null) { sb.append(" AND postgame >= ?"); params.add(startPostgame); }
		if (endPostgame != null) { sb.append(" AND postgame <= ?"); params.add(endPostgame); }
		if (includeNumber != null) {
			sb.append(" AND (num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?)");
			for (int i=0;i<7;i++) params.add(includeNumber);
		}
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 PreparedStatement ps = conn.prepareStatement(sb.toString())) {
			bind(ps, params);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 필터링 + 페이징 조회 + 정렬
	public List<LottoNumber> getLottoHistory(Integer startPostgame, Integer endPostgame, Integer includeNumber, int offset, int limit, String sortCol, String sortDir) {
		List<LottoNumber> list = new ArrayList<>();
		String orderBy = resolveOrderBy(sortCol, sortDir);
		StringBuilder sb = new StringBuilder(
			"SELECT postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount FROM tb_lotto_number WHERE 1=1");
		List<Object> params = new ArrayList<>();
		if (startPostgame != null) { sb.append(" AND postgame >= ?"); params.add(startPostgame); }
		if (endPostgame != null) { sb.append(" AND postgame <= ?"); params.add(endPostgame); }
		if (includeNumber != null) {
			sb.append(" AND (num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?)");
			for (int i=0;i<7;i++) params.add(includeNumber);
		}
		sb.append(" ").append(orderBy).append(" LIMIT ? OFFSET ?");
		params.add(limit);
		params.add(offset);
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 PreparedStatement ps = conn.prepareStatement(sb.toString())) {
			bind(ps, params);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) list.add(mapRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private String resolveOrderBy(String col, String dir) {
		String direction = "DESC";
		if ("asc".equalsIgnoreCase(dir)) direction = "ASC";
		String column;
		if ("postgame".equalsIgnoreCase(col)) column = "postgame";
		else if ("firstprize".equalsIgnoreCase(col)) column = "firstprize";
		else if ("firstprizecount".equalsIgnoreCase(col)) column = "firstprizecount";
		else column = "postgame"; // default
		return "ORDER BY " + column + " " + direction;
	}

	// 필터된 전체 행에 대한 번호 빈도(1~45), 보너스 포함
	public Map<Integer, Integer> countFrequencies(Integer startPostgame, Integer endPostgame, Integer includeNumber) {
		Map<Integer, Integer> freq = new LinkedHashMap<>();
		for (int i=1;i<=45;i++) freq.put(i, 0);
		StringBuilder sb = new StringBuilder("SELECT num1,num2,num3,num4,num5,num6,bonusnum FROM tb_lotto_number WHERE 1=1");
		List<Object> params = new ArrayList<>();
		if (startPostgame != null) { sb.append(" AND postgame >= ?"); params.add(startPostgame); }
		if (endPostgame != null) { sb.append(" AND postgame <= ?"); params.add(endPostgame); }
		if (includeNumber != null) {
			sb.append(" AND (num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?)");
			for (int i=0;i<7;i++) params.add(includeNumber);
		}
		try (Connection conn = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());
			 PreparedStatement ps = conn.prepareStatement(sb.toString())) {
			bind(ps, params);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					increment(freq, rs.getInt(1));
					increment(freq, rs.getInt(2));
					increment(freq, rs.getInt(3));
					increment(freq, rs.getInt(4));
					increment(freq, rs.getInt(5));
					increment(freq, rs.getInt(6));
					increment(freq, rs.getInt(7));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return freq;
	}

	private void increment(Map<Integer,Integer> map, int key) {
		if (key >= 1 && key <= 45) map.put(key, map.get(key) + 1);
	}

	private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			ps.setObject(i + 1, params.get(i));
		}
	}

	private LottoNumber mapRow(ResultSet rs) throws SQLException {
		LottoNumber n = new LottoNumber();
		n.setPostgame(rs.getInt("postgame"));
		n.setNum1(rs.getInt("num1"));
		n.setNum2(rs.getInt("num2"));
		n.setNum3(rs.getInt("num3"));
		n.setNum4(rs.getInt("num4"));
		n.setNum5(rs.getInt("num5"));
		n.setNum6(rs.getInt("num6"));
		n.setBonusnum(rs.getInt("bonusnum"));
		n.setFirstprize(rs.getLong("firstprize"));
		n.setFirstprizecount(rs.getLong("firstprizecount"));
		return n;
	}
}
