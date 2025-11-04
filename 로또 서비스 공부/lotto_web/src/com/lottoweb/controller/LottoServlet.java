package com.lottoweb.controller;
import javax.servlet.*;
import javax.servlet.http.*;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;
import com.lottoweb.util.LuckyNumber;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class LottoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 최신 로또 번호 조회
        LottoDAO lottoDAO = new LottoDAO();
        LottoNumber lottoNumber = lottoDAO.getLottoNumber();
        request.setAttribute("lottoNumber", lottoNumber);

        // 기본/행운번호 포함 추첨
        boolean useLucky = request.getParameter("useLucky") != null;
        Map<String, Object> lottoResult;
        if (useLucky) {
            int lucky = LuckyNumber.todayKST();
            lottoResult = lottoDAO.drawLottoNumbersIncluding(lucky);
            request.setAttribute("usedLucky", true);
            request.setAttribute("luckyNumber", lucky);
        } else {
            lottoResult = lottoDAO.drawLottoNumbers();
            request.setAttribute("usedLucky", false);
        }

        @SuppressWarnings("unchecked")
        List<Integer> drawnNumbers = (List<Integer>) lottoResult.get("drawnNumbers");
        int bonusNumber = (int) lottoResult.get("bonusNumber");

        request.setAttribute("drawnNumbers", drawnNumbers);
        request.setAttribute("bonusNumber", bonusNumber);

        request.getRequestDispatcher("/lotto/lottoResult.jsp").forward(request, response);
    }
}