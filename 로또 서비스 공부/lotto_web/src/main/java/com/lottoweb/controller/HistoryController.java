package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class HistoryController {

	private final LottoDAO lottoDAO;

	@Autowired
	public HistoryController(LottoDAO lottoDAO) {
		this.lottoDAO = lottoDAO;
	}

	@GetMapping("/lotto/lottoHistory")
	public String history(
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "20") int size,
			@RequestParam(required = false) Integer start,
			@RequestParam(required = false) Integer end,
			@RequestParam(required = false) Integer number,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String dir,
			Model model) {

		if (size <= 0) size = 20;
		if (page <= 0) page = 1;
		int offset = (page - 1) * size;

		int totalCount = lottoDAO.countLottoHistory(start, end, number);
		int totalPages = (int) Math.ceil(totalCount / (double) size);
		List<LottoNumber> items = lottoDAO.getLottoHistory(start, end, number, offset, size, sort, dir);
		Map<Integer, Integer> freq = lottoDAO.countFrequencies(start, end, number);

		model.addAttribute("items", items);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", page);
		model.addAttribute("size", size);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("number", number);
		model.addAttribute("sort", sort);
		model.addAttribute("dir", dir);
		model.addAttribute("freq", freq);

		return "lotto/lottoHistory";
	}

	@GetMapping(value = "/lotto/lottoHistory", params = "export=csv")
	public ResponseEntity<String> exportCsv(
			@RequestParam(required = false) Integer start,
			@RequestParam(required = false) Integer end,
			@RequestParam(required = false) Integer number,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false) String dir) {
		
		List<LottoNumber> all = lottoDAO.getLottoHistory(start, end, number, 0, Integer.MAX_VALUE / 2, sort, dir);
		StringBuilder csv = new StringBuilder();
		csv.append("postgame,num1,num2,num3,num4,num5,num6,bonusnum,firstprize,firstprizecount\n");
		for (LottoNumber n : all) {
			csv.append(String.format(Locale.US, "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
					n.getPostgame(), n.getNum1(), n.getNum2(), n.getNum3(), n.getNum4(), n.getNum5(), n.getNum6(),
					n.getBonusnum(), n.getFirstprize(), n.getFirstprizecount()));
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
		String filename = URLEncoder.encode("lotto_history.csv", StandardCharsets.UTF_8);
		headers.setContentDispositionFormData("attachment", filename);
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(csv.toString());
	}
}

