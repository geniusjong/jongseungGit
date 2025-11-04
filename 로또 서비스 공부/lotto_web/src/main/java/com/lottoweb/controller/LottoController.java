package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.util.LuckyNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class LottoController {

	private final LottoDAO lottoDAO;

	@Autowired
	public LottoController(LottoDAO lottoDAO) {
		this.lottoDAO = lottoDAO;
	}

	@GetMapping("/lotto")
	public String lotto(@RequestParam(required = false) Boolean useLucky, Model model) {
		// 최신 로또 번호 조회
		model.addAttribute("lottoNumber", lottoDAO.getLottoNumber());

		// 기본/행운번호 포함 추첨
		Map<String, Object> lottoResult;
		if (useLucky != null && useLucky) {
			int lucky = LuckyNumber.todayKST();
			lottoResult = lottoDAO.drawLottoNumbersIncluding(lucky);
			model.addAttribute("usedLucky", true);
			model.addAttribute("luckyNumber", lucky);
		} else {
			lottoResult = lottoDAO.drawLottoNumbers();
			model.addAttribute("usedLucky", false);
		}

		@SuppressWarnings("unchecked")
		List<Integer> drawnNumbers = (List<Integer>) lottoResult.get("drawnNumbers");
		int bonusNumber = (int) lottoResult.get("bonusNumber");

		model.addAttribute("drawnNumbers", drawnNumbers);
		model.addAttribute("bonusNumber", bonusNumber);

		return "lotto/lottoResult";
	}
}

