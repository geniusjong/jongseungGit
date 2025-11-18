# 통합 테스트 & API 테스트 가능 여부 분석

## ? 프로젝트 구조 분석 결과

### ? **통합 테스트 가능: YES**
### ? **API 테스트 가능: YES**

---

## 1. 통합 테스트 (Integration Test) 분석

### ? 가능한 이유

#### 1.1 Repository 구조
```java
? SavedLottoNumberRepository extends JpaRepository
? UserRepository extends JpaRepository  
? LottoRepository extends JpaRepository
? EmailVerificationTokenRepository extends JpaRepository
```
- **JPA Repository 사용** → 실제 DB 연동 테스트 가능
- **Spring Data JPA** → 테스트 환경 구성 용이

#### 1.2 테스트 가능한 통합 시나리오

##### ? **SavedLottoNumberRepository 통합 테스트**
```java
@SpringBootTest
@Transactional
class SavedLottoNumberRepositoryTest {
    // 테스트 가능:
    - findByUserOrderByCreatedAtDesc() - 실제 DB 조회
    - countByUser() - 실제 DB 카운트
    - findByIdAndUser() - 실제 DB 조회
    - save() - 실제 DB 저장
    - delete() - 실제 DB 삭제
}
```

##### ? **UserRepository 통합 테스트**
```java
@SpringBootTest
@Transactional
class UserRepositoryTest {
    // 테스트 가능:
    - findByUsername() - 실제 DB 조회
    - findByEmail() - 실제 DB 조회
    - existsByUsernameOrEmail() - 실제 DB 존재 확인
}
```

##### ? **Service + Repository 통합 테스트**
```java
@SpringBootTest
@Transactional
class SavedLottoNumberServiceIntegrationTest {
    @Autowired
    private SavedLottoNumberService service;
    
    @Autowired
    private SavedLottoNumberRepository repository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 실제 DB를 사용한 통합 테스트 가능
}
```

#### 1.3 테스트 환경 구성 방법

##### 옵션 1: H2 인메모리 DB (권장) ?
```properties
# src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**장점:**
- ? 빠른 실행 속도
- ? 실제 DB 설치 불필요
- ? 테스트 격리 (각 테스트마다 초기화)

##### 옵션 2: 실제 MariaDB (Docker)
```yaml
# docker-compose.test.yml
services:
  test-db:
    image: mariadb:10.11
    environment:
      MYSQL_ROOT_PASSWORD: test
      MYSQL_DATABASE: lotto_test
```

**장점:**
- ? 프로덕션과 동일한 환경
- ? 실제 DB 동작 검증

---

## 2. API 테스트 (Controller Test) 분석

### ? 가능한 이유

#### 2.1 REST API 엔드포인트 존재
```java
@RestController
@RequestMapping("/api/lotto")
public class LottoApiController {
    ? GET  /api/lotto/draw          - 로또 번호 추첨
    ? GET  /api/lotto/latest        - 최신 로또 번호 조회
    ? GET  /api/lotto/history       - 로또 히스토리 조회
    ? POST /api/lotto/save           - 로또 번호 저장 (인증 필요)
}
```

#### 2.2 테스트 가능한 API 시나리오

##### ? **인증 불필요 API 테스트**
```java
@WebMvcTest(LottoApiController.class)
class LottoApiControllerTest {
    // 테스트 가능:
    ? GET /api/lotto/draw?useLucky=true
    ? GET /api/lotto/latest
    ? GET /api/lotto/history?page=0&size=10
}
```

##### ? **인증 필요 API 테스트**
```java
@WebMvcTest(LottoApiController.class)
@AutoConfigureMockMvc
class LottoApiControllerAuthTest {
    @MockBean
    private SavedLottoNumberService service;
    
    // MockMvc로 인증된 사용자 시뮬레이션 가능
    mockMvc.perform(post("/api/lotto/save")
        .with(user("testuser"))  // Spring Security Mock
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"numbers\":[1,2,3,4,5,6],\"bonusNumber\":7}"))
        .andExpect(status().isOk());
}
```

#### 2.3 테스트 도구

##### ? **MockMvc 사용 가능**
```java
@SpringBootTest
@AutoConfigureMockMvc
class LottoApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    // HTTP 요청/응답 테스트 가능
}
```

##### ? **@WebMvcTest 사용 가능**
```java
@WebMvcTest(LottoApiController.class)
class LottoApiControllerTest {
    // Controller만 로드하여 빠른 테스트
    // Service는 @MockBean으로 주입
}
```

---

## 3. 상세 테스트 가능 항목

### 3.1 통합 테스트 가능 항목

#### ? **Repository 레이어**
| Repository | 테스트 가능 메서드 | 우선순위 |
|-----------|------------------|---------|
| `SavedLottoNumberRepository` | `findByUserOrderByCreatedAtDesc()` | ??? 높음 |
| | `countByUser()` | ??? 높음 |
| | `findByIdAndUser()` | ?? 중간 |
| | `findByUserAndNum1AndNum2...()` | ? 낮음 |
| `UserRepository` | `findByUsername()` | ??? 높음 |
| | `findByEmail()` | ?? 중간 |
| | `existsByUsernameOrEmail()` | ?? 중간 |
| `LottoRepository` | `findByPostgame()` | ?? 중간 |
| `EmailVerificationTokenRepository` | `findByToken()` | ? 낮음 |

#### ? **Service + Repository 통합**
| Service | 통합 테스트 가능 여부 | 우선순위 |
|---------|---------------------|---------|
| `SavedLottoNumberService` | ? 가능 | ??? 높음 |
| `EmailService` | ?? 제한적 (이메일 발송은 Mock) | ?? 중간 |
| `CustomUserDetailsService` | ? 가능 | ?? 중간 |

### 3.2 API 테스트 가능 항목

#### ? **LottoApiController**
| 엔드포인트 | HTTP Method | 인증 필요 | 테스트 가능 | 우선순위 |
|-----------|------------|----------|-----------|---------|
| `/api/lotto/draw` | GET | ? | ? | ??? 높음 |
| `/api/lotto/latest` | GET | ? | ? | ??? 높음 |
| `/api/lotto/history` | GET | ? | ? | ?? 중간 |
| `/api/lotto/save` | POST | ? | ? | ??? 높음 |

#### ? **SavedLottoNumberController** (View Controller)
| 엔드포인트 | HTTP Method | 테스트 가능 | 우선순위 |
|-----------|------------|-----------|---------|
| `/saved-lotto` | GET | ?? 제한적 (View 반환) | ? 낮음 |

**참고:** View Controller는 API 테스트보다는 E2E 테스트가 적합합니다.

---

## 4. 테스트 작성 우선순위

### ? 1순위: API 테스트 (가장 중요)
```java
? LottoApiControllerTest
   - GET /api/lotto/draw 테스트
   - GET /api/lotto/latest 테스트
   - POST /api/lotto/save 테스트 (인증 포함)
```

**이유:**
- ? REST API는 외부 클라이언트와의 계약
- ? API 변경 시 즉시 감지 필요
- ? 빠른 실행 속도

### ? 2순위: Repository 통합 테스트
```java
? SavedLottoNumberRepositoryTest
   - 실제 DB 연동 테스트
   - JPA 쿼리 정확성 검증
```

**이유:**
- ? 데이터 무결성 검증
- ? 복잡한 쿼리 검증
- ? 실제 DB 동작 확인

### ? 3순위: Service + Repository 통합 테스트
```java
? SavedLottoNumberServiceIntegrationTest
   - Service와 Repository 함께 테스트
```

**이유:**
- ? 여러 레이어 간 상호작용 검증
- ? 트랜잭션 동작 확인

---

## 5. 테스트 환경 구성 필요 사항

### 5.1 의존성 확인

#### ? 이미 포함됨
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
- JUnit 5 ?
- Mockito ?
- AssertJ ?
- MockMvc ?

#### ?? 추가 필요 (H2 DB 사용 시)
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### 5.2 테스트 설정 파일

#### `src/test/resources/application-test.properties`
```properties
# H2 인메모리 DB 설정
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 로깅
logging.level.org.hibernate.SQL=DEBUG
```

---

## 6. 테스트 작성 예시

### 6.1 Repository 통합 테스트 예시
```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SavedLottoNumberRepositoryTest {
    
    @Autowired
    private SavedLottoNumberRepository repository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUserOrderByCreatedAtDesc() {
        // Given: 사용자와 저장된 번호 생성
        User user = new User("testuser", "password", "test@example.com");
        userRepository.save(user);
        
        SavedLottoNumber saved1 = new SavedLottoNumber(user, 1,2,3,4,5,6,7);
        SavedLottoNumber saved2 = new SavedLottoNumber(user, 10,20,30,40,41,42,43);
        repository.save(saved1);
        repository.save(saved2);
        
        // When: 조회
        List<SavedLottoNumber> result = repository.findByUserOrderByCreatedAtDesc(user);
        
        // Then: 최신순 정렬 확인
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()));
    }
}
```

### 6.2 API 테스트 예시
```java
@WebMvcTest(LottoApiController.class)
class LottoApiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LottoDAO lottoDAO;
    
    @MockBean
    private SavedLottoNumberService savedLottoNumberService;
    
    @Test
    void testDrawLotto() throws Exception {
        // Given: Mock 데이터 설정
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("drawnNumbers", Arrays.asList(1,2,3,4,5,6));
        mockResult.put("bonusNumber", 7);
        when(lottoDAO.drawLottoNumbers()).thenReturn(mockResult);
        
        // When & Then: API 호출 및 검증
        mockMvc.perform(get("/api/lotto/draw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.drawnNumbers").isArray())
                .andExpect(jsonPath("$.data.bonusNumber").value(7));
    }
    
    @Test
    void testSaveLottoNumber_Unauthorized() throws Exception {
        // 인증 없이 저장 시도
        mockMvc.perform(post("/api/lotto/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numbers\":[1,2,3,4,5,6],\"bonusNumber\":7}"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void testSaveLottoNumber_Success() throws Exception {
        // 인증된 사용자로 저장
        SavedLottoNumber saved = new SavedLottoNumber(...);
        when(savedLottoNumberService.saveLottoNumber(any(), any(), anyInt()))
                .thenReturn(saved);
        
        mockMvc.perform(post("/api/lotto/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numbers\":[1,2,3,4,5,6],\"bonusNumber\":7}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
```

---

## 7. 결론 및 권장 사항

### ? **통합 테스트 가능 여부: YES**
- **이유:** JPA Repository 사용, 실제 DB 연동 가능
- **권장:** H2 인메모리 DB 사용

### ? **API 테스트 가능 여부: YES**
- **이유:** @RestController 존재, MockMvc 사용 가능
- **권장:** @WebMvcTest로 빠른 테스트

### ? **다음 단계 권장 순서**

1. **H2 DB 의존성 추가** (pom.xml)
2. **테스트 설정 파일 생성** (application-test.properties)
3. **API 테스트 작성** (LottoApiControllerTest)
4. **Repository 통합 테스트 작성** (SavedLottoNumberRepositoryTest)

---

## 8. 참고사항

### ?? 주의사항

1. **이메일 발송 테스트**
   - `EmailService`는 실제 이메일 발송하므로 Mock 사용 권장
   - `@MockBean`으로 EmailService를 Mock 처리

2. **Spring Security 테스트**
   - `@WithMockUser` 어노테이션 사용
   - 또는 `SecurityConfig`를 테스트용으로 별도 설정

3. **트랜잭션 관리**
   - `@Transactional` 사용 시 테스트 후 자동 롤백
   - 실제 DB 사용 시 `@DirtiesContext` 고려

---

## 9. 테스트 커버리지 목표

| 테스트 유형 | 목표 커버리지 | 현재 상태 |
|-----------|-------------|----------|
| 단위 테스트 | 70-80% | ? SavedLottoNumberService 완료 |
| 통합 테스트 | 50-60% | ? 미작성 |
| API 테스트 | 80-90% | ? 미작성 |

---

**결론: 현재 프로젝트는 통합 테스트와 API 테스트 모두 작성 가능합니다!** ?

