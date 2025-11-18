package com.lottoweb.repository;

import com.lottoweb.model.SavedLottoNumber;
import com.lottoweb.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SavedLottoNumberRepository Integration Test
 * 
 * Tests Repository operations using H2 in-memory database.
 * 
 * Test Scenarios:
 * 1. Save and Find operations
 * 2. Find by User (ordered by CreatedAt DESC)
 * 3. Count by User
 * 4. Find by ID and User
 * 5. Delete operations
 */
@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SavedLottoNumberRepository 통합 테스트")
class SavedLottoNumberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SavedLottoNumberRepository repository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private SavedLottoNumber saved1;
    private SavedLottoNumber saved2;
    private SavedLottoNumber saved3;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser1 = new User("testuser1", "password1", "test1@example.com");
        testUser1.setEnabled(true);
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("testuser2", "password2", "test2@example.com");
        testUser2.setEnabled(true);
        testUser2 = userRepository.save(testUser2);

        // 테스트용 저장된 로또 번호 생성
        saved1 = new SavedLottoNumber(testUser1, 1, 2, 3, 4, 5, 6, 7);
        saved1.setCreatedAt(LocalDateTime.now().minusDays(2));
        saved1 = repository.save(saved1);

        saved2 = new SavedLottoNumber(testUser1, 10, 20, 30, 40, 41, 42, 43);
        saved2.setCreatedAt(LocalDateTime.now().minusDays(1));
        saved2 = repository.save(saved2);

        saved3 = new SavedLottoNumber(testUser2, 5, 15, 25, 35, 36, 37, 38);
        saved3.setCreatedAt(LocalDateTime.now());
        saved3 = repository.save(saved3);

        // 영속성 컨텍스트 플러시
        entityManager.flush();
        entityManager.clear();
    }

    // ============================================
    // 시나리오 1: 저장 및 조회 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 1-1: 로또 번호 저장 및 ID로 조회")
    void testSaveAndFindById() {
        // Given: 새로운 로또 번호
        SavedLottoNumber newSaved = new SavedLottoNumber(testUser1, 11, 12, 13, 14, 15, 16, 17);

        // When: 저장
        SavedLottoNumber saved = repository.save(newSaved);
        entityManager.flush();
        entityManager.clear();

        // Then: ID로 조회 가능
        Optional<SavedLottoNumber> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(11, found.get().getNum1());
        assertEquals(12, found.get().getNum2());
        assertEquals(17, found.get().getBonusNumber());
        assertEquals(testUser1.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("시나리오 1-2: 모든 저장된 번호 조회")
    void testFindAll() {
        // When: 모든 저장된 번호 조회
        List<SavedLottoNumber> all = repository.findAll();

        // Then: 3개가 조회되어야 함
        assertEquals(3, all.size());
    }

    // ============================================
    // 시나리오 2: 사용자별 조회 테스트 (최신순 정렬)
    // ============================================
    @Test
    @DisplayName("시나리오 2-1: 사용자별 저장된 번호 조회 (최신순 정렬)")
    void testFindByUserOrderByCreatedAtDesc() {
        // When: testUser1의 저장된 번호 조회
        List<SavedLottoNumber> user1Numbers = repository.findByUserOrderByCreatedAtDesc(testUser1);

        // Then: 2개가 조회되고 최신순으로 정렬되어야 함
        assertEquals(2, user1Numbers.size());
        assertEquals(saved2.getId(), user1Numbers.get(0).getId()); // 최신 것이 먼저
        assertEquals(saved1.getId(), user1Numbers.get(1).getId());
        assertTrue(user1Numbers.get(0).getCreatedAt().isAfter(user1Numbers.get(1).getCreatedAt()));
    }

    @Test
    @DisplayName("시나리오 2-2: 다른 사용자의 저장된 번호 조회")
    void testFindByUserOrderByCreatedAtDesc_DifferentUser() {
        // When: testUser2의 저장된 번호 조회
        List<SavedLottoNumber> user2Numbers = repository.findByUserOrderByCreatedAtDesc(testUser2);

        // Then: 1개만 조회되어야 함
        assertEquals(1, user2Numbers.size());
        assertEquals(saved3.getId(), user2Numbers.get(0).getId());
    }

    @Test
    @DisplayName("시나리오 2-3: 저장된 번호가 없는 사용자 조회")
    void testFindByUserOrderByCreatedAtDesc_NoSavedNumbers() {
        // Given: 새로운 사용자 (저장된 번호 없음)
        User newUser = new User("newuser", "password", "new@example.com");
        newUser.setEnabled(true);
        newUser = userRepository.save(newUser);
        entityManager.flush();

        // When: 저장된 번호 조회
        List<SavedLottoNumber> numbers = repository.findByUserOrderByCreatedAtDesc(newUser);

        // Then: 빈 리스트 반환
        assertTrue(numbers.isEmpty());
    }

    // ============================================
    // 시나리오 3: 개수 조회 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 3-1: 사용자별 저장된 번호 개수 조회")
    void testCountByUser() {
        // When: testUser1의 저장된 번호 개수 조회
        long count = repository.countByUser(testUser1);

        // Then: 2개여야 함
        assertEquals(2L, count);
    }

    @Test
    @DisplayName("시나리오 3-2: 다른 사용자의 저장된 번호 개수 조회")
    void testCountByUser_DifferentUser() {
        // When: testUser2의 저장된 번호 개수 조회
        long count = repository.countByUser(testUser2);

        // Then: 1개여야 함
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("시나리오 3-3: 저장된 번호가 없는 사용자 개수 조회")
    void testCountByUser_NoSavedNumbers() {
        // Given: 새로운 사용자
        User newUser = new User("newuser", "password", "new@example.com");
        newUser.setEnabled(true);
        newUser = userRepository.save(newUser);
        entityManager.flush();

        // When: 개수 조회
        long count = repository.countByUser(newUser);

        // Then: 0이어야 함
        assertEquals(0L, count);
    }

    // ============================================
    // 시나리오 4: 사용자별 ID 조회 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 4-1: 사용자가 소유한 번호 ID로 조회 성공")
    void testFindByIdAndUser_Success() {
        // When: testUser1이 소유한 번호 ID로 조회
        Optional<SavedLottoNumber> found = repository.findByIdAndUser(saved1.getId(), testUser1);

        // Then: 조회 성공
        assertTrue(found.isPresent());
        assertEquals(saved1.getId(), found.get().getId());
        assertEquals(testUser1.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("시나리오 4-2: 다른 사용자가 소유한 번호 조회 시도")
    void testFindByIdAndUser_DifferentUser() {
        // When: testUser2가 testUser1의 번호를 조회하려고 시도
        Optional<SavedLottoNumber> found = repository.findByIdAndUser(saved1.getId(), testUser2);

        // Then: 조회 실패 (빈 Optional)
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("시나리오 4-3: 존재하지 않는 ID로 조회")
    void testFindByIdAndUser_NotFound() {
        // When: 존재하지 않는 ID로 조회
        Optional<SavedLottoNumber> found = repository.findByIdAndUser(999L, testUser1);

        // Then: 조회 실패
        assertFalse(found.isPresent());
    }

    // ============================================
    // 시나리오 5: 삭제 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 5-1: 저장된 번호 삭제")
    void testDelete() {
        // Given: 저장된 번호
        SavedLottoNumber toDelete = repository.findById(saved1.getId()).orElseThrow();

        // When: 삭제
        repository.delete(toDelete);
        entityManager.flush();
        entityManager.clear();

        // Then: 조회 불가능
        Optional<SavedLottoNumber> found = repository.findById(saved1.getId());
        assertFalse(found.isPresent());

        // 다른 번호는 여전히 존재
        assertTrue(repository.findById(saved2.getId()).isPresent());
        assertTrue(repository.findById(saved3.getId()).isPresent());
    }

    @Test
    @DisplayName("시나리오 5-2: 사용자별 모든 번호 삭제")
    void testDeleteAllByUser() {
        // Given: testUser1의 모든 번호
        List<SavedLottoNumber> user1Numbers = repository.findByUserOrderByCreatedAtDesc(testUser1);
        assertEquals(2, user1Numbers.size());

        // When: 모든 번호 삭제
        repository.deleteAll(user1Numbers);
        entityManager.flush();
        entityManager.clear();

        // Then: testUser1의 번호가 모두 삭제됨
        assertEquals(0, repository.countByUser(testUser1));

        // testUser2의 번호는 여전히 존재
        assertEquals(1, repository.countByUser(testUser2));
    }

    // ============================================
    // 시나리오 6: 번호 조합으로 조회 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 6-1: 특정 번호 조합으로 조회")
    void testFindByUserAndNum1AndNum2AndNum3AndNum4AndNum5AndNum6() {
        // When: 정확한 번호 조합으로 조회
        Optional<SavedLottoNumber> found = repository.findByUserAndNum1AndNum2AndNum3AndNum4AndNum5AndNum6(
                testUser1, 1, 2, 3, 4, 5, 6);

        // Then: 조회 성공
        assertTrue(found.isPresent());
        assertEquals(saved1.getId(), found.get().getId());
    }

    @Test
    @DisplayName("시나리오 6-2: 다른 번호 조합으로 조회 시도")
    void testFindByUserAndNum1AndNum2AndNum3AndNum4AndNum5AndNum6_NotFound() {
        // When: 존재하지 않는 번호 조합으로 조회
        Optional<SavedLottoNumber> found = repository.findByUserAndNum1AndNum2AndNum3AndNum4AndNum5AndNum6(
                testUser1, 99, 98, 97, 96, 95, 94);

        // Then: 조회 실패
        assertFalse(found.isPresent());
    }

    // ============================================
    // 시나리오 7: 데이터 정합성 테스트
    // ============================================
    @Test
    @DisplayName("시나리오 7-1: 저장 시 생성일시 자동 설정 확인")
    void testCreatedAtAutoSet() {
        // Given: 새로운 번호
        SavedLottoNumber newSaved = new SavedLottoNumber(testUser1, 21, 22, 23, 24, 25, 26, 27);
        LocalDateTime beforeSave = LocalDateTime.now();

        // When: 저장
        SavedLottoNumber saved = repository.save(newSaved);
        entityManager.flush();

        // Then: 생성일시가 자동 설정됨
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isAfter(beforeSave.minusSeconds(1)));
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("시나리오 7-2: 사용자와의 연관관계 확인")
    void testUserRelationship() {
        // When: 저장된 번호 조회
        SavedLottoNumber found = repository.findById(saved1.getId()).orElseThrow();

        // Then: 사용자와의 연관관계가 올바르게 설정됨
        assertNotNull(found.getUser());
        assertEquals(testUser1.getId(), found.getUser().getId());
        assertEquals(testUser1.getUsername(), found.getUser().getUsername());
    }
}
