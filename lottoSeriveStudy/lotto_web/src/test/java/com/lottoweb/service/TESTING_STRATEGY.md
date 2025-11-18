# 실무 테스트 전략 가이드

## ? 테스트 피라미드 (Test Pyramid)

실무에서는 일반적으로 **테스트 피라미드** 구조를 따릅니다:

```
        /\
       /  \     E2E 테스트 (End-to-End)
      /____\    10% - 전체 시스템 통합 테스트
     /      \   
    /________\  통합 테스트 (Integration Test)
   /          \ 20% - 여러 컴포넌트 간 상호작용
  /____________\ 
 /              \
/________________\ 단위 테스트 (Unit Test)
                 70% - 개별 메서드/클래스 테스트
```

---

## ? 실무에서 일반적인 테스트 범위

### 1. **단위 테스트 (Unit Test)** - 필수 ?
**우리가 방금 작성한 것!**

#### 범위:
- ? **Service 레이어** (비즈니스 로직)
  - 복잡한 로직이 있는 모든 메서드
  - 유효성 검사 로직
  - 예외 처리 로직
- ? **Util/Helper 클래스**
- ? **복잡한 계산 로직**
- ? **데이터 변환 로직**

#### 예시:
```java
// ? 테스트 필요
@Service
public class SavedLottoNumberService {
    public SavedLottoNumber saveLottoNumber(...) { ... }  // 복잡한 로직
    public boolean isDuplicate(...) { ... }              // 비즈니스 규칙
}

// ? 테스트 불필요 (너무 단순)
public class User {
    public String getUsername() { return username; }  // 단순 getter
}
```

#### 커버리지 목표:
- **핵심 비즈니스 로직: 80-90%**
- **전체 코드: 60-70%**

---

### 2. **통합 테스트 (Integration Test)** - 권장 ?

#### 범위:
- ? **Repository + Database**
  - 실제 DB와의 연동 테스트
  - JPA 쿼리 정확성 검증
- ? **Service + Repository**
  - 여러 레이어 간 상호작용
- ? **외부 API 연동**
  - 외부 서비스 호출 테스트

#### 예시:
```java
@SpringBootTest
@Transactional
class SavedLottoNumberRepositoryTest {
    @Autowired
    private SavedLottoNumberRepository repository;
    
    @Test
    void testFindByUserOrderByCreatedAtDesc() {
        // 실제 DB에 데이터 저장 후 조회 테스트
    }
}
```

#### 커버리지 목표:
- **핵심 통합 시나리오: 50-60%**

---

### 3. **API 테스트 (Controller Test)** - 권장 ?

#### 범위:
- ? **REST API 엔드포인트**
  - HTTP 요청/응답 검증
  - 인증/인가 테스트
  - 요청 검증 로직

#### 예시:
```java
@WebMvcTest(SavedLottoNumberController.class)
class SavedLottoNumberControllerTest {
    @MockBean
    private SavedLottoNumberService service;
    
    @Test
    void testSaveLottoNumber() throws Exception {
        mockMvc.perform(post("/api/lotto/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numbers\":[1,2,3,4,5,6],\"bonus\":7}"))
                .andExpect(status().isOk());
    }
}
```

---

### 4. **E2E 테스트 (End-to-End)** - 선택적 ?

#### 범위:
- ? **핵심 사용자 시나리오**
  - 로그인 → 로또 번호 저장 → 조회
  - 전체 플로우 검증
- ? **주요 비즈니스 프로세스**

#### 예시:
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LottoE2ETest {
    @Test
    void testCompleteLottoFlow() {
        // 1. 로그인
        // 2. 로또 번호 저장
        // 3. 저장된 번호 조회
        // 4. 삭제
    }
}
```

#### 커버리지 목표:
- **핵심 시나리오: 10-20%**

---

## ? 회사 규모별 테스트 전략

### 스타트업 / 소규모 팀
```
? 단위 테스트: 핵심 비즈니스 로직만
? API 테스트: 주요 엔드포인트만
? E2E 테스트: 수동 테스트로 대체
```

### 중견 기업
```
? 단위 테스트: 70% 커버리지
? 통합 테스트: 핵심 시나리오
? API 테스트: 모든 엔드포인트
? E2E 테스트: 핵심 플로우만
```

### 대기업 / 금융권
```
? 단위 테스트: 80-90% 커버리지
? 통합 테스트: 모든 주요 시나리오
? API 테스트: 모든 엔드포인트 + 성능 테스트
? E2E 테스트: 자동화된 시나리오
? 성능 테스트: 부하 테스트, 스트레스 테스트
? 보안 테스트: 취약점 스캔
```

---

## ? 실무 체크리스트

### 필수 (Must Have) ?
- [ ] **핵심 비즈니스 로직 단위 테스트**
  - 복잡한 계산 로직
  - 유효성 검사
  - 예외 처리
- [ ] **Repository 통합 테스트**
  - 복잡한 쿼리
  - 데이터 정합성
- [ ] **API 엔드포인트 테스트**
  - 주요 CRUD 작업
  - 인증/인가

### 권장 (Should Have) ?
- [ ] **Service 레이어 전체 테스트**
- [ ] **외부 API 연동 테스트**
- [ ] **트랜잭션 테스트**
- [ ] **에러 핸들링 테스트**

### 선택적 (Nice to Have) ?
- [ ] **E2E 테스트**
- [ ] **성능 테스트**
- [ ] **보안 테스트**
- [ ] **UI 테스트** (프론트엔드)

---

## ? 현재 프로젝트 권장 사항

### 1단계: 현재 완료 ?
- ? `SavedLottoNumberService` 단위 테스트

### 2단계: 다음 추가 권장 ?
```java
// 1. Repository 통합 테스트
@SpringBootTest
@Transactional
class SavedLottoNumberRepositoryTest { ... }

// 2. Controller API 테스트
@WebMvcTest(SavedLottoNumberController.class)
class SavedLottoNumberControllerTest { ... }

// 3. 다른 Service 테스트
- EmailServiceTest
- AuthServiceTest (로그인/회원가입)
```

### 3단계: 선택적 ?
```java
// E2E 테스트
@SpringBootTest
class LottoE2ETest {
    // 로그인 → 번호 저장 → 조회 → 삭제 전체 플로우
}
```

---

## ? 실무 팁

### 1. **테스트 우선순위**
```
1순위: 핵심 비즈니스 로직 (돈/데이터 관련)
2순위: 자주 변경되는 코드
3순위: 복잡한 로직
4순위: 단순 CRUD (낮은 우선순위)
```

### 2. **테스트 작성 원칙**
- ? **빠르게 실행** (단위 테스트는 초 단위)
- ? **독립적** (다른 테스트에 영향 없음)
- ? **반복 가능** (항상 같은 결과)
- ? **명확한 이름** (`testSaveLottoNumber_WhenUserNotFound_ThrowsException`)

### 3. **테스트 커버리지 목표**
- **최소**: 핵심 로직 60%
- **권장**: 전체 70-80%
- **이상적**: 90%+ (시간/비용 고려)

### 4. **테스트 비용 vs 효과**
```
높은 효과:
- 복잡한 비즈니스 로직
- 금융/결제 관련
- 데이터 무결성 중요한 부분

낮은 효과:
- 단순 getter/setter
- 설정 파일
- 단순 매핑 로직
```

---

## ? 일반적인 테스트 비율

### 시간 배분
```
개발: 60%
테스트 작성: 30%
리팩토링: 10%
```

### 코드 비율
```
프로덕션 코드: 100%
테스트 코드: 50-100% (프로덕션 코드 대비)
```

---

## ? 다음 단계 추천

현재 프로젝트 기준으로 다음을 권장합니다:

1. **Repository 통합 테스트** 작성
2. **Controller API 테스트** 작성
3. **EmailService 테스트** 작성 (이메일 발송 로직)
4. **GitHub Actions에 테스트 자동화** (이미 설정됨)

이 중에서 어떤 것부터 진행할까요?

