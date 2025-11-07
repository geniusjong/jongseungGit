package com.lottoweb.controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;

public class HistoryServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int page = parseIntOrDefault(request.getParameter("page"), 1);
		int size = parseIntOrDefault(request.getParameter("size"), 20);
		Integer start = parseNullableInt(request.getParameter("start"));
		Integer end = parseNullableInt(request.getParameter("end"));
		Integer number = parseNullableInt(request.getParameter("number"));
		String sort = request.getParameter("sort"); // postgame | firstprize | firstprizecount
		String dir = request.getParameter("dir");   // asc | desc
		String export = request.getParameter("export");

		if (size <= 0) size = 20;
		if (page <= 0) page = 1;
		int offset = (page - 1) * size;

		LottoDAO dao = new LottoDAO();

		if ("csv".equalsIgnoreCase(export)) {
			List<LottoNumber> all = dao.getLottoHistory(start, end, number, 0, Integer.MAX_VALUE / 2, sort, dir);
			response.setContentType("text/csv; charset=UTF-8");
			String filename = URLEncoder.encode("lotto_history.csv", "UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			try (PrintWriter out = response.getWriter()) {
				out.println("postgame,num1,num2,num3,num4,num5,num6,bonusnum,firstprize,firstprizecount");
				for (LottoNumber n : all) {
					out.printf(Locale.US, "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
						n.getPostgame(), n.getNum1(), n.getNum2(), n.getNum3(), n.getNum4(), n.getNum5(), n.getNum6(),
						n.getBonusnum(), n.getFirstprize(), n.getFirstprizecount());
				}
			}
			return;
		}

		int totalCount = dao.countLottoHistory(start, end, number);
		int totalPages = (int) Math.ceil(totalCount / (double) size);
		List<LottoNumber> items = dao.getLottoHistory(start, end, number, offset, size, sort, dir);
		Map<Integer, Integer> freq = dao.countFrequencies(start, end, number);

		request.setAttribute("items", items);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("currentPage", page);
		request.setAttribute("size", size);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("start", start);
		request.setAttribute("end", end);
		request.setAttribute("number", number);
		request.setAttribute("sort", sort);
		request.setAttribute("dir", dir);
		request.setAttribute("freq", freq);

		request.getRequestDispatcher("/lotto/lottoHistory.jsp").forward(request, response);
	}

	private int parseIntOrDefault(String s, int def) {
		try { return Integer.parseInt(s); } catch (Exception e) { return def; }
	}

	private Integer parseNullableInt(String s) {
		try { if (s == null || s.isEmpty()) return null; return Integer.valueOf(s); } catch (Exception e) { return null; }
	}
}
