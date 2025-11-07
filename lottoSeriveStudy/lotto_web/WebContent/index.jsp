<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.lottoweb.dao.LottoDAO" %>
<%@ page import="com.lottoweb.model.LottoNumber" %>
<%
// latest가 없으면 직접 조회
if (request.getAttribute("latest") == null) {
    LottoDAO dao = new LottoDAO();
    LottoNumber latest = dao.getLottoNumber();
    request.setAttribute("latest", latest);
    // 최근 N개 회차 목록(카드 슬라이드 용)
    java.util.List<com.lottoweb.model.LottoNumber> recentList = dao.getLottoHistory(null, null, null, 0, 5, "postgame", "desc");
    request.setAttribute("recentList", recentList);
}
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로또 웹 애플리케이션</title>
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
        .container { max-width: 1100px; margin: 56px auto 80px; padding: 0 20px; }
        h1 {
            margin: 0 0 16px; font-size: 48px; letter-spacing: -1.2px; text-align: center; 
            background: linear-gradient(90deg, var(--accent1), var(--accent2));
            -webkit-background-clip: text; background-clip: text; color: transparent;
            text-shadow: 0 8px 30px rgba(124,108,255,.25);
        }
        h2 { margin: 8px 0 24px; text-align: center; color: var(--muted); font-weight: 600; }

        .card { background: var(--card); border: 1px solid rgba(255,255,255,.06); border-radius: var(--radius); box-shadow: var(--shadow); overflow: hidden; padding: 28px; }
        .latest-meta { display:flex; align-items:center; justify-content:space-between; color: var(--muted); font-weight: 600; margin-bottom: 12px; }
        .latest-meta .pill { padding: 6px 12px; border-radius: 999px; background: rgba(255,255,255,.06); color: var(--text); font-weight: 700; }
        .lotto-numbers { display:flex; align-items:center; justify-content:center; flex-wrap:wrap; gap: 12px; margin-top: 8px; }
        .group { display:flex; flex-direction:column; align-items:center; }
        .balls-row { display:flex; align-items:center; gap:12px; }
        .caption { margin-top:8px; color: var(--muted); font-weight:700; }
        .light-card { background:#f7f8fb; border:1px solid rgba(0,0,0,.05); border-radius: 22px; padding: 18px; box-shadow: 0 8px 24px rgba(0,0,0,.08) inset; }
        .ball { width: 54px; height: 54px; border-radius: 50%; display:flex; align-items:center; justify-content:center; font-weight: 900; font-size: 20px; color:#ffffff; text-shadow: 0 2px 0 rgba(0,0,0,.15); box-shadow: 0 8px 16px rgba(0,0,0,.2); border: 2px solid rgba(255,255,255,.9); }
        .ball.yellow { background:#ffc400; }
        .ball.blue { background:#6cc7ff; }
        .ball.red { background:#ff7b87; }
        .ball.gray { background:#b8bcc4; }
        .ball.green { background:#94d145; }
        .ball.bonus { outline: 3px dashed rgba(0,0,0,.12); outline-offset: 3px; }
        .plus { color: var(--muted); font-weight: 700; padding: 0 6px; }
        .stats { margin-top: 18px; display:grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 12px; }
        .stat { background: rgba(255,255,255,.04); border: 1px solid rgba(255,255,255,.06); border-radius: 12px; padding: 14px; text-align:center; font-weight:700; }
        .carousel { margin-top: 28px; }
        .cards { display:flex; gap: 16px; overflow-x: auto; padding-bottom: 8px; scrollbar-width: thin; }
        .mini-card { min-width: 300px; background: var(--card); border: 1px solid rgba(255,255,255,.06); border-radius: 14px; box-shadow: var(--shadow); padding: 18px; }
        .mini-head { display:flex; align-items:center; justify-content:space-between; color: var(--muted); font-weight: 700; margin-bottom: 8px; }
        .mini-numbers { display:flex; align-items:center; gap: 8px; flex-wrap: wrap; }
        .mini-ball { width: 36px; height: 36px; border-radius: 50%; display:flex; align-items:center; justify-content:center; font-weight: 900; font-size: 14px; color:#fff; text-shadow: 0 2px 0 rgba(0,0,0,.15); box-shadow: 0 6px 12px rgba(0,0,0,.18); border: 2px solid rgba(255,255,255,.9); }
        .mini-yellow { background:#ffc400; }
        .mini-blue { background:#6cc7ff; }
        .mini-red { background:#ff7b87; }
        .mini-gray { background:#b8bcc4; }
        .mini-green { background:#94d145; }
        .mini-bonus { outline: 3px dashed rgba(0,0,0,.12); outline-offset: 2px; }

        .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 18px; margin-top: 32px; justify-items: center; }
        .btn { 
            display: inline-flex; align-items: center; justify-content: center; gap: 10px; 
            padding: 16px 28px; border: 0; border-radius: 14px; cursor: pointer; font-weight: 700; 
            color: #0a0d1f; background: linear-gradient(135deg, var(--accent2), #66ffd8); 
            box-shadow: 0 10px 20px rgba(0,224,184,.25), inset 0 -2px 0 rgba(0,0,0,.15);
            transform: translateY(0); transition: transform .12s ease, box-shadow .2s ease, filter .2s ease;
        }
        .btn:hover { transform: translateY(-2px); filter: brightness(1.02); box-shadow: 0 14px 28px rgba(0,224,184,.35); }
        .btn:active { transform: translateY(0); box-shadow: 0 10px 16px rgba(0,224,184,.25); }
        .btn.secondary { background: linear-gradient(135deg, var(--accent1), #a997ff); color: #0a0d1f; box-shadow: 0 10px 20px rgba(124,108,255,.28), inset 0 -2px 0 rgba(0,0,0,.15); }
        .btn.secondary:hover { box-shadow: 0 14px 28px rgba(124,108,255,.38); }

        .kicker { margin-top: 34px; text-align: center; font-weight: 700; letter-spacing: .05em; color: #c8d0ff; }
    </style>
</head>
<body>
<div class="container">
    <h1>로또 웹 애플리케이션</h1>
    <h2>최근 로또 당첨 번호</h2>

    <div class="card">
        <c:choose>
            <c:when test="${not empty latest}">
                <div class="latest-meta">
                    <div>최근 회차: <span class="pill">${latest.postgame} 회</span></div>
                </div>
                <div class="light-card">
                    <div class="lotto-numbers">
                        <div class="group">
                        <div class="balls-row">
                        <c:set var="n1" value="${latest.num1}"/>
                        <c:set var="n2" value="${latest.num2}"/>
                        <c:set var="n3" value="${latest.num3}"/>
                        <c:set var="n4" value="${latest.num4}"/>
                        <c:set var="n5" value="${latest.num5}"/>
                        <c:set var="n6" value="${latest.num6}"/>

                        <c:set var="c1" value="${n1 <= 10 ? 'yellow' : (n1 <= 20 ? 'blue' : (n1 <= 30 ? 'red' : (n1 <= 40 ? 'gray' : 'green')))}"/>
                        <c:set var="c2" value="${n2 <= 10 ? 'yellow' : (n2 <= 20 ? 'blue' : (n2 <= 30 ? 'red' : (n2 <= 40 ? 'gray' : 'green')))}"/>
                        <c:set var="c3" value="${n3 <= 10 ? 'yellow' : (n3 <= 20 ? 'blue' : (n3 <= 30 ? 'red' : (n3 <= 40 ? 'gray' : 'green')))}"/>
                        <c:set var="c4" value="${n4 <= 10 ? 'yellow' : (n4 <= 20 ? 'blue' : (n4 <= 30 ? 'red' : (n4 <= 40 ? 'gray' : 'green')))}"/>
                        <c:set var="c5" value="${n5 <= 10 ? 'yellow' : (n5 <= 20 ? 'blue' : (n5 <= 30 ? 'red' : (n5 <= 40 ? 'gray' : 'green')))}"/>
                        <c:set var="c6" value="${n6 <= 10 ? 'yellow' : (n6 <= 20 ? 'blue' : (n6 <= 30 ? 'red' : (n6 <= 40 ? 'gray' : 'green')))}"/>

                        <div class="ball ${c1}">${n1}</div>
                        <div class="ball ${c2}">${n2}</div>
                        <div class="ball ${c3}">${n3}</div>
                        <div class="ball ${c4}">${n4}</div>
                        <div class="ball ${c5}">${n5}</div>
                        <div class="ball ${c6}">${n6}</div>
                        </div>
                        <div class="caption">당첨번호</div>
                        </div>
                        <span class="plus">+</span>

                        <c:set var="bn" value="${latest.bonusnum}"/>
                        <c:set var="bc" value="${bn <= 10 ? 'yellow' : (bn <= 20 ? 'blue' : (bn <= 30 ? 'red' : (bn <= 40 ? 'gray' : 'green')))}"/>
                        <div class="group">
                          <div class="balls-row">
                            <div class="ball ${bc} bonus">${bn}</div>
                          </div>
                          <div class="caption">보너스</div>
                        </div>
                    </div>
                </div>
                <div class="stats">
                    <div class="stat">1등 당첨금액<br/><span style="font-size:18px;"><fmt:formatNumber value="${latest.firstprize}" type="number"/> 원</span></div>
                    <div class="stat">1등 당첨자 수<br/><span style="font-size:18px;"><fmt:formatNumber value="${latest.firstprizecount}" type="number"/> 명</span></div>
                </div>
            </c:when>
            <c:otherwise>
                <div style="color:var(--muted); padding:22px; text-align:center;">과거 당첨 번호가 없습니다. 추가 기능을 확인해주세요.</div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="carousel">
        <h2>최근 5회차</h2>
        <div class="cards">
            <c:forEach var="it" items="${recentList}">
                <div class="mini-card">
                    <div class="mini-head">
                        <span>${it.postgame} 회</span>
                        <span><fmt:formatNumber value="${it.firstprize}" type="number"/> 원</span>
                    </div>
                    <div class="mini-numbers">
                        <c:set var="n1" value="${it.num1}"/>
                        <c:set var="n2" value="${it.num2}"/>
                        <c:set var="n3" value="${it.num3}"/>
                        <c:set var="n4" value="${it.num4}"/>
                        <c:set var="n5" value="${it.num5}"/>
                        <c:set var="n6" value="${it.num6}"/>

                        <c:set var="c1" value="${n1 <= 10 ? 'mini-yellow' : (n1 <= 20 ? 'mini-blue' : (n1 <= 30 ? 'mini-red' : (n1 <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <c:set var="c2" value="${n2 <= 10 ? 'mini-yellow' : (n2 <= 20 ? 'mini-blue' : (n2 <= 30 ? 'mini-red' : (n2 <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <c:set var="c3" value="${n3 <= 10 ? 'mini-yellow' : (n3 <= 20 ? 'mini-blue' : (n3 <= 30 ? 'mini-red' : (n3 <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <c:set var="c4" value="${n4 <= 10 ? 'mini-yellow' : (n4 <= 20 ? 'mini-blue' : (n4 <= 30 ? 'mini-red' : (n4 <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <c:set var="c5" value="${n5 <= 10 ? 'mini-yellow' : (n5 <= 20 ? 'mini-blue' : (n5 <= 30 ? 'mini-red' : (n5 <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <c:set var="c6" value="${n6 <= 10 ? 'mini-yellow' : (n6 <= 20 ? 'mini-blue' : (n6 <= 30 ? 'mini-red' : (n6 <= 40 ? 'mini-gray' : 'mini-green')))}"/>

                        <div class="mini-ball ${c1}">${n1}</div>
                        <div class="mini-ball ${c2}">${n2}</div>
                        <div class="mini-ball ${c3}">${n3}</div>
                        <div class="mini-ball ${c4}">${n4}</div>
                        <div class="mini-ball ${c5}">${n5}</div>
                        <div class="mini-ball ${c6}">${n6}</div>
                        <span class="plus">+</span>
                        <c:set var="bv" value="${it.bonusnum}"/>
                        <c:set var="bcls" value="${bv <= 10 ? 'mini-yellow' : (bv <= 20 ? 'mini-blue' : (bv <= 30 ? 'mini-red' : (bv <= 40 ? 'mini-gray' : 'mini-green')))}"/>
                        <div class="mini-ball ${bcls} mini-bonus">${bv}</div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="kicker">추가 기능</div>
    <div class="grid">
        <button class="btn secondary" onclick="location.href='<%=request.getContextPath()%>/lotto';">로또 번호 추첨</button>
        <button class="btn" onclick="location.href='<%=request.getContextPath()%>/lotto/lottoHistory.jsp'">과거 당첨 번호 조회</button>
    </div>
</div>
</body>
</html>
