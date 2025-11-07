# ? JPA 적용 완료 - 단계별 작업 요약

## ? 완료된 작업

### ? Step 1: pom.xml에 JPA 의존성 추가
```xml
<!-- Spring Boot JPA (ORM) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### ? Step 2: application.properties에 JPA 설정 추가
- `src/main/resources/application.properties` 파일 생성
- 데이터베이스 연결 설정
- JPA 설정 (ddl-auto, show-sql 등)
- MariaDB 방언 설정

### ? Step 3: LottoNumber를 JPA Entity로 변환
- `src/main/java/com/lottoweb/model/LottoNumber.java` 생성
- `@Entity`, `@Table`, `@Id`, `@Column` 어노테이션 추가
- 기존 필드와 Getter/Setter 유지

### ? Step 4: LottoRepository 인터페이스 생성
- `src/main/java/com/lottoweb/repository/LottoRepository.java` 생성
- `JpaRepository<LottoNumber, Integer>` 상속
- 쿼리 메서드 정의 (findFirstByOrderByPostgameDesc 등)
- 복잡한 쿼리는 `@Query` 어노테이션으로 작성

### ? Step 5: DAO 코드를 Repository로 전환
- `LottoDAO`에 `LottoRepository` 주입
- 기존 JDBC 코드는 주석 처리하고 JPA 방식으로 전환
- 전환된 메서드:
  - `getLottoNumber()` - 최신 로또 번호 조회
  - `countLottoHistory()` - 전체 개수 조회
  - `getLottoHistory()` - 페이징 히스토리 조회
  - `countLottoHistory()` - 필터링된 개수 조회
  - `getLottoHistory()` - 필터링 + 페이징 조회

---

## ? 코드 변경 요약

### **Before (JDBC 방식)**
```java
// 25줄의 RowMapper
private static final RowMapper<LottoNumber> LOTTO_NUMBER_ROW_MAPPER = ...;

// 3줄의 SQL 쿼리
public LottoNumber getLottoNumber() {
    String sql = "SELECT * FROM tb_lotto_number ORDER BY postgame DESC LIMIT 1";
    List<LottoNumber> results = jdbcTemplate.query(sql, LOTTO_NUMBER_ROW_MAPPER);
    return results.isEmpty() ? null : results.get(0);
}
```

### **After (JPA 방식)**
```java
// Entity에 @Entity만 추가
@Entity
public class LottoNumber { ... }

// Repository 메서드 1줄
public LottoNumber getLottoNumber() {
    return lottoRepository.findFirstByOrderByPostgameDesc()
            .orElse(null);
}
```

**코드 감소량: 약 85%!**

---

## ? 다음 단계 (실행하기)

### **1. Maven 의존성 다운로드**
프로젝트를 새로고침하거나 Maven을 업데이트해야 합니다:

**IDE에서:**
- Maven 프로젝트 새로고침 (Refresh)
- 또는: `mvn clean install` 실행

**터미널에서:**
```bash
mvn clean install
```

### **2. 프로젝트 실행 및 테스트**
```bash
mvn spring-boot:run
```

또는 IDE에서 `LottoWebApplication`을 실행

### **3. 확인 사항**
- 애플리케이션이 정상적으로 시작되는지 확인
- 데이터베이스 연결이 정상인지 확인
- 콘솔에 SQL 쿼리가 출력되는지 확인 (show-sql=true 설정)

---

## ?? 주의사항

### **1. IDE 에러 해결**
현재 IDE에서 `javax.persistence` 관련 에러가 보일 수 있습니다. 이는 Maven 의존성이 아직 다운로드되지 않아서입니다.

**해결 방법:**
1. Maven 프로젝트 새로고침
2. `mvn clean install` 실행
3. IDE 재시작

### **2. 데이터베이스 연결 확인**
- MariaDB가 실행 중인지 확인
- `application.properties`의 데이터베이스 연결 정보 확인
- 테이블 `tb_lotto_number`가 존재하는지 확인

### **3. 기존 코드와의 호환성**
- 기존 JDBC 코드는 주석 처리했지만, 필요하면 다시 활성화 가능
- `LottoWeightCalculator` 관련 메서드는 아직 JDBC 사용 중
  - 필요하면 이것도 JPA로 전환 가능

---

## ? 코드 변경 상세

### **전환된 메서드 목록**

1. ? `getLottoNumber()` - JPA 전환 완료
2. ? `countLottoHistory()` - JPA 전환 완료
3. ? `getLottoHistory(int offset, int limit)` - JPA 전환 완료
4. ? `countLottoHistory(Integer, Integer, Integer)` - JPA 전환 완료
5. ? `getLottoHistory(Integer, Integer, Integer, int, int, String, String)` - JPA 전환 완료

### **아직 JDBC 사용 중인 메서드**

다음 메서드들은 복잡한 로직이 있어서 아직 JDBC를 사용 중입니다:
- `getAllLottoNumbers()` - 가중치 계산용
- `drawLottoNumbers()` - 로또 번호 추첨
- `drawLottoNumbersIncluding()` - 특정 번호 포함 추첨
- `countFrequencies()` - 번호 빈도 계산

필요하면 나중에 JPA로 전환할 수 있습니다.

---

## ? 학습 포인트

### **1. Entity 클래스**
- `@Entity`: JPA가 관리하는 엔티티임을 표시
- `@Table(name = "tb_lotto_number")`: 테이블 이름 지정
- `@Id`: 기본키 지정
- `@Column`: 컬럼 이름 지정 (필요시)

### **2. Repository 인터페이스**
- `JpaRepository<엔티티타입, 기본키타입>` 상속
- 기본 CRUD 메서드 자동 제공 (save, findById, findAll, delete 등)
- 쿼리 메서드: 메서드 이름만으로 쿼리 자동 생성
- `@Query`: 복잡한 쿼리는 JPQL로 작성

### **3. 페이징 (Pageable)**
- `PageRequest.of(page, size)`: 페이지 번호와 크기 지정
- `Page<T>`: 페이징 정보 포함
- `pageable.getContent()`: 실제 데이터 리스트 가져오기

---

## ? 다음에 할 수 있는 것

### **1. 나머지 메서드도 JPA로 전환**
- `getAllLottoNumbers()` 메서드도 JPA로 전환
- 가중치 계산 로직을 Repository로 옮기기

### **2. 테스트 코드 작성**
- Repository 테스트
- 통합 테스트 작성

### **3. 성능 최적화**
- N+1 문제 확인 및 해결
- 페치 조인 사용
- 쿼리 최적화

### **4. 추가 기능**
- 새로운 쿼리 메서드 추가
- 복잡한 통계 쿼리 작성

---

## ? 참고 자료

- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- JPA_완벽가이드.md 파일 참고
- 프로젝트_분석_및_학습로드맵.md 파일 참고

---

## ? 체크리스트

- [x] Step 1: pom.xml에 JPA 의존성 추가
- [x] Step 2: application.properties 생성 및 설정
- [x] Step 3: LottoNumber Entity 생성
- [x] Step 4: LottoRepository 인터페이스 생성
- [x] Step 5: DAO 코드를 Repository로 전환
- [ ] Maven 의존성 다운로드 확인
- [ ] 프로젝트 실행 및 테스트
- [ ] 데이터베이스 연결 확인
- [ ] SQL 쿼리 출력 확인

---

**축하합니다! ? JPA 적용이 완료되었습니다!**

이제 Maven을 새로고침하고 프로젝트를 실행해보세요!

