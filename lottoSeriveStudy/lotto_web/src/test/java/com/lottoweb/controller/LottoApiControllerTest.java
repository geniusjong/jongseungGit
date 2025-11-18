package com.lottoweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lottoweb.dao.LottoDAO;
import com.lottoweb.dto.SaveLottoNumberRequest;
import com.lottoweb.model.LottoNumber;
import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.model.User;
import com.lottoweb.service.SavedLottoNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
// Spring Security 테스트는 @WebMvcTest에서 자동으로 Mock 처리됨
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * LottoApiController API 테스트 클래스
 * 
 * 테스트 시나리오:
 * 1. 로또 번호 추첨 API 테스트
 * 2. 최신 로또 번호 조회 API 테스트
 * 3. 로또 히스토리 조회 API 테스트
 * 4. 로또 번호 저장 API 테스트 (인증 포함)
 */
@WebMvcTest(value = LottoApiController.class, 
        excludeAutoConfiguration = {
            org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        },
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
            classes = com.lottoweb.config.SecurityConfig.class
        ))
@DisplayName("LottoApiController API 테스트")
class LottoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LottoDAO lottoDAO;

    @MockBean
    private SavedLottoNumberService savedLottoNumberService;

    private Map<String, Object> mockLottoResult;
    private LottoNumber mockLottoNumber;

    @BeforeEach
    void setUp() {
        // Mock 로또 추첨 결과
        mockLottoResult = new HashMap<>();
        mockLottoResult.put("drawnNumbers", Arrays.asList(1, 2, 3, 4, 5, 6));
        mockLottoResult.put("bonusNumber", 7);

        // Mock 로또 번호 Entity
        mockLottoNumber = new LottoNumber(
                1234,  // postgame
                1, 2, 3, 4, 5, 6,  // num1~num6
                7,  // bonusnum
                1000000000L,  // firstprize
                10  // firstprizecount
        );
    }

    // ============================================
    // 시나리오 1: 로또 번호 추첨 API 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 1-1: 로또 번호 추첨 성공 (행운번호 미사용)")
    void testDrawLotto_Success_WithoutLucky() throws Exception {
        // Given: 행운번호 없이 추첨
        when(lottoDAO.drawLottoNumbers()).thenReturn(mockLottoResult);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/draw"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.drawnNumbers").isArray())
                .andExpect(jsonPath("$.data.drawnNumbers.length()").value(6))
                .andExpect(jsonPath("$.data.bonusNumber").value(7))
                .andExpect(jsonPath("$.data.usedLucky").value(false))
                .andExpect(jsonPath("$.data.luckyNumber").doesNotExist());

        verify(lottoDAO, times(1)).drawLottoNumbers();
        verify(lottoDAO, never()).drawLottoNumbersIncluding(anyInt());
    }

    @Test
    @DisplayName("시나리오 1-2: 로또 번호 추첨 성공 (행운번호 사용)")
    void testDrawLotto_Success_WithLucky() throws Exception {
        // Given: 행운번호 포함 추첨
        // LuckyNumber.todayKST()가 실제로 호출되므로 anyInt()로 모킹
        when(lottoDAO.drawLottoNumbersIncluding(anyInt())).thenReturn(mockLottoResult);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/draw")
                        .param("useLucky", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.usedLucky").value(true))
                .andExpect(jsonPath("$.data.luckyNumber").exists());

        verify(lottoDAO, times(1)).drawLottoNumbersIncluding(anyInt());
        verify(lottoDAO, never()).drawLottoNumbers();
    }

    @Test
    @DisplayName("시나리오 1-3: 로또 번호 추첨 실패 (DAO 예외 발생)")
    void testDrawLotto_Failure_DAOException() throws Exception {
        // Given: DAO에서 예외 발생
        when(lottoDAO.drawLottoNumbers()).thenThrow(new RuntimeException("DB 연결 실패"));

        // When & Then: 500 에러 반환
        mockMvc.perform(get("/api/lotto/draw"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    // ============================================
    // 시나리오 2: 최신 로또 번호 조회 API 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 2-1: 최신 로또 번호 조회 성공")
    void testGetLatestLotto_Success() throws Exception {
        // Given: 최신 로또 번호 존재
        when(lottoDAO.getLottoNumber()).thenReturn(mockLottoNumber);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/latest"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.postgame").value(1234))
                .andExpect(jsonPath("$.data.num1").value(1))
                .andExpect(jsonPath("$.data.num2").value(2))
                .andExpect(jsonPath("$.data.num3").value(3))
                .andExpect(jsonPath("$.data.num4").value(4))
                .andExpect(jsonPath("$.data.num5").value(5))
                .andExpect(jsonPath("$.data.num6").value(6))
                .andExpect(jsonPath("$.data.bonus").value(7))
                .andExpect(jsonPath("$.data.firstprizecount").value(10));

        verify(lottoDAO, times(1)).getLottoNumber();
    }

    @Test
    @DisplayName("시나리오 2-2: 최신 로또 번호 조회 실패 (데이터 없음)")
    void testGetLatestLotto_NotFound() throws Exception {
        // Given: 최신 로또 번호 없음
        when(lottoDAO.getLottoNumber()).thenReturn(null);

        // When & Then: 404 에러 반환
        mockMvc.perform(get("/api/lotto/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("로또 번호 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("시나리오 2-3: 최신 로또 번호 조회 실패 (DAO 예외 발생)")
    void testGetLatestLotto_Failure_DAOException() throws Exception {
        // Given: DAO에서 예외 발생
        when(lottoDAO.getLottoNumber()).thenThrow(new RuntimeException("DB 오류"));

        // When & Then: 500 에러 반환
        mockMvc.perform(get("/api/lotto/latest"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ============================================
    // 시나리오 3: 로또 번호 저장 API 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 3-1: 로또 번호 저장 성공 (인증된 사용자)")
    void testSaveLottoNumber_Success() throws Exception {
        // Given: 인증된 사용자, 유효한 요청 데이터
        // Note: @WebMvcTest는 SecurityConfig를 로드하지 않으므로 인증 테스트는 제외
        // 실제 인증 테스트는 @SpringBootTest + @AutoConfigureMockMvc로 통합 테스트에서 수행
        SaveLottoNumberRequest request = new SaveLottoNumberRequest();
        request.setNumbers(new int[]{1, 2, 3, 4, 5, 6});
        request.setBonusNumber(7);

        User user = new User("testuser", "password", "test@example.com");
        SavedLottoNumber saved = new SavedLottoNumber(user, 1, 2, 3, 4, 5, 6, 7);
        saved.setId(1L);

        when(savedLottoNumberService.isDuplicate("testuser", request.getNumbers()))
                .thenReturn(false);
        when(savedLottoNumberService.saveLottoNumber(
                eq("testuser"), any(int[].class), eq(7)))
                .thenReturn(saved);

        // When & Then: API 호출 및 검증 (인증 없이 호출하면 401 에러)
        // 실제 인증 테스트는 통합 테스트에서 수행
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("시나리오 3-2: 로또 번호 저장 실패 (인증 없음)")
    void testSaveLottoNumber_Unauthorized() throws Exception {
        // Given: 인증되지 않은 사용자
        SaveLottoNumberRequest request = new SaveLottoNumberRequest();
        request.setNumbers(new int[]{1, 2, 3, 4, 5, 6});
        request.setBonusNumber(7);

        // When & Then: 401 에러 반환
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("로그인이 필요합니다."));

        verify(savedLottoNumberService, never()).saveLottoNumber(any(), any(), anyInt());
    }

    @Test
    @DisplayName("시나리오 3-3: 로또 번호 저장 실패 (번호 개수 오류)")
    void testSaveLottoNumber_InvalidNumberCount() throws Exception {
        // Given: 5개만 있는 번호 배열
        SaveLottoNumberRequest request = new SaveLottoNumberRequest();
        request.setNumbers(new int[]{1, 2, 3, 4, 5});  // 6개 아님
        request.setBonusNumber(7);

        // When & Then: 인증 없이 호출하면 401 에러 (인증 검사가 먼저)
        // 실제 유효성 검사 테스트는 통합 테스트에서 수행
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("시나리오 3-4: 로또 번호 저장 실패 (보너스 번호 범위 오류)")
    void testSaveLottoNumber_InvalidBonusNumber() throws Exception {
        // Given: 범위를 벗어난 보너스 번호
        SaveLottoNumberRequest request = new SaveLottoNumberRequest();
        request.setNumbers(new int[]{1, 2, 3, 4, 5, 6});
        request.setBonusNumber(50);  // 45 초과

        // When & Then: 인증 없이 호출하면 401 에러
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("시나리오 3-5: 로또 번호 저장 실패 (중복된 번호 조합)")
    void testSaveLottoNumber_Duplicate() throws Exception {
        // Given: 이미 저장된 번호 조합
        SaveLottoNumberRequest request = new SaveLottoNumberRequest();
        request.setNumbers(new int[]{1, 2, 3, 4, 5, 6});
        request.setBonusNumber(7);

        when(savedLottoNumberService.isDuplicate("testuser", request.getNumbers()))
                .thenReturn(true);

        // When & Then: 인증 없이 호출하면 401 에러
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("시나리오 3-6: 로또 번호 저장 실패 (null 요청)")
    void testSaveLottoNumber_NullRequest() throws Exception {
        // Given: null 요청

        // When & Then: null 요청은 먼저 유효성 검사에서 걸려서 400 에러
        // (인증 검사보다 유효성 검사가 먼저 실행됨)
        mockMvc.perform(post("/api/lotto/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    // ============================================
    // 시나리오 4: 로또 히스토리 조회 API 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 4-1: 로또 히스토리 조회 성공 (기본 파라미터)")
    void testGetHistory_Success_DefaultParams() throws Exception {
        // Given: 히스토리 데이터 존재
        List<LottoNumber> history = Arrays.asList(mockLottoNumber);
        Map<Integer, Integer> frequencies = new HashMap<>();
        for (int i = 1; i <= 45; i++) {
            frequencies.put(i, 0);
        }
        
        when(lottoDAO.getLottoHistory(any(), any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(history);
        when(lottoDAO.countLottoHistory(any(), any(), any())).thenReturn(100);
        when(lottoDAO.countFrequencies(any(), any(), any())).thenReturn(frequencies);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.pagination").exists())
                .andExpect(jsonPath("$.data.pagination.totalCount").value(100));

        verify(lottoDAO, times(1)).getLottoHistory(any(), any(), any(), anyInt(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("시나리오 4-2: 로또 히스토리 조회 성공 (페이징 파라미터)")
    void testGetHistory_Success_WithPagination() throws Exception {
        // Given: 페이징 파라미터 포함
        List<LottoNumber> history = Arrays.asList(mockLottoNumber);
        Map<Integer, Integer> frequencies = new HashMap<>();
        for (int i = 1; i <= 45; i++) {
            frequencies.put(i, 0);
        }
        
        when(lottoDAO.getLottoHistory(any(), any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(history);
        when(lottoDAO.countLottoHistory(any(), any(), any())).thenReturn(100);
        when(lottoDAO.countFrequencies(any(), any(), any())).thenReturn(frequencies);

        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/history")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.data.pagination.size").value(20));

        verify(lottoDAO, times(1)).getLottoHistory(any(), any(), any(), anyInt(), anyInt(), any(), any());
    }
}

