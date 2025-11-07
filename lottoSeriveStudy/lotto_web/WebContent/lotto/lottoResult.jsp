<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로또 결과</title>
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
        .container { max-width: 960px; margin: 56px auto 80px; padding: 0 20px; }
        h1 { margin: 0 0 8px; font-size: 44px; text-align: center; background: linear-gradient(90deg, var(--accent1), var(--accent2)); -webkit-background-clip: text; background-clip: text; color: transparent; text-shadow: 0 8px 30px rgba(124,108,255,.25); }
        h2 { margin: 6px 0 24px; text-align: center; color: var(--muted); font-weight: 600; }

        .card { background: var(--card); border: 1px solid rgba(255,255,255,.06); border-radius: var(--radius); box-shadow: var(--shadow); padding: 28px; }
        .lotto-numbers { display: flex; align-items: center; justify-content: center; gap: 12px; flex-wrap: wrap; margin-top: 8px; }
        .light-card { background:#f7f8fb; border:1px solid rgba(0,0,0,.05); border-radius: 22px; padding: 18px; box-shadow: 0 8px 24px rgba(0,0,0,.08) inset; }
        .ball { width: 54px; height: 54px; border-radius: 50%; display:flex; align-items:center; justify-content:center; font-weight: 900; font-size: 20px; color:#ffffff; text-shadow: 0 2px 0 rgba(0,0,0,.15); box-shadow: 0 8px 16px rgba(0,0,0,.2); border: 2px solid rgba(255,255,255,.9); }
        .ball.yellow { background:#ffc400; }
        .ball.blue { background:#6cc7ff; }
        .ball.red { background:#ff7b87; }
        .ball.gray { background:#b8bcc4; }
        .ball.green { background:#94d145; }
        .ball.bonus { outline: 3px dashed rgba(0,0,0,.12); outline-offset: 3px; }
        .plus { color: var(--muted); font-weight: 700; padding: 0 6px; }

        .button-container { margin-top: 34px; display:flex; gap:18px; justify-content:center; }
        .btn { display:inline-flex; align-items:center; justify-content:center; padding:14px 24px; border:0; border-radius: 14px; cursor:pointer; font-weight:700; color:#0a0d1f; background: linear-gradient(135deg, var(--accent2), #66ffd8); box-shadow: 0 10px 20px rgba(0,224,184,.25), inset 0 -2px 0 rgba(0,0,0,.15); transition: transform .12s ease, box-shadow .2s ease; }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 14px 28px rgba(0,224,184,.35); }
        .btn.secondary { background: linear-gradient(135deg, var(--accent1), #a997ff); box-shadow: 0 10px 20px rgba(124,108,255,.28), inset 0 -2px 0 rgba(0,0,0,.15); }
    </style>
</head>
<body>
<div class="container">
    <h1>로또 번호 추첨 결과</h1>
    <h2>추첨된 로또 번호</h2>

    <div class="card light-card">
        <div class="lotto-numbers">
            <c:forEach var="num" items="${drawnNumbers}">
                <c:set var="cls" value="${num <= 10 ? 'yellow' : (num <= 20 ? 'blue' : (num <= 30 ? 'red' : (num <= 40 ? 'gray' : 'green')))}"/>
                <div class="ball ${cls}">${num}</div>
            </c:forEach>
            <span class="plus">+</span>
            <c:set var="bcls" value="${bonusNumber <= 10 ? 'yellow' : (bonusNumber <= 20 ? 'blue' : (bonusNumber <= 30 ? 'red' : (bonusNumber <= 40 ? 'gray' : 'green')))}"/>
            <div class="ball ${bcls} bonus">${bonusNumber}</div>
        </div>
    </div>

    <div style="text-align:center; margin-top:10px; color: var(--muted); font-weight:700;">
        <c:if test="${usedLucky}">오늘의 행운번호(<span style="color:#ff9d9d;">${luckyNumber}</span>)를 포함해 추첨했습니다.</c:if>
    </div>

    <div class="button-container">
        <button class="btn secondary" onclick="location.href='<%=request.getContextPath()%>/index.jsp'">메인 화면으로</button>
        <button class="btn" onclick="location.href='<%=request.getContextPath()%>/lotto'">다시 추첨</button>
        <button class="btn" onclick="location.href='<%=request.getContextPath()%>/lotto?useLucky=1'">행운번호 포함해 뽑기</button>
    </div>
</div>
</body>
</html>
