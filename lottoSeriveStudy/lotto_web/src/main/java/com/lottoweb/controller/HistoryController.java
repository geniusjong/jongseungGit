package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.model.LottoNumber;
import com.lottoweb.util.LuckyNumber;
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

	@GetMapping({"/lotto/lottoHistory", "/history"})
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

		// 가장 많이/적게 나온 번호 계산
		int mostFreq = -1, mostFreqCount = -1, leastFreq = -1, leastFreqCount = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
			if (e.getValue() > mostFreqCount) {
				mostFreqCount = e.getValue();
				mostFreq = e.getKey();
			}
			if (e.getValue() < leastFreqCount && e.getValue() > 0) {
				leastFreqCount = e.getValue();
				leastFreq = e.getKey();
			}
			if (e.getValue() > maxFreq) {
				maxFreq = e.getValue();
			}
		}

		// 그룹 시작 페이지 계산 (5개씩 그룹)
		int groupStart = ((page - 1) / 5) * 5 + 1;
		if (groupStart <= 0 || groupStart > totalPages) {
			groupStart = 1;
		}

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
		model.addAttribute("groupStart", groupStart);
		model.addAttribute("freq", freq);
		model.addAttribute("mostFreq", mostFreq);
		model.addAttribute("mostFreqCount", mostFreqCount);
		model.addAttribute("leastFreq", leastFreq);
		model.addAttribute("leastFreqCount", leastFreqCount);
		model.addAttribute("maxFreq", maxFreq);
		model.addAttribute("luckyNum", LuckyNumber.todayKST());

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
