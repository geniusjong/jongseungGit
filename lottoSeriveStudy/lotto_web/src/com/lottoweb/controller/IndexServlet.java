package com.lottoweb.controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;

public class IndexServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LottoDAO lottoDAO = new LottoDAO();
		LottoNumber latest = lottoDAO.getLottoNumber();
		request.setAttribute("latest", latest);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
}
