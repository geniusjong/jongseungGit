package com.lottoweb.service;

import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.model.User;
import com.lottoweb.repository.SavedLottoNumberRepository;
import com.lottoweb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SavedLottoNumberService 테스트 클래스
 * 
 * 테스트 시나리오:
 * 1. 정상적인 로또 번호 저장 테스트
 * 2. 유효성 검사 실패 테스트 (번호 개수, 중복, 범위 오류)
 * 3. 저장된 로또 번호 조회 테스트
 * 4. 로또 번호 삭제 테스트
 * 5. 중복 확인 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SavedLottoNumberService 테스트")
class SavedLottoNumberServiceTest {

    @Mock
    private SavedLottoNumberRepository savedLottoNumberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SavedLottoNumberService savedLottoNumberService;

    private User testUser;
    private int[] validNumbers;
    private int validBonusNumber;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());

        // 유효한 로또 번호
        validNumbers = new int[]{7, 12, 23, 31, 38, 42};
        validBonusNumber = 15;
    }

    // ============================================
    // 시나리오 1: 정상적인 로또 번호 저장 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 1: 정상적인 로또 번호 저장 성공")
    void testSaveLottoNumber_Success() {
        // Given: 사용자가 존재하고, 유효한 로또 번호가 주어진 경우
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        SavedLottoNumber savedLottoNumber = new SavedLottoNumber(
                testUser, 7, 12, 23, 31, 38, 42, validBonusNumber
        );
        savedLottoNumber.setId(1L);
        
        when(savedLottoNumberRepository.save(any(SavedLottoNumber.class)))
                .thenReturn(savedLottoNumber);

        // When: 로또 번호를 저장하면
        SavedLottoNumber result = savedLottoNumberService.saveLottoNumber(
                "testuser", validNumbers, validBonusNumber
        );

        // Then: 정상적으로 저장되고, 번호가 정렬되어 저장되어야 함
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(7, result.getNum1());
        assertEquals(12, result.getNum2());
        assertEquals(23, result.getNum3());
        assertEquals(31, result.getNum4());
        assertEquals(38, result.getNum5());
        assertEquals(42, result.getNum6());
        assertEquals(validBonusNumber, result.getBonusNumber());
        assertEquals(testUser, result.getUser());

        // Repository의 save 메서드가 한 번 호출되었는지 확인
        verify(savedLottoNumberRepository, times(1)).save(any(SavedLottoNumber.class));
    }

    @Test
    @DisplayName("시나리오 1-1: 정렬되지 않은 번호가 정렬되어 저장되는지 확인")
    void testSaveLottoNumber_UnorderedNumbers_Sorted() {
        // Given: 정렬되지 않은 번호 배열
        int[] unorderedNumbers = new int[]{42, 7, 31, 12, 38, 23};
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        SavedLottoNumber savedLottoNumber = new SavedLottoNumber(
                testUser, 7, 12, 23, 31, 38, 42, validBonusNumber
        );
        savedLottoNumber.setId(1L);
        
        when(savedLottoNumberRepository.save(any(SavedLottoNumber.class)))
                .thenReturn(savedLottoNumber);

        // When: 정렬되지 않은 번호를 저장하면
        SavedLottoNumber result = savedLottoNumberService.saveLottoNumber(
                "testuser", unorderedNumbers, validBonusNumber
        );

        // Then: 번호가 정렬되어 저장되어야 함
        assertNotNull(result);
        assertTrue(result.getNum1() < result.getNum2());
        assertTrue(result.getNum2() < result.getNum3());
        assertTrue(result.getNum3() < result.getNum4());
        assertTrue(result.getNum4() < result.getNum5());
        assertTrue(result.getNum5() < result.getNum6());
    }

    // ============================================
    // 시나리오 2: 유효성 검사 실패 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 2-1: 존재하지 않는 사용자로 저장 시도 시 예외 발생")
    void testSaveLottoNumber_UserNotFound() {
        // Given: 존재하지 않는 사용자
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "nonexistent", validNumbers, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다"));
        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-2: 번호 배열이 6개가 아닌 경우 예외 발생")
    void testSaveLottoNumber_InvalidNumberCount() {
        // Given: 5개만 있는 번호 배열
        int[] invalidNumbers = new int[]{7, 12, 23, 31, 38};
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", invalidNumbers, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("로또 번호는 6개여야 합니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-3: null 번호 배열 입력 시 예외 발생")
    void testSaveLottoNumber_NullNumbers() {
        // Given: null 번호 배열
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", null, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("로또 번호는 6개여야 합니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-4: 번호 범위를 벗어난 경우 예외 발생 (1 미만)")
    void testSaveLottoNumber_NumberOutOfRange_BelowOne() {
        // Given: 0이 포함된 번호 배열
        int[] invalidNumbers = new int[]{0, 7, 12, 23, 31, 38};
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", invalidNumbers, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("로또 번호는 1부터 45 사이의 숫자여야 합니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-5: 번호 범위를 벗어난 경우 예외 발생 (45 초과)")
    void testSaveLottoNumber_NumberOutOfRange_AboveFortyFive() {
        // Given: 46이 포함된 번호 배열
        int[] invalidNumbers = new int[]{7, 12, 23, 31, 38, 46};
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", invalidNumbers, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("로또 번호는 1부터 45 사이의 숫자여야 합니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-6: 일반 번호에 중복이 있는 경우 예외 발생")
    void testSaveLottoNumber_DuplicateNumbers() {
        // Given: 중복된 번호가 있는 배열
        int[] duplicateNumbers = new int[]{7, 12, 23, 12, 38, 42};
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", duplicateNumbers, validBonusNumber
                )
        );

        assertTrue(exception.getMessage().contains("로또 번호는 중복될 수 없습니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-7: 보너스 번호가 일반 번호와 중복되는 경우 예외 발생")
    void testSaveLottoNumber_BonusNumberDuplicate() {
        // Given: 보너스 번호가 일반 번호와 중복
        int bonusDuplicate = 7; // 일반 번호에 7이 포함되어 있음
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", validNumbers, bonusDuplicate
                )
        );

        assertTrue(exception.getMessage().contains("보너스 번호는 일반 번호와 중복될 수 없습니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    @Test
    @DisplayName("시나리오 2-8: 보너스 번호 범위를 벗어난 경우 예외 발생")
    void testSaveLottoNumber_BonusNumberOutOfRange() {
        // Given: 범위를 벗어난 보너스 번호
        int invalidBonus = 50;
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.saveLottoNumber(
                        "testuser", validNumbers, invalidBonus
                )
        );

        assertTrue(exception.getMessage().contains("보너스 번호는 1부터 45 사이의 숫자여야 합니다"));
        verify(savedLottoNumberRepository, never()).save(any());
    }

    // ============================================
    // 시나리오 3: 저장된 로또 번호 조회 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 3: 저장된 로또 번호 전체 조회 성공")
    void testGetSavedLottoNumbers_Success() {
        // Given: 사용자가 저장한 로또 번호 목록
        SavedLottoNumber saved1 = new SavedLottoNumber(
                testUser, 1, 2, 3, 4, 5, 6, 7
        );
        saved1.setId(1L);
        saved1.setCreatedAt(LocalDateTime.now().minusDays(2));

        SavedLottoNumber saved2 = new SavedLottoNumber(
                testUser, 10, 20, 30, 40, 41, 42, 43
        );
        saved2.setId(2L);
        saved2.setCreatedAt(LocalDateTime.now().minusDays(1));

        List<SavedLottoNumber> savedNumbers = Arrays.asList(saved2, saved1); // 최신순

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(savedNumbers);

        // When: 저장된 로또 번호 목록을 조회하면
        List<SavedLottoNumber> result = savedLottoNumberService.getSavedLottoNumbers("testuser");

        // Then: 최신순으로 정렬된 목록이 반환되어야 함
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId()); // 최신 것이 먼저
        assertEquals(1L, result.get(1).getId());

        verify(savedLottoNumberRepository, times(1))
                .findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("시나리오 3-1: 저장된 번호가 없는 경우 빈 리스트 반환")
    void testGetSavedLottoNumbers_EmptyList() {
        // Given: 저장된 번호가 없음
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(new ArrayList<>());

        // When: 저장된 로또 번호 목록을 조회하면
        List<SavedLottoNumber> result = savedLottoNumberService.getSavedLottoNumbers("testuser");

        // Then: 빈 리스트가 반환되어야 함
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("시나리오 3-2: 존재하지 않는 사용자로 조회 시 예외 발생")
    void testGetSavedLottoNumbers_UserNotFound() {
        // Given: 존재하지 않는 사용자
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.getSavedLottoNumbers("nonexistent")
        );

        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다"));
        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(savedLottoNumberRepository, never()).findByUserOrderByCreatedAtDesc(any());
    }

    // ============================================
    // 시나리오 4: 로또 번호 삭제 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 4: 로또 번호 삭제 성공")
    void testDeleteSavedLottoNumber_Success() {
        // Given: 사용자가 저장한 로또 번호
        SavedLottoNumber savedLottoNumber = new SavedLottoNumber(
                testUser, 7, 12, 23, 31, 38, 42, validBonusNumber
        );
        savedLottoNumber.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.of(savedLottoNumber));
        doNothing().when(savedLottoNumberRepository).delete(savedLottoNumber);

        // When: 로또 번호를 삭제하면
        boolean result = savedLottoNumberService.deleteSavedLottoNumber("testuser", 1L);

        // Then: 삭제 성공 여부로 true가 반환되어야 함
        assertTrue(result);
        verify(savedLottoNumberRepository, times(1)).findByIdAndUser(1L, testUser);
        verify(savedLottoNumberRepository, times(1)).delete(savedLottoNumber);
    }

    @Test
    @DisplayName("시나리오 4-1: 존재하지 않는 로또 번호 삭제 시 false 반환")
    void testDeleteSavedLottoNumber_NotFound() {
        // Given: 존재하지 않는 로또 번호
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByIdAndUser(999L, testUser))
                .thenReturn(Optional.empty());

        // When: 존재하지 않는 로또 번호를 삭제하려고 하면
        boolean result = savedLottoNumberService.deleteSavedLottoNumber("testuser", 999L);

        // Then: false가 반환되어야 함
        assertFalse(result);
        verify(savedLottoNumberRepository, times(1)).findByIdAndUser(999L, testUser);
        verify(savedLottoNumberRepository, never()).delete(any());
    }

    @Test
    @DisplayName("시나리오 4-2: 다른 사용자의 로또 번호 삭제 시도 시 false 반환")
    void testDeleteSavedLottoNumber_DifferentUser() {
        // Given: 다른 사용자의 로또 번호
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");

        SavedLottoNumber otherUserLotto = new SavedLottoNumber(
                otherUser, 1, 2, 3, 4, 5, 6, 7
        );
        otherUserLotto.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.empty()); // testuser의 번호가 아님

        // When: 다른 사용자의 로또 번호를 삭제하려고 하면
        boolean result = savedLottoNumberService.deleteSavedLottoNumber("testuser", 1L);

        // Then: false가 반환되어야 함
        assertFalse(result);
        verify(savedLottoNumberRepository, never()).delete(any());
    }

    @Test
    @DisplayName("시나리오 4-3: 존재하지 않는 사용자로 삭제 시도 시 예외 발생")
    void testDeleteSavedLottoNumber_UserNotFound() {
        // Given: 존재하지 않는 사용자
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.deleteSavedLottoNumber("nonexistent", 1L)
        );

        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다"));
        assertTrue(exception.getMessage().contains("nonexistent"));
        verify(savedLottoNumberRepository, never()).findByIdAndUser(any(), any());
    }

    // ============================================
    // 시나리오 5: 중복 확인 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 5: 중복된 번호 조합 확인 성공")
    void testIsDuplicate_True() {
        // Given: 이미 저장된 번호 조합
        SavedLottoNumber existing = new SavedLottoNumber(
                testUser, 7, 12, 23, 31, 38, 42, validBonusNumber
        );
        existing.setId(1L);

        List<SavedLottoNumber> savedNumbers = Arrays.asList(existing);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(savedNumbers);

        // When: 동일한 번호 조합이 중복인지 확인하면
        boolean result = savedLottoNumberService.isDuplicate("testuser", validNumbers);

        // Then: true가 반환되어야 함
        assertTrue(result);
    }

    @Test
    @DisplayName("시나리오 5-1: 중복되지 않은 번호 조합 확인")
    void testIsDuplicate_False() {
        // Given: 다른 번호 조합이 저장되어 있음
        SavedLottoNumber existing = new SavedLottoNumber(
                testUser, 1, 2, 3, 4, 5, 6, 7
        );
        existing.setId(1L);

        List<SavedLottoNumber> savedNumbers = Arrays.asList(existing);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(savedNumbers);

        // When: 다른 번호 조합이 중복인지 확인하면
        boolean result = savedLottoNumberService.isDuplicate("testuser", validNumbers);

        // Then: false가 반환되어야 함
        assertFalse(result);
    }

    @Test
    @DisplayName("시나리오 5-2: 순서만 다른 같은 번호 조합의 중복 확인")
    void testIsDuplicate_DifferentOrder_SameNumbers() {
        // Given: 이미 저장된 번호 조합 (정렬된 상태)
        SavedLottoNumber existing = new SavedLottoNumber(
                testUser, 7, 12, 23, 31, 38, 42, validBonusNumber
        );
        existing.setId(1L);

        List<SavedLottoNumber> savedNumbers = Arrays.asList(existing);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(savedNumbers);

        // When: 순서만 다른 같은 번호 조합이 중복인지 확인하면
        int[] differentOrder = new int[]{42, 7, 31, 12, 38, 23}; // 순서만 다름
        boolean result = savedLottoNumberService.isDuplicate("testuser", differentOrder);

        // Then: true가 반환되어야 함 (순서를 정렬하고 같은 번호면 중복)
        assertTrue(result);
    }

    @Test
    @DisplayName("시나리오 5-3: 저장된 번호가 없는 경우 중복 아님")
    void testIsDuplicate_NoSavedNumbers() {
        // Given: 저장된 번호가 없음
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(new ArrayList<>());

        // When: 중복인지 확인하면
        boolean result = savedLottoNumberService.isDuplicate("testuser", validNumbers);

        // Then: false가 반환되어야 함
        assertFalse(result);
    }

    @Test
    @DisplayName("시나리오 5-4: null 또는 잘못된 번호 배열 입력 시 false 반환")
    void testIsDuplicate_InvalidInput() {
        // Given: null 번호 배열
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When: null 번호 배열이 중복인지 확인하면
        boolean result1 = savedLottoNumberService.isDuplicate("testuser", null);
        boolean result2 = savedLottoNumberService.isDuplicate("testuser", new int[]{1, 2, 3}); // 6개 아님

        // Then: false가 반환되어야 함
        assertFalse(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("시나리오 5-5: 존재하지 않는 사용자로 중복 확인 시 예외 발생")
    void testIsDuplicate_UserNotFound() {
        // Given: 존재하지 않는 사용자
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savedLottoNumberService.isDuplicate("nonexistent", validNumbers)
        );

        assertTrue(exception.getMessage().contains("사용자를 찾을 수 없습니다"));
        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    // ============================================
    // 추가 테스트: 저장된 로또 번호 개수 조회
    // ============================================
    @Test
    @DisplayName("추가: 저장된 로또 번호 개수 조회 성공")
    void testGetSavedLottoNumberCount_Success() {
        // Given: 저장된 번호가 3개
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.countByUser(testUser)).thenReturn(3L);

        // When: 저장된 번호 개수를 조회하면
        long count = savedLottoNumberService.getSavedLottoNumberCount("testuser");

        // Then: 3개가 반환되어야 함
        assertEquals(3L, count);
        verify(savedLottoNumberRepository, times(1)).countByUser(testUser);
    }

    @Test
    @DisplayName("추가: 저장된 번호가 없는 경우 0 반환")
    void testGetSavedLottoNumberCount_Zero() {
        // Given: 저장된 번호가 없음
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(savedLottoNumberRepository.countByUser(testUser)).thenReturn(0L);

        // When: 저장된 번호 개수를 조회하면
        long count = savedLottoNumberService.getSavedLottoNumberCount("testuser");

        // Then: 0이 반환되어야 함
        assertEquals(0L, count);
    }
}
