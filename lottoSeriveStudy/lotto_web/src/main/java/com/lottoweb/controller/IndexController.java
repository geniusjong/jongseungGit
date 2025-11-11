package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

	private final LottoDAO lottoDAO;

	@Autowired
	public IndexController(LottoDAO lottoDAO) {
		this.lottoDAO = lottoDAO;
	}

	@GetMapping("/")
	public String index(Model model) {
		LottoNumber latest = lottoDAO.getLottoNumber();
		model.addAttribute("latest", latest);
		
		// 최근 5개 회차 목록
		List<LottoNumber> recentList = lottoDAO.getLottoHistory(null, null, null, 0, 5, "postgame", "desc");
		model.addAttribute("recentList", recentList);
		
		return "index";
	}
}
