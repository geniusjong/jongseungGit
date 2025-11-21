package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.dto.*;
import com.lottoweb.model.LottoNumber;
import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.service.SavedLottoNumberService;
import com.lottoweb.util.LuckyNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 濡쒕삉 踰덊샇 愿??젴 REST API Controller
 * 
 * 狩? @RestController vs @Controller 李⑥씠:
 * 
 * @Controller:
 * - View ?씠由꾩쓣 諛섑솚 (?삁: "index", "lotto/lottoResult")
 * - Thymeleaf媛? HTML?쓣 ?깮?꽦?븯?뿬 ?궗?슜?옄?뿉寃? ?쟾?떖
 * - ?쎒 ?럹?씠吏? ?젋?뜑留곸뿉 ?쟻?빀
 * 
 * @RestController:
 * - ?뜲?씠?꽣 媛앹껜瑜? 吏곸젒 諛섑솚 (?삁: ApiResponse, DTO)
 * - Spring?씠 ?옄?룞?쑝濡? JSON?쑝濡? 蹂??솚
 * - API ?쓳?떟?뿉 ?쟻?빀
 * 
 * 狩? ?솢 ?븯?씠釉뚮━?뱶 諛⑹떇?쓣 ?궗?슜?븯?굹?슂?
 * 1. 湲곗〈 ?쎒 ?궗?슜?옄: /lotto ?넂 Thymeleaf ?럹?씠吏? (湲곗〈 諛⑹떇 ?쑀吏?)
 * 2. 紐⑤컮?씪 ?빋/?떎瑜? ?겢?씪?씠?뼵?듃: /api/lotto/draw ?넂 JSON ?쓳?떟 (?깉濡쒖슫 諛⑹떇)
 * 3. 媛숈?? 鍮꾩쫰?땲?뒪 濡쒖쭅 ?옱?궗?슜: LottoDAO瑜? 怨듭쑀?븯?뿬 以묐났 ?젣嫄?
 * 
 * API ?뿏?뱶?룷?씤?듃:
 * - GET /api/lotto/draw?useLucky=true  ?넂 濡쒕삉 踰덊샇 異붿꺼
 * - GET /api/lotto/latest               ?넂 理쒖떊 濡쒕삉 踰덊샇 議고쉶
 * - GET /api/lotto/history              ?넂 濡쒕삉 ?엳?뒪?넗由? 議고쉶 (?럹?씠吏?, ?븘?꽣留?, ?넻怨?)
 * - POST /api/lotto/save                ?넂 濡쒕삉 踰덊샇 ????옣 (?씤利? ?븘?슂)
 */
@RestController
@RequestMapping("/api/lotto")
@Tag(name = "로또 API", description = "로또 번호 추첨, 조회, 저장 관련 API")
public class LottoApiController {

    private final LottoDAO lottoDAO;
    private final SavedLottoNumberService savedLottoNumberService;

    @Autowired
    public LottoApiController(LottoDAO lottoDAO, SavedLottoNumberService savedLottoNumberService) {
        this.lottoDAO = lottoDAO;
        this.savedLottoNumberService = savedLottoNumberService;
    }

    /**
     * 濡쒕삉 踰덊샇 異붿꺼 API
     * 
     * ? ?씠 API?쓽 ?옣?젏:
     * 1. 紐⑤컮?씪 ?빋?뿉?꽌 ?궗?슜 媛??뒫
     * 2. Postman?쑝濡? ?뀒?뒪?듃 媛??뒫
     * 3. ?봽濡좏듃?뿏?뱶(React/Vue)?뿉?꽌 fetch濡? ?샇異? 媛??뒫
     * 4. ?떎瑜? ?꽌踰꾩뿉?꽌?룄 ?샇異? 媛??뒫
     * 
     * @param useLucky ?뻾?슫踰덊샇 ?궗?슜 ?뿬遺? (true硫? ?삤?뒛 ?궇吏? 湲곕컲 ?뻾?슫踰덊샇 ?룷?븿)
     * @return JSON ?삎?떇?쓽 異붿꺼 寃곌낵
     * 
     * ?쓳?떟 ?삁?떆:
     * {
     *   "success": true,
     *   "message": "?꽦怨?",
     *   "data": {
     *     "drawnNumbers": [1, 2, 3, 4, 5, 6],
     *     "bonusNumber": 7,
     *     "usedLucky": true,
     *     "luckyNumber": 8
     *   }
     * }
     */
    @GetMapping("/draw")
    public ResponseEntity<ApiResponse<LottoDrawResponse>> drawLotto(
            @Parameter(description = "행운번호 사용 여부", example = "true")
            @RequestParam(required = false, defaultValue = "false") Boolean useLucky) {
        
        try {
            // 狩? 湲곗〈 LottoDAO瑜? ?옱?궗?슜?븯?뿬 以묐났 肄붾뱶 ?젣嫄?
            // 湲곗〈 LottoController??? 媛숈?? 濡쒖쭅?쓣 ?궗?슜?븯?릺, JSON?쑝濡? 諛섑솚
            Map<String, Object> lottoResult;
            boolean usedLucky = false;
            Integer luckyNumber = null;
            
            if (useLucky != null && useLucky) {
                luckyNumber = LuckyNumber.todayKST();
                lottoResult = lottoDAO.drawLottoNumbersIncluding(luckyNumber);
                usedLucky = true;
            } else {
                lottoResult = lottoDAO.drawLottoNumbers();
            }
            
            @SuppressWarnings("unchecked")
            List<Integer> drawnNumbers = (List<Integer>) lottoResult.get("drawnNumbers");
            int bonusNumber = (int) lottoResult.get("bonusNumber");
            
            // DTO濡? 蹂??솚?븯?뿬 ?쓳?떟
            LottoDrawResponse response = new LottoDrawResponse(
                    drawnNumbers, bonusNumber, usedLucky, luckyNumber
            );
            
            // 狩? ResponseEntity瑜? ?궗?슜?븯?뿬 HTTP ?긽?깭 肄붾뱶 ?젣?뼱
            // 200 OK濡? 湲곕낯 ?꽦怨? ?쓳?떟 諛섑솚
            return ResponseEntity.ok(ApiResponse.success("濡쒕삉 踰덊샇 異붿꺼 ?꽦怨?", response));
            
        } catch (Exception e) {
            // ?삤瑜? 諛쒖깮 ?떆 500 Internal Server Error 諛섑솚
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("濡쒕삉 踰덊샇 異붿꺼 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }

    /**
     * 理쒖떊 濡쒕삉 ?떦泥? 踰덊샇 議고쉶 API
     * 
     * @return 理쒖떊 濡쒕삉 ?떦泥? 踰덊샇 ?젙蹂?
     * 
     * ?쓳?떟 ?삁?떆:
     * {
     *   "success": true,
     *   "message": "?꽦怨?",
     *   "data": {
     *     "postgame": 1234,
     *     "num1": 1, "num2": 2, "num3": 3,
     *     "num4": 4, "num5": 5, "num6": 6,
     *     "bonus": 7,
     *     "firstprizecount": 10
     *   }
     * }
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<LottoNumberResponse>> getLatestLotto() {
        try {
            LottoNumber latest = lottoDAO.getLottoNumber();
            
            if (latest == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("濡쒕삉 踰덊샇 ?젙蹂대?? 李얠쓣 ?닔 ?뾾?뒿?땲?떎."));
            }
            
            // Entity瑜? DTO濡? 蹂??솚
            // 狩? Entity?쓽 ?븘?뱶紐?(bonusnum)怨? DTO?쓽 ?븘?뱶紐?(bonus)?씠 ?떎瑜? ?닔 ?엳?쑝誘?濡?
            // getBonusnum() 硫붿꽌?뱶瑜? ?궗?슜?븯?뿬 蹂??솚
            LottoNumberResponse response = new LottoNumberResponse(
                    latest.getPostgame(),
                    latest.getNum1(), latest.getNum2(), latest.getNum3(),
                    latest.getNum4(), latest.getNum5(), latest.getNum6(),
                    latest.getBonusnum(),  // Entity?뒗 bonusnum, DTO?뒗 bonus濡? 留ㅽ븨
                    latest.getFirstprizecount()
            );
            
            return ResponseEntity.ok(ApiResponse.success("理쒖떊 濡쒕삉 踰덊샇 議고쉶 ?꽦怨?", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("濡쒕삉 踰덊샇 議고쉶 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }

    /**
     * 濡쒕삉 ?엳?뒪?넗由? 議고쉶 API
     * 
     * ? ?씠 API?쓽 ?옣?젏:
     * 1. ?럹?씠吏?: ?럹?씠吏뺤쑝濡? ?뜲?씠?꽣瑜? ?굹?늻?뼱 ?쟾?넚 (????슜?웾 ?뜲?씠?꽣 泥섎━)
     * 2. ?븘?꽣留?: ?쉶李? 踰붿쐞, ?듅?젙 踰덊샇 ?룷?븿 ?뿬遺?濡? ?븘?꽣留?
     * 3. ?젙?젹: ?떎?뼇?븳 ?젙?젹 諛⑹떇 ?젣怨?
     * 4. ?넻怨?: 鍮덈룄 遺꾩꽍, 媛??옣 留롮씠/?쟻寃? ?굹?삩 踰덊샇 ?젙蹂? ?젣怨?
     * 5. ?븳 踰덉쓽 ?샇異쒕줈 紐⑤뱺 ?젙蹂? ?젣怨?: 由ъ뒪?듃, ?럹?씠吏?, ?넻怨꾨?? ?븳 踰덉뿉
     * 
     * 狩? ?솢 ?럹?씠吏뺤쓣 ?궗?슜?븯?굹?슂?
     * - ????슜?웾 ?뜲?씠?꽣瑜? ?븳 踰덉뿉 ?쟾?넚?븯硫? ?꽦?뒫 ????븯
     * - ?겢?씪?씠?뼵?듃媛? ?븘?슂?븳 ?럹?씠吏?留? ?슂泥??븯?뿬 ?슚?쑉?쟻
     * - 罹먯떛 ?솢?슜?룄 利앷??
     * 
     * @param page ?럹?씠吏? 踰덊샇 (湲곕낯媛?: 1)
     * @param size ?럹?씠吏??떦 ?뜲?씠?꽣 ?닔 (湲곕낯媛?: 20)
     * @param start ?떆?옉 ?쉶李? (?꽑?깮)
     * @param end 醫낅즺 ?쉶李? (?꽑?깮)
     * @param number ?룷?븿?븷 踰덊샇 (?꽑?깮)
     * @param sort ?젙?젹 ?븘?뱶 (?꽑?깮, 湲곕낯媛?: postgame)
     * @param dir ?젙?젹 諛⑺뼢 (?꽑?깮, 湲곕낯媛?: desc)
     * @return JSON ?삎?떇?쓽 ?엳?뒪?넗由? 議고쉶 寃곌낵
     * 
     * ?쓳?떟 ?삁?떆:
     * {
     *   "success": true,
     *   "message": "?엳?뒪?넗由? 議고쉶 ?꽦怨?",
     *   "data": {
     *     "items": [...],          // 濡쒕삉 踰덊샇 由ъ뒪?듃
     *     "pagination": {          // ?럹?씠吏? ?젙蹂?
     *       "currentPage": 1,
     *       "totalPages": 100,
     *       "totalCount": 2000,
     *       "size": 20
     *     },
     *     "filters": {              // ?븘?꽣 ?젙蹂?
     *       "start": 1000,
     *       "end": 2000,
     *       "number": 7
     *     },
     *     "statistics": {           // ?넻怨? ?젙蹂?
     *       "frequencies": {...},   // 媛? 踰덊샇?쓽 異쒗쁽 鍮덈룄
     *       "mostFrequent": {       // 媛??옣 留롮씠 ?굹?삩 踰덊샇
     *         "number": 7,
     *         "count": 100
     *       },
     *       "leastFrequent": {      // 媛??옣 ?쟻寃? ?굹?삩 踰덊샇
     *         "number": 1,
     *         "count": 50
     *       }
     *     }
     *   }
     * }
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<LottoHistoryResponse>> getHistory(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "페이지당 데이터 수", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size,
            @Parameter(description = "시작 회차", example = "1000")
            @RequestParam(required = false) Integer start,
            @Parameter(description = "종료 회차", example = "2000")
            @RequestParam(required = false) Integer end,
            @Parameter(description = "포함할 번호", example = "7")
            @RequestParam(required = false) Integer number,
            @Parameter(description = "정렬 필드", example = "postgame")
            @RequestParam(required = false) String sort,
            @Parameter(description = "정렬 방향 (asc/desc)", example = "desc")
            @RequestParam(required = false) String dir) {
        
        try {
            // 狩? ?럹?씠吏? 踰붿쐞 寃??궗 諛? 怨꾩궛
            if (size <= 0) size = 20;
            if (page <= 0) page = 1;
            int offset = (page - 1) * size;
            
            // 狩? 湲곗〈 LottoDAO 硫붿꽌?뱶 ?옱?궗?슜
            // 1. ?쟾泥? 媛쒖닔 議고쉶 (?븘?꽣留? ?쟻?슜)
            int totalCount = lottoDAO.countLottoHistory(start, end, number);
            int totalPages = (int) Math.ceil(totalCount / (double) size);
            
            // 2. ?엳?뒪?넗由? 由ъ뒪?듃 議고쉶 (?럹?씠吏?, ?븘?꽣留?, ?젙?젹 ?쟻?슜)
            List<LottoNumber> lottoList = lottoDAO.getLottoHistory(start, end, number, offset, size, sort, dir);
            
            // 3. Entity瑜? DTO濡? 蹂??솚
            List<LottoNumberResponse> items = new ArrayList<>();
            for (LottoNumber lotto : lottoList) {
                LottoNumberResponse response = new LottoNumberResponse(
                        lotto.getPostgame(),
                        lotto.getNum1(), lotto.getNum2(), lotto.getNum3(),
                        lotto.getNum4(), lotto.getNum5(), lotto.getNum6(),
                        lotto.getBonusnum(),
                        lotto.getFirstprizecount()
                );
                items.add(response);
            }
            
            // 4. 鍮덈룄 ?넻怨? 怨꾩궛
            Map<Integer, Integer> frequencies = lottoDAO.countFrequencies(start, end, number);
            
            // 5. 媛??옣 留롮씠/?쟻寃? ?굹?삩 踰덊샇 怨꾩궛
            LottoHistoryResponse.FrequencyInfo mostFrequent = null;
            LottoHistoryResponse.FrequencyInfo leastFrequent = null;
            
            int mostFreq = -1, mostFreqCount = -1, leastFreq = -1, leastFreqCount = Integer.MAX_VALUE;
            
            for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
                int num = entry.getKey();
                int count = entry.getValue();
                
                // 媛??옣 留롮씠 ?굹?삩 踰덊샇
                if (count > mostFreqCount) {
                    mostFreqCount = count;
                    mostFreq = num;
                }
                
                // 媛??옣 ?쟻寃? ?굹?삩 踰덊샇 (0蹂대떎 ?겙 寃쎌슦留?)
                if (count > 0 && count < leastFreqCount) {
                    leastFreqCount = count;
                    leastFreq = num;
                }
            }
            
            if (mostFreq > 0 && mostFreqCount > 0) {
                mostFrequent = new LottoHistoryResponse.FrequencyInfo(mostFreq, mostFreqCount);
            }
            
            if (leastFreq > 0 && leastFreqCount < Integer.MAX_VALUE) {
                leastFrequent = new LottoHistoryResponse.FrequencyInfo(leastFreq, leastFreqCount);
            }
            
            // 6. DTO ?깮?꽦
            LottoHistoryResponse.PaginationInfo pagination = new LottoHistoryResponse.PaginationInfo(
                    page, totalPages, totalCount, size
            );
            
            LottoHistoryResponse.FilterInfo filters = new LottoHistoryResponse.FilterInfo(
                    start, end, number
            );
            
            LottoHistoryResponse.StatisticsInfo statistics = new LottoHistoryResponse.StatisticsInfo(
                    frequencies, mostFrequent, leastFrequent
            );
            
            LottoHistoryResponse response = new LottoHistoryResponse(
                    items, pagination, filters, statistics
            );
            
            return ResponseEntity.ok(ApiResponse.success("?엳?뒪?넗由? 議고쉶 ?꽦怨?", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("?엳?뒪?넗由? 議고쉶 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }
    
    /**
     * 濡쒕삉 踰덊샇 ????옣 API
     * ?씤利앸맂 ?궗?슜?옄留? ?젒洹? 媛??뒫?빀?땲?떎.
     * 
     * @param principal ?씤利앸맂 ?궗?슜?옄 ?젙蹂? (Spring Security媛? ?옄?룞 二쇱엯)
     * @param request ????옣?븷 濡쒕삉 踰덊샇 ?젙蹂? (6媛? 踰덊샇 + 蹂대꼫?뒪 踰덊샇)
     * @return ????옣 寃곌낵
     * 
     * ?슂泥? ?삁?떆:
     * POST /api/lotto/save
     * {
     *   "numbers": [1, 2, 3, 4, 5, 6],
     *   "bonusNumber": 7
     * }
     * 
     * ?쓳?떟 ?삁?떆:
     * {
     *   "success": true,
     *   "message": "濡쒕삉 踰덊샇媛? ????옣?릺?뿀?뒿?땲?떎.",
     *   "data": {
     *     "id": 1,
     *     "numbers": [1, 2, 3, 4, 5, 6],
     *     "bonusNumber": 7,
     *     "createdAt": "2024-01-01T12:00:00"
     *   }
     * }
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SavedLottoNumberResponse>> saveLottoNumber(
            Principal principal,
            @RequestBody SaveLottoNumberRequest request) {
        
        try {
            // ?씤利? ?솗?씤
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("濡쒓렇?씤?씠 ?븘?슂?빀?땲?떎."));
            }
            
            String username = principal.getName();
            
            // ?슂泥? ?뜲?씠?꽣 ?쑀?슚?꽦 寃??궗
            if (request == null || request.getNumbers() == null || request.getNumbers().length != 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("濡쒕삉 踰덊샇?뒗 6媛쒖뿬?빞 ?빀?땲?떎."));
            }
            
            if (request.getBonusNumber() < 1 || request.getBonusNumber() > 45) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("蹂대꼫?뒪 踰덊샇?뒗 1遺??꽣 45 ?궗?씠?쓽 ?닽?옄?뿬?빞 ?빀?땲?떎."));
            }
            
            // 以묐났 泥댄겕
            if (savedLottoNumberService.isDuplicate(username, request.getNumbers())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error("?씠誘? ????옣?맂 踰덊샇 議고빀?엯?땲?떎."));
            }
            
            // ????옣
            SavedLottoNumber saved = savedLottoNumberService.saveLottoNumber(
                    username,
                    request.getNumbers(),
                    request.getBonusNumber()
            );
            
            // DTO濡? 蹂??솚
            SavedLottoNumberResponse response = new SavedLottoNumberResponse(
                    saved.getId(),
                    new int[]{saved.getNum1(), saved.getNum2(), saved.getNum3(), 
                             saved.getNum4(), saved.getNum5(), saved.getNum6()},
                    saved.getBonusNumber(),
                    saved.getCreatedAt()
            );
            
            return ResponseEntity.ok(ApiResponse.success("濡쒕삉 踰덊샇媛? ????옣?릺?뿀?뒿?땲?떎.", response));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("濡쒕삉 踰덊샇 ????옣 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }

    /**
     * ????옣?맂 濡쒕삉 踰덊샇 紐⑸줉 議고쉶 API
     *
     * @param principal ?쁽?옱 濡쒓렇?씤?븳 ?궗?슜?옄
     * @return ????옣?맂 濡쒕삉 踰덊샇 紐⑸줉
     *
     * ?쓳?떟 ?삁?떆:
     * {
     *   "success": true,
     *   "message": "議고쉶 ?꽦怨?",
     *   "data": [
     *     {
     *       "id": 1,
     *       "numbers": [1, 2, 3, 4, 5, 6],
     *       "bonusNumber": 7,
     *       "savedAt": "2024-01-01T12:00:00"
     *     }
     *   ]
     * }
     */
    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<SavedLottoNumberResponse>>> getSavedLottoNumbers(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("濡쒓렇?씤?씠 ?븘?슂?빀?땲?떎."));
            }

            String username = principal.getName();
            List<SavedLottoNumber> savedNumbers = savedLottoNumberService.getSavedLottoNumbers(username);

            // Entity瑜? DTO濡? 蹂??솚
            List<SavedLottoNumberResponse> responseList = new ArrayList<>();
            for (SavedLottoNumber saved : savedNumbers) {
                SavedLottoNumberResponse response = new SavedLottoNumberResponse(
                        saved.getId(),
                        new int[]{saved.getNum1(), saved.getNum2(), saved.getNum3(),
                                 saved.getNum4(), saved.getNum5(), saved.getNum6()},
                        saved.getBonusNumber(),
                        saved.getCreatedAt()
                );
                responseList.add(response);
            }

            return ResponseEntity.ok(ApiResponse.success("????옣?맂 濡쒕삉 踰덊샇 議고쉶 ?꽦怨?", responseList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("????옣?맂 濡쒕삉 踰덊샇 議고쉶 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }

    /**
     * ????옣?맂 濡쒕삉 踰덊샇 ?궘?젣 API
     *
     * @param principal ?쁽?옱 濡쒓렇?씤?븳 ?궗?슜?옄
     * @param id ?궘?젣?븷 ????옣?맂 濡쒕삉 踰덊샇 ID
     * @return ?궘?젣 寃곌낵
     */
    @DeleteMapping("/saved/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSavedLottoNumber(
            Principal principal,
            @Parameter(description = "삭제할 저장된 로또 번호 ID", example = "1")
            @PathVariable Long id) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("濡쒓렇?씤?씠 ?븘?슂?빀?땲?떎."));
            }

            String username = principal.getName();
            boolean deleted = savedLottoNumberService.deleteSavedLottoNumber(username, id);

            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("?궘?젣?릺?뿀?뒿?땲?떎.", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("????옣?맂 踰덊샇瑜? 李얠쓣 ?닔 ?뾾?뒿?땲?떎."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("?궘?젣 以? ?삤瑜섍?? 諛쒖깮?뻽?뒿?땲?떎: " + e.getMessage()));
        }
    }
}
