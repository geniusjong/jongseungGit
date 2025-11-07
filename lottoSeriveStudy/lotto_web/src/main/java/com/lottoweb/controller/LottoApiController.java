package com.lottoweb.controller;

import com.lottoweb.dao.LottoDAO;
import com.lottoweb.dto.*;
import com.lottoweb.model.LottoNumber;
import com.lottoweb.util.LuckyNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 로또 번호 관련 REST API Controller
 * 
 * ⭐ @RestController vs @Controller 차이:
 * 
 * @Controller:
 * - View 이름을 반환 (예: "index", "lotto/lottoResult")
 * - Thymeleaf가 HTML을 생성하여 사용자에게 전달
 * - 웹 페이지 렌더링에 적합
 * 
 * @RestController:
 * - 데이터 객체를 직접 반환 (예: ApiResponse, DTO)
 * - Spring이 자동으로 JSON으로 변환
 * - API 응답에 적합
 * 
 * ⭐ 왜 하이브리드 방식을 사용하나요?
 * 1. 기존 웹 사용자: /lotto → Thymeleaf 페이지 (기존 방식 유지)
 * 2. 모바일 앱/다른 클라이언트: /api/lotto/draw → JSON 응답 (새로운 방식)
 * 3. 같은 비즈니스 로직 재사용: LottoDAO를 공유하여 중복 제거
 * 
 * API 엔드포인트:
 * - GET /api/lotto/draw?useLucky=true  → 로또 번호 추첨
 * - GET /api/lotto/latest               → 최신 로또 번호 조회
 * - GET /api/lotto/history              → 로또 히스토리 조회 (페이징, 필터링, 통계)
 */
@RestController
@RequestMapping("/api/lotto")
public class LottoApiController {

    private final LottoDAO lottoDAO;

    @Autowired
    public LottoApiController(LottoDAO lottoDAO) {
        this.lottoDAO = lottoDAO;
    }

    /**
     * 로또 번호 추첨 API
     * 
     * ⭐ 이 API의 장점:
     * 1. 모바일 앱에서 사용 가능
     * 2. Postman으로 테스트 가능
     * 3. 프론트엔드(React/Vue)에서 fetch로 호출 가능
     * 4. 다른 서비스에서도 호출 가능
     * 
     * @param useLucky 행운번호 사용 여부 (true면 오늘 날짜 기반 행운번호 포함)
     * @return JSON 형식의 추첨 결과
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "message": "성공",
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
            @RequestParam(required = false, defaultValue = "false") Boolean useLucky) {
        
        try {
            // ⭐ 기존 LottoDAO를 재사용하여 중복 코드 제거
            // 기존 LottoController와 같은 로직을 사용하지만, JSON으로 반환
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
            
            // DTO로 변환하여 응답
            LottoDrawResponse response = new LottoDrawResponse(
                    drawnNumbers, bonusNumber, usedLucky, luckyNumber
            );
            
            // ⭐ ResponseEntity를 사용하여 HTTP 상태 코드 제어
            // 200 OK와 함께 성공 응답 반환
            return ResponseEntity.ok(ApiResponse.success("로또 번호 추첨 성공", response));
            
        } catch (Exception e) {
            // 에러 발생 시 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로또 번호 추첨 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 최신 로또 당첨 번호 조회 API
     * 
     * @return 최신 로또 당첨 번호 정보
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "message": "성공",
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
                        .body(ApiResponse.error("로또 번호 정보를 찾을 수 없습니다."));
            }
            
            // Entity를 DTO로 변환
            // ⭐ Entity의 필드명(bonusnum)과 DTO의 필드명(bonus)이 다를 수 있으므로
            // getBonusnum() 메서드를 사용하여 변환
            LottoNumberResponse response = new LottoNumberResponse(
                    latest.getPostgame(),
                    latest.getNum1(), latest.getNum2(), latest.getNum3(),
                    latest.getNum4(), latest.getNum5(), latest.getNum6(),
                    latest.getBonusnum(),  // Entity는 bonusnum, DTO는 bonus로 명명
                    latest.getFirstprizecount()
            );
            
            return ResponseEntity.ok(ApiResponse.success("최신 로또 번호 조회 성공", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로또 번호 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 로또 히스토리 조회 API
     * 
     * ⭐ 이 API의 장점:
     * 1. 페이징: 페이지별로 데이터를 나누어 전송 (대용량 데이터 처리)
     * 2. 필터링: 회차 범위, 특정 번호 포함 여부로 필터링
     * 3. 정렬: 다양한 정렬 옵션 제공
     * 4. 통계: 빈도 분석, 가장 많이/적게 나온 번호 정보 제공
     * 5. 한 번의 호출로 모든 정보 제공: 리스트, 페이징, 통계를 한 번에
     * 
     * ⭐ 왜 페이징을 사용하나요?
     * - 대용량 데이터를 한 번에 전송하면 성능 저하
     * - 클라이언트가 필요한 페이지만 요청하여 효율적
     * - 네트워크 사용량 감소
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지당 데이터 수 (기본값: 20)
     * @param start 시작 회차 (선택)
     * @param end 종료 회차 (선택)
     * @param number 포함할 번호 (선택)
     * @param sort 정렬 필드 (선택, 기본값: postgame)
     * @param dir 정렬 방향 (선택, 기본값: desc)
     * @return JSON 형식의 히스토리 조회 결과
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "message": "히스토리 조회 성공",
     *   "data": {
     *     "items": [...],          // 로또 번호 리스트
     *     "pagination": {          // 페이징 정보
     *       "currentPage": 1,
     *       "totalPages": 100,
     *       "totalCount": 2000,
     *       "size": 20
     *     },
     *     "filters": {              // 필터 정보
     *       "start": 1000,
     *       "end": 2000,
     *       "number": 7
     *     },
     *     "statistics": {           // 통계 정보
     *       "frequencies": {...},   // 각 번호의 출현 빈도
     *       "mostFrequent": {       // 가장 많이 나온 번호
     *         "number": 7,
     *         "count": 100
     *       },
     *       "leastFrequent": {      // 가장 적게 나온 번호
     *         "number": 1,
     *         "count": 50
     *       }
     *     }
     *   }
     * }
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<LottoHistoryResponse>> getHistory(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) Integer end,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir) {
        
        try {
            // ⭐ 페이징 검증 및 계산
            if (size <= 0) size = 20;
            if (page <= 0) page = 1;
            int offset = (page - 1) * size;
            
            // ⭐ 기존 LottoDAO 메서드 재사용
            // 1. 전체 건수 조회 (필터링 적용)
            int totalCount = lottoDAO.countLottoHistory(start, end, number);
            int totalPages = (int) Math.ceil(totalCount / (double) size);
            
            // 2. 히스토리 리스트 조회 (페이징, 필터링, 정렬 적용)
            List<LottoNumber> lottoList = lottoDAO.getLottoHistory(start, end, number, offset, size, sort, dir);
            
            // 3. Entity를 DTO로 변환
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
            
            // 4. 빈도 통계 계산
            Map<Integer, Integer> frequencies = lottoDAO.countFrequencies(start, end, number);
            
            // 5. 가장 많이/적게 나온 번호 계산
            LottoHistoryResponse.FrequencyInfo mostFrequent = null;
            LottoHistoryResponse.FrequencyInfo leastFrequent = null;
            
            int mostFreq = -1, mostFreqCount = -1, leastFreq = -1, leastFreqCount = Integer.MAX_VALUE;
            
            for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
                int num = entry.getKey();
                int count = entry.getValue();
                
                // 가장 많이 나온 번호
                if (count > mostFreqCount) {
                    mostFreqCount = count;
                    mostFreq = num;
                }
                
                // 가장 적게 나온 번호 (0보다 큰 경우만)
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
            
            // 6. DTO 조립
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
            
            return ResponseEntity.ok(ApiResponse.success("히스토리 조회 성공", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("히스토리 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}

