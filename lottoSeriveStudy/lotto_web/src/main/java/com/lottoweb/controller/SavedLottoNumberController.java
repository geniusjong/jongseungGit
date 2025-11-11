package com.lottoweb.controller;

import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.service.SavedLottoNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/**
 * 저장된 로또 번호 관련 웹 페이지 Controller
 */
@Controller
public class SavedLottoNumberController {

    private final SavedLottoNumberService savedLottoNumberService;

    @Autowired
    public SavedLottoNumberController(SavedLottoNumberService savedLottoNumberService) {
        this.savedLottoNumberService = savedLottoNumberService;
    }

    /**
     * 저장된 로또 번호 목록 페이지
     * 
     * @param principal 현재 로그인한 사용자
     * @param model 뷰에 전달할 데이터
     * @return 저장된 번호 목록 페이지
     */
    @GetMapping("/saved-lotto")
    public String savedLottoList(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        List<SavedLottoNumber> savedNumbers = savedLottoNumberService.getSavedLottoNumbers(username);
        long count = savedLottoNumberService.getSavedLottoNumberCount(username);

        model.addAttribute("savedNumbers", savedNumbers);
        model.addAttribute("count", count);
        model.addAttribute("username", username);

        return "lotto/savedLottoList";
    }
}

