<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.lottoweb.dao.LottoDAO" %>
<%@ page import="com.lottoweb.model.LottoNumber" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.*" %>
<%
    request.setCharacterEncoding("UTF-8");
    // Read params
    String pPage = request.getParameter("page");
    String pSize = request.getParameter("size");
    String pStart = request.getParameter("start");
    String pEnd = request.getParameter("end");
    String pNumber = request.getParameter("number");
    String pSort = request.getParameter("sort");
    String pDir = request.getParameter("dir");
    String pGroupStart = request.getParameter("groupStart");
    String export = request.getParameter("export");

    int pageNo = 1; try { if (pPage != null) pageNo = Integer.parseInt(pPage); } catch (Exception ignore) {}
    int size = 10; try { if (pSize != null) size = Integer.parseInt(pSize); } catch (Exception ignore) {}
    int groupStart = 1; try { if (pGroupStart != null) groupStart = Integer.parseInt(pGroupStart); } catch (Exception ignore) {}
    Integer start = null; try { if (pStart != null && !pStart.isEmpty()) start = Integer.valueOf(pStart); } catch (Exception ignore) {}
    Integer end = null; try { if (pEnd != null && !pEnd.isEmpty()) end = Integer.valueOf(pEnd); } catch (Exception ignore) {}
    Integer number = null; try { if (pNumber != null && !pNumber.isEmpty()) number = Integer.valueOf(pNumber); } catch (Exception ignore) {}
    String sort = (pSort == null || pSort.isEmpty()) ? null : pSort;
    String dir = (pDir == null || pDir.isEmpty()) ? null : pDir;

    LottoDAO dao = new LottoDAO();

    if ("csv".equalsIgnoreCase(export)) {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=lotto_history.csv");
        java.io.PrintWriter outCsv = response.getWriter();
        List<LottoNumber> all = dao.getLottoHistory(start, end, number, 0, Integer.MAX_VALUE / 2, sort, dir);
        outCsv.println("postgame,num1,num2,num3,num4,num5,num6,bonusnum,firstprize,firstprizecount");
        for (LottoNumber n : all) {
            outCsv.printf(java.util.Locale.US, "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
                n.getPostgame(), n.getNum1(), n.getNum2(), n.getNum3(), n.getNum4(), n.getNum5(), n.getNum6(),
                n.getBonusnum(), n.getFirstprize(), n.getFirstprizecount());
        }
        outCsv.flush();
        return;
    }

    if (pageNo <= 0) pageNo = 1; if (size <= 0) size = 10;
    int totalCount = dao.countLottoHistory(start, end, number);
    int totalPages = (int) Math.ceil(totalCount / (double) size);
    
    // 그룹 시작 페이지 계산: groupStart가 없으면 현재 페이지 기준으로 계산
    if (groupStart <= 0 || groupStart > totalPages) {
        groupStart = ((pageNo - 1) / 5) * 5 + 1;
    }
    // 현재 페이지가 현재 그룹 범위를 벗어나면 그룹 재조정
    if (pageNo < groupStart || pageNo >= groupStart + 5) {
        groupStart = ((pageNo - 1) / 5) * 5 + 1;
    }
    
    int offset = (pageNo - 1) * size;
    List<LottoNumber> items = dao.getLottoHistory(start, end, number, offset, size, sort, dir);
    Map<Integer, Integer> freq = dao.countFrequencies(start, end, number);

    // 최다/최소 빈도 번호 계산
    int mostFreq = -1, mostFreqCount = -1, leastFreq = -1, leastFreqCount = Integer.MAX_VALUE;
    for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
        if (e.getValue() > mostFreqCount) { mostFreqCount = e.getValue(); mostFreq = e.getKey(); }
        if (e.getValue() < leastFreqCount && e.getValue() > 0) { leastFreqCount = e.getValue(); leastFreq = e.getKey(); }
    }
    // 행운의 번호: KST 기준 날짜로 시드 → 자정마다 변경
    LocalDate todayKst = LocalDate.now(ZoneId.of("Asia/Seoul"));
    long seed = todayKst.toEpochDay();
    Random rand = new Random(seed);
    int luckyNum = rand.nextInt(45) + 1;

    request.setAttribute("items", items);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("currentPage", pageNo);
    request.setAttribute("size", size);
    request.setAttribute("totalCount", totalCount);
    request.setAttribute("start", start);
    request.setAttribute("end", end);
    request.setAttribute("number", number);
    request.setAttribute("sort", sort);
    request.setAttribute("dir", dir);
    request.setAttribute("groupStart", groupStart);
    request.setAttribute("freq", freq);
    request.setAttribute("mostFreq", mostFreq);
    request.setAttribute("mostFreqCount", mostFreqCount);
    request.setAttribute("leastFreq", leastFreq);
    request.setAttribute("leastFreqCount", leastFreqCount);
    request.setAttribute("luckyNum", luckyNum);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>로또 당첨 이력</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Pretendard:wght@400;600;700&family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
<style>
	:root {
		--bg: #0f1226;
		--card: #171a2f;
		--muted: #a5b0d6;
		--text: #e9ecff;
		--accent1: #7c6cff;
		--accent2: #00e0b8;
		--accent3: #ff9d9d;
		--shadow: 0 10px 30px rgba(0,0,0,.35);
		--radius: 18px;
	}
	* { box-sizing: border-box; }
	body { 
		margin: 0; 
		font-family: 'Pretendard', 'Inter', system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans KR", sans-serif; 
		background: radial-gradient(1200px 800px at 20% -10%, rgba(124,108,255,.25), transparent 60%),
					radial-gradient(1000px 700px at 120% 10%, rgba(0,224,184,.18), transparent 50%),
					var(--bg);
		color: var(--text);
	}
	.container { max-width: 1200px; margin: 56px auto 80px; padding: 0 20px; }
	h1 {
		margin: 0 0 32px; font-size: 48px; letter-spacing: -1.2px; text-align: center; 
		background: linear-gradient(90deg, var(--accent1), var(--accent2));
		-webkit-background-clip: text; background-clip: text; color: transparent;
		text-shadow: 0 8px 30px rgba(124,108,255,.25);
	}
	h2 { margin: 32px 0 20px; color: var(--muted); font-weight: 600; font-size: 24px; }
	
	.card { background: var(--card); border: 1px solid rgba(255,255,255,.06); border-radius: var(--radius); box-shadow: var(--shadow); overflow: hidden; margin-bottom: 32px; }
	table { width: 100%; border-collapse: separate; border-spacing: 0; table-layout: fixed; }
	thead th { font-weight: 700; color: #cfd6ff; background: rgba(255,255,255,.03); padding: 16px 14px; text-align: center; }
	thead th a { color: #cfd6ff; text-decoration: none; transition: color .2s; }
	thead th a:hover { color: var(--accent2); }
	th:nth-child(1), td:nth-child(1) { width: 8%; } /* 회차 */
	th:nth-child(2), td:nth-child(2) { width: 45%; text-align: center; } /* 번호 */
	th:nth-child(3), td:nth-child(3) { width: 23%; text-align: right; } /* 1등 금액 */
	th:nth-child(4), td:nth-child(4) { width: 24%; text-align: right; } /* 1등 당첨자 수 */
	td { padding: 16px 14px; text-align: center; border-top: 1px solid rgba(255,255,255,.06); }
	tbody tr:hover { background: rgba(255,255,255,.02); }
	
    /* Lotto color balls */
    .badge { display:inline-flex; align-items:center; justify-content:center; width:36px; height:36px; border-radius:50%; margin:3px; font-weight:900; font-size:14px; color:#fff; text-shadow:0 2px 0 rgba(0,0,0,.15); border:2px solid rgba(255,255,255,.9); box-shadow:0 4px 12px rgba(0,0,0,.18); }
    .badge.yellow { background:#ffc400; }
    .badge.blue { background:#6cc7ff; }
    .badge.red { background:#ff7b87; }
    .badge.gray { background:#b8bcc4; }
    .badge.green { background:#94d145; }
    .badge.bonus { outline: 2px dashed rgba(0,0,0,.12); outline-offset:2px; }
	
	.controls { margin-bottom: 24px; display: flex; gap: 12px; align-items: center; flex-wrap: wrap; padding: 20px; background: var(--card); border-radius: var(--radius); border: 1px solid rgba(255,255,255,.06); }
	.controls form { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; flex: 1; }
	.controls label { color: var(--muted); font-weight: 600; font-size: 14px; }
	.controls input[type="number"], .controls select { 
		padding: 10px 14px; border: 1px solid rgba(255,255,255,.1); border-radius: 10px; 
		background: rgba(255,255,255,.04); color: var(--text); font-size: 14px;
		transition: border-color .2s, background .2s;
	}
	.controls input[type="number"]:focus, .controls select:focus { 
		outline: none; border-color: var(--accent1); background: rgba(255,255,255,.06);
	}
	.controls button[type="submit"] {
		padding: 10px 20px; border: 0; border-radius: 10px; cursor: pointer; font-weight: 700;
		background: linear-gradient(135deg, var(--accent2), #66ffd8); color: #0a0d1f;
		box-shadow: 0 6px 16px rgba(0,224,184,.25);
		transition: transform .12s, box-shadow .2s;
	}
	.controls button[type="submit"]:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0,224,184,.35); }
	.controls button[type="submit"]:active { transform: translateY(0); }
	
	.pagination { margin-top: 24px; text-align: center; }
	.pagination a, .pagination span { 
		display: inline-block; padding: 10px 16px; margin: 0 4px; 
		border-radius: 10px; text-decoration: none; font-weight: 600; font-size: 14px;
		transition: all .2s;
	}
	.pagination a { 
		color: var(--text); background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.1);
	}
	.pagination a:hover { 
		background: rgba(255,255,255,.1); transform: translateY(-2px);
	}
	.pagination .active { 
		background: linear-gradient(135deg, var(--accent1), #a997ff); 
		color: #0a0d1f; border: 0; box-shadow: 0 4px 12px rgba(124,108,255,.3);
	}
	
	.heatmap { display: grid; grid-template-columns: repeat(9, 1fr); gap: 8px; margin-top: 24px; }
	.cell { 
		height: 60px; border-radius: 10px; display: flex; flex-direction: column; 
		align-items: center; justify-content: center; font-weight: 700; font-size: 14px;
		color: var(--text); border: 1px solid rgba(255,255,255,.1);
		transition: transform .2s, box-shadow .2s;
	}
	.cell:hover { transform: scale(1.05); box-shadow: 0 4px 12px rgba(30,144,255,.3); }
	.heatmap-info { 
		margin-top: 24px; padding: 24px; background: var(--card); border-radius: var(--radius); 
		border: 1px solid rgba(255,255,255,.06); box-shadow: var(--shadow);
	}
	.heatmap-info h3 { margin-top: 0; margin-bottom: 16px; color: var(--text); font-size: 20px; }
	.heatmap-info .stat { margin: 12px 0; font-weight: 600; color: var(--muted); font-size: 16px; }
	.heatmap-info .stat strong { color: var(--accent2); }
	.heatmap-info .lucky { color: var(--accent3); font-size: 20px; font-weight: 700; }
	
	.btn { 
		display: inline-flex; align-items: center; justify-content: center; gap: 10px; 
		padding: 14px 24px; border: 0; border-radius: 14px; cursor: pointer; font-weight: 700; 
		color: #0a0d1f; background: linear-gradient(135deg, var(--accent2), #66ffd8); 
		box-shadow: 0 10px 20px rgba(0,224,184,.25); text-decoration: none;
		transform: translateY(0); transition: transform .12s, box-shadow .2s, filter .2s;
	}
	.btn:hover { transform: translateY(-2px); filter: brightness(1.02); box-shadow: 0 14px 28px rgba(0,224,184,.35); }
	.btn:active { transform: translateY(0); box-shadow: 0 10px 16px rgba(0,224,184,.25); }
</style>
</head>
<body>
<div class="container">
<h1>로또 당첨 이력</h1>

<div class="controls">
	<form method="get" action="/lotto_web/lotto/lottoHistory.jsp">
		<label>회차 시작 <input type="number" name="start" value="${start}"></label>
		<label>회차 끝 <input type="number" name="end" value="${end}"></label>
		<label>번호 포함 <input type="number" name="number" min="1" max="45" value="${number}"></label>
		<label>페이지 크기
			<select name="size">
				<option value="10" ${size==10? 'selected' : ''}>10</option>
				<option value="20" ${size==20? 'selected' : ''}>20</option>
				<option value="50" ${size==50? 'selected' : ''}>50</option>
			</select>
		</label>
		<button type="submit">적용</button>
		<button type="submit" name="export" value="csv">CSV 다운로드</button>
	</form>
	<div style="color: var(--muted); font-weight: 600; font-size: 14px;">총 <strong style="color: var(--accent2);">${totalCount}</strong>건</div>
</div>

<c:set var="baseUrl" value="/lotto_web/lotto/lottoHistory.jsp"/>

<div class="card">
<table>
	<thead>
		<tr>
			<th class="sortable" data-sort="postgame">
				<a href="javascript:void(0)" onclick="sortTable('postgame')">회차 <span id="sort-postgame">▼</span></a>
			</th>
			<th>번호</th>
			<th class="sortable" data-sort="firstprize">
				<a href="javascript:void(0)" onclick="sortTable('firstprize')">1등 금액 <span id="sort-firstprize">▼</span></a>
			</th>
			<th class="sortable" data-sort="firstprizecount">
				<a href="javascript:void(0)" onclick="sortTable('firstprizecount')">1등 당첨자 수 <span id="sort-firstprizecount">▼</span></a>
			</th>
		</tr>
	</thead>
	<tbody id="dataTableBody">
		<c:forEach var="n" items="${items}">
			<tr data-postgame="${n.postgame}" data-firstprize="${n.firstprize}" data-firstprizecount="${n.firstprizecount}">
				<td>${n.postgame}</td>
                <td>
                    <c:set var="a1" value="${n.num1}"/>
                    <c:set var="a2" value="${n.num2}"/>
                    <c:set var="a3" value="${n.num3}"/>
                    <c:set var="a4" value="${n.num4}"/>
                    <c:set var="a5" value="${n.num5}"/>
                    <c:set var="a6" value="${n.num6}"/>
                    <c:set var="b" value="${n.bonusnum}"/>

                    <c:set var="k1" value="${a1 <= 10 ? 'yellow' : (a1 <= 20 ? 'blue' : (a1 <= 30 ? 'red' : (a1 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="k2" value="${a2 <= 10 ? 'yellow' : (a2 <= 20 ? 'blue' : (a2 <= 30 ? 'red' : (a2 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="k3" value="${a3 <= 10 ? 'yellow' : (a3 <= 20 ? 'blue' : (a3 <= 30 ? 'red' : (a3 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="k4" value="${a4 <= 10 ? 'yellow' : (a4 <= 20 ? 'blue' : (a4 <= 30 ? 'red' : (a4 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="k5" value="${a5 <= 10 ? 'yellow' : (a5 <= 20 ? 'blue' : (a5 <= 30 ? 'red' : (a5 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="k6" value="${a6 <= 10 ? 'yellow' : (a6 <= 20 ? 'blue' : (a6 <= 30 ? 'red' : (a6 <= 40 ? 'gray' : 'green')))}"/>
                    <c:set var="kb" value="${b <= 10 ? 'yellow' : (b <= 20 ? 'blue' : (b <= 30 ? 'red' : (b <= 40 ? 'gray' : 'green')))}"/>

                    <span class="badge ${k1}">${a1}</span>
                    <span class="badge ${k2}">${a2}</span>
                    <span class="badge ${k3}">${a3}</span>
                    <span class="badge ${k4}">${a4}</span>
                    <span class="badge ${k5}">${a5}</span>
                    <span class="badge ${k6}">${a6}</span>
                    <span>+</span>
                    <span class="badge ${kb} bonus">${b}</span>
                </td>
				<td data-value="${n.firstprize}"><fmt:formatNumber value="${n.firstprize}" type="number"/> 원</td>
				<td data-value="${n.firstprizecount}"><fmt:formatNumber value="${n.firstprizecount}" type="number"/> 명</td>
			</tr>
		</c:forEach>
		<c:if test="${empty items}">
			<tr><td colspan="4">데이터가 없습니다.</td></tr>
		</c:if>
	</tbody>
</table>
</div>

<div class="pagination">
	<c:set var="cp" value="${currentPage}"/>
	<c:set var="tp" value="${totalPages}"/>
	<c:set var="gs" value="${groupStart}"/>
	<c:choose>
		<c:when test="${tp > 1}">
			<c:set var="endPage" value="${gs + 4 > tp ? tp : gs + 4}"/>
			<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=1&groupStart=1">처음</a>
			<c:choose>
				<c:when test="${gs > 1}">
					<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${gs - 5}&groupStart=${gs - 5}">이전</a>
				</c:when>
				<c:otherwise>
					<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${cp-1 > 0 ? cp-1 : 1}&groupStart=${gs}">이전</a>
				</c:otherwise>
			</c:choose>
			<c:forEach var="p" begin="${gs}" end="${endPage}">
				<c:choose>
					<c:when test="${p == cp}"><span class="active">${p}</span></c:when>
					<c:otherwise><a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${p}&groupStart=${gs}">${p}</a></c:otherwise>
				</c:choose>
			</c:forEach>
			<c:choose>
				<c:when test="${endPage < tp}">
					<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${endPage + 1}&groupStart=${endPage + 1}">다음</a>
				</c:when>
				<c:otherwise>
					<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${cp+1 <= tp ? cp+1 : tp}&groupStart=${gs}">다음</a>
				</c:otherwise>
			</c:choose>
			<c:set var="lastGroupStart" value="${((tp-1) / 5) * 5 + 1}"/>
			<a href="${baseUrl}?start=${start}&end=${end}&number=${number}&size=${size}&sort=${sort}&dir=${dir}&page=${tp}&groupStart=${lastGroupStart}">마지막</a>
		</c:when>
		<c:otherwise>
			<span class="active">1</span>
		</c:otherwise>
	</c:choose>
</div>

<c:if test="${not empty freq}">
	<h2>번호 빈도 히트맵 (필터 반영)</h2>
	<div class="card">
		<c:set var="max" value="0"/>
		<c:forEach var="entry" items="${freq}">
			<c:if test="${entry.value > max}"><c:set var="max" value="${entry.value}"/></c:if>
		</c:forEach>
		<div class="heatmap">
			<c:forEach var="entry" items="${freq}">
				<c:set var="val" value="${entry.value}"/>
				<c:set var="alpha" value="${max > 0 ? (0.15 + (val * 0.85) / max) : 0.15}"/>
				<div class="cell" style="background: rgba(30,144,255, ${alpha}); padding: 12px;">
					<span>${entry.key}번</span>
					<span style="font-size: 11px; opacity: 0.85;">${val}개</span>
				</div>
			</c:forEach>
		</div>
		<div class="heatmap-info" style="margin-top: 24px; background: transparent; border: 0; box-shadow: none; padding: 0;">
		<h3 style="margin-bottom: 16px;">번호 분석</h3>
		<c:if test="${mostFreq > 0}">
			<div class="stat">가장 많이 나온 번호: <strong>${mostFreq}번</strong> (${mostFreqCount}회)</div>
		</c:if>
		<c:if test="${leastFreq > 0}">
			<div class="stat">가장 적게 나온 번호: <strong>${leastFreq}번</strong> (${leastFreqCount}회)</div>
		</c:if>
		<div class="stat lucky">🍀 오늘의 행운의 번호: <strong>${luckyNum}번</strong></div>
		</div>
	</div>
</c:if>

<div style="margin-top: 32px; text-align: center;">
	<a href="/lotto_web/index.jsp" class="btn">메인으로</a>
</div>
</div>

<script>
	let currentSort = { column: null, direction: 'asc' };
	
	function sortTable(column) {
		const tbody = document.getElementById('dataTableBody');
		const rows = Array.from(tbody.querySelectorAll('tr'));
		
		// 빈 데이터 행 제외
		const dataRows = rows.filter(row => row.getAttribute('data-postgame') !== null);
		
		if (dataRows.length === 0) return;
		
		// 정렬 방향 결정
		if (currentSort.column === column) {
			currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
		} else {
			currentSort.column = column;
			currentSort.direction = 'asc';
		}
		
		// 정렬 실행
		dataRows.sort((a, b) => {
			let valueA, valueB;
			
			if (column === 'postgame') {
				valueA = parseInt(a.getAttribute('data-postgame'));
				valueB = parseInt(b.getAttribute('data-postgame'));
			} else if (column === 'firstprize') {
				valueA = parseInt(a.getAttribute('data-firstprize'));
				valueB = parseInt(b.getAttribute('data-firstprize'));
			} else if (column === 'firstprizecount') {
				valueA = parseInt(a.getAttribute('data-firstprizecount'));
				valueB = parseInt(b.getAttribute('data-firstprizecount'));
			}
			
			if (currentSort.direction === 'asc') {
				return valueA - valueB;
			} else {
				return valueB - valueA;
			}
		});
		
		// 정렬된 순서로 DOM 재배치
		dataRows.forEach(row => tbody.appendChild(row));
		
		// 정렬 표시 업데이트
		document.querySelectorAll('#sort-postgame, #sort-firstprize, #sort-firstprizecount').forEach(span => {
			span.textContent = '▼';
		});
		
		const sortSpan = document.getElementById('sort-' + column);
		if (sortSpan) {
			sortSpan.textContent = currentSort.direction === 'asc' ? '▲' : '▼';
		}
	}
</script>

</body>
</html>