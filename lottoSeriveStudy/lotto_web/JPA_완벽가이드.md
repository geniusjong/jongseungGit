# ? JPA 완벽 가이드 - 현재 프로젝트와 비교

## ? JPA란 무엇인가?

### **정의**
**JPA (Java Persistence API)**는 Java에서 관계형 데이터베이스를 사용할 때, 객체와 관계형 데이터베이스 사이의 패러다임 불일치를 해결하기 위한 **자바 ORM (Object-Relational Mapping) 표준 기술**입니다.

### **핵심 개념**
- **ORM (Object-Relational Mapping)**: 객체와 관계형 데이터베이스의 테이블을 자동으로 매핑해주는 기술
- **표준 기술**: JPA는 자바의 표준 스펙이므로, 구현체를 바꿔도 코드를 거의 수정하지 않아도 됨
- **추상화**: SQL 쿼리를 직접 작성하지 않고, 객체를 다루는 것처럼 데이터베이스를 사용

---

## ? 현재 프로젝트 vs JPA 비교

### **현재 프로젝트의 방식 (JDBC + JdbcTemplate)**

#### **1. 데이터 조회 코드**

```java
// 현재 LottoDAO.java의 방식
public LottoNumber getLottoNumber() {
    String sql = "SELECT * FROM tb_lotto_number ORDER BY postgame DESC LIMIT 1";
    List<LottoNumber> results = jdbcTemplate.query(sql, LOTTO_NUMBER_ROW_MAPPER);
    return results.isEmpty() ? null : results.get(0);
}

// RowMapper를 수동으로 작성해야 함
private static final RowMapper<LottoNumber> LOTTO_NUMBER_ROW_MAPPER = new RowMapper<LottoNumber>() {
    @Override
    public LottoNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
        LottoNumber n = new LottoNumber();
        n.setPostgame(rs.getInt("postgame"));
        n.setNum1(rs.getInt("num1"));
        n.setNum2(rs.getInt("num2"));
        // ... 10개 필드를 모두 수동으로 매핑
        return n;
    }
};
```

**문제점:**
- ? SQL을 직접 작성해야 함
- ? ResultSet에서 객체로 변환하는 코드를 매번 작성해야 함
- ? 필드가 많을수록 코드가 복잡해짐
- ? 데이터베이스 변경 시 SQL을 모두 수정해야 함

---

#### **2. JPA를 사용한다면?**

```java
// JPA Entity 클래스
@Entity
@Table(name = "tb_lotto_number")
public class LottoNumber {
    @Id
    private int postgame;
    
    private int num1;
    private int num2;
    private int num3;
    private int num4;
    private int num5;
    private int num6;
    
    @Column(name = "bonusnum")
    private int bonusnum;
    
    private long firstprize;
    private long firstprizecount;
    
    // Getter, Setter...
}

// Repository 인터페이스
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    // 최신 로또 번호 조회
    LottoNumber findFirstByOrderByPostgameDesc();
    
    // 회차로 조회
    Optional<LottoNumber> findByPostgame(int postgame);
    
    // 복잡한 쿼리도 메서드 이름으로 자동 생성
    List<LottoNumber> findByPostgameBetween(int start, int end);
}

// 사용하는 곳
@Service
public class LottoService {
    @Autowired
    private LottoRepository lottoRepository;
    
    public LottoNumber getLottoNumber() {
        return lottoRepository.findFirstByOrderByPostgameDesc();
    }
}
```

**장점:**
- ? SQL을 작성하지 않아도 됨
- ? ResultSet 매핑 코드가 필요 없음
- ? 메서드 이름만으로 쿼리 자동 생성
- ? 타입 안정성 보장

---

## ? JPA의 핵심 구성 요소

### **1. Entity (엔티티)**
데이터베이스의 테이블과 매핑되는 Java 클래스

```java
@Entity  // JPA가 관리하는 엔티티임을 표시
@Table(name = "tb_lotto_number")  // 테이블 이름 지정
public class LottoNumber {
    @Id  // 기본키 지정
    private int postgame;
    
    @Column(name = "bonusnum")  // 컬럼 이름 지정 (필요시)
    private int bonusnum;
    
    // 나머지 필드들은 자동으로 매핑
}
```

### **2. Repository (리포지토리)**
데이터베이스 접근을 담당하는 인터페이스

```java
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    // JpaRepository<엔티티타입, 기본키타입>
    // 기본 CRUD 메서드가 자동으로 제공됨!
}
```

**자동 제공되는 메서드:**
- `save(entity)` - 저장/수정
- `findById(id)` - ID로 조회
- `findAll()` - 전체 조회
- `deleteById(id)` - 삭제
- `count()` - 개수 조회
- 등등...

### **3. 영속성 컨텍스트 (Persistence Context)**
JPA가 엔티티를 관리하는 공간으로, **1차 캐시** 역할을 함

```java
// 같은 ID로 두 번 조회해도 실제 DB에는 한 번만 쿼리
LottoNumber lotto1 = repository.findById(1000);
LottoNumber lotto2 = repository.findById(1000);
// lotto1 == lotto2 (같은 객체 참조)
```

---

## ? JPA를 사용하면 좋은 이유

### **1. 생산성 향상 ???**

#### **현재 방식 (JDBC)**
```java
// 저장
public void save(LottoNumber lotto) {
    String sql = "INSERT INTO tb_lotto_number (postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, 
        lotto.getPostgame(), 
        lotto.getNum1(), 
        lotto.getNum2(),
        // ... 10개 필드를 모두 나열
    );
}
```

#### **JPA 방식**
```java
// 저장
lottoRepository.save(lotto);
// 끝! 모든 필드가 자동으로 저장됨
```

**코드 라인 수 비교:**
- JDBC: 약 20-30줄
- JPA: 1줄

---

### **2. 유지보수성 향상 ???**

#### **현재 방식의 문제점**
```java
// 필드가 추가되면?
public class LottoNumber {
    private int postgame;
    private int num1;
    // ... 기존 필드들
    private String newField;  // 새 필드 추가
}

// 모든 SQL과 RowMapper를 수정해야 함!
// 1. INSERT SQL 수정
// 2. UPDATE SQL 수정
// 3. SELECT SQL 수정
// 4. RowMapper 수정
// 총 4곳 이상 수정 필요
```

#### **JPA 방식**
```java
// 필드만 추가하면 끝!
@Entity
public class LottoNumber {
    // ... 기존 필드들
    private String newField;  // 새 필드만 추가
}

// SQL 수정 불필요! JPA가 자동으로 처리
```

---

### **3. 타입 안정성 ???**

#### **현재 방식 (런타임 에러 발생 가능)**
```java
// SQL 오타를 발견하기 어려움
String sql = "SELECT * FROM tb_lotto_number WHERE postgame = ?";
// 테이블 이름이 바뀌면? → 컴파일 타임에 에러 안 남
// 컬럼 이름 오타? → 런타임에만 발견
```

#### **JPA 방식 (컴파일 타임 에러)**
```java
// 타입 안정성 보장
LottoNumber lotto = repository.findById(1000);  // 컴파일 타임에 타입 체크
lotto.getPostgame();  // IDE가 자동완성 제공, 오타 방지
```

---

### **4. 객체 지향적 코드 작성 ??**

#### **현재 방식 (절차적)**
```java
// SQL 중심의 코드
String sql = "SELECT * FROM tb_lotto_number WHERE postgame = ?";
LottoNumber lotto = jdbcTemplate.queryForObject(sql, rowMapper, postgame);
// 객체를 다루는 게 아니라 SQL을 다루는 느낌
```

#### **JPA 방식 (객체 지향적)**
```java
// 객체 중심의 코드
LottoNumber lotto = repository.findByPostgame(postgame);
// 객체를 직접 다루는 느낌 - 더 직관적
```

---

### **5. 데이터베이스 독립성 ??**

#### **현재 방식**
```java
// MySQL 문법
String sql = "SELECT * FROM tb_lotto_number LIMIT 1";

// PostgreSQL로 바꾸면?
String sql = "SELECT * FROM tb_lotto_number FETCH FIRST 1 ROW ONLY";
// SQL을 모두 수정해야 함!
```

#### **JPA 방식**
```java
// 데이터베이스와 무관하게 동일한 코드
LottoNumber lotto = repository.findFirstByOrderByPostgameDesc();
// MySQL이든 PostgreSQL이든 코드는 동일!
// JPA가 데이터베이스 방언(Dialect)을 자동으로 처리
```

---

### **6. 복잡한 쿼리도 간단하게 ??**

#### **현재 방식 (복잡한 쿼리)**
```java
// 현재 LottoDAO.java의 방식
public List<LottoNumber> getLottoHistory(Integer startPostgame, Integer endPostgame, Integer includeNumber, int offset, int limit, String sortCol, String sortDir) {
    StringBuilder sb = new StringBuilder(
        "SELECT postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount FROM tb_lotto_number WHERE 1=1");
    List<Object> params = new ArrayList<>();
    
    if (startPostgame != null) {
        sb.append(" AND postgame >= ?");
        params.add(startPostgame);
    }
    if (endPostgame != null) {
        sb.append(" AND postgame <= ?");
        params.add(endPostgame);
    }
    if (includeNumber != null) {
        sb.append(" AND (num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?)");
        for (int i = 0; i < 7; i++) params.add(includeNumber);
    }
    // ... 50줄 이상의 복잡한 코드
}
```

#### **JPA 방식 (간단한 쿼리)**
```java
// 방법 1: 쿼리 메서드
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    List<LottoNumber> findByPostgameBetweenAndPostgameOrderByPostgameDesc(
        int start, int end, int includeNumber);
}

// 방법 2: @Query 어노테이션 (복잡한 쿼리)
@Query("SELECT l FROM LottoNumber l WHERE " +
       "(:startPostgame IS NULL OR l.postgame >= :startPostgame) AND " +
       "(:endPostgame IS NULL OR l.postgame <= :endPostgame) AND " +
       "(:includeNumber IS NULL OR l.num1 = :includeNumber OR l.num2 = :includeNumber)")
List<LottoNumber> findWithFilters(
    @Param("startPostgame") Integer startPostgame,
    @Param("endPostgame") Integer endPostgame,
    @Param("includeNumber") Integer includeNumber);
```

**장점:**
- ? SQL 문자열 연결 로직 불필요
- ? 파라미터 바인딩이 자동으로 안전하게 처리
- ? JPQL은 객체 중심 쿼리 (타입 안정성)

---

### **7. 자동 쿼리 최적화 ?**

#### **JPA의 지연 로딩 (Lazy Loading)**
```java
// 필요할 때만 데이터를 가져옴
LottoNumber lotto = repository.findById(1000);
// 아직 실제 DB 조회 안 함

int postgame = lotto.getPostgame();  // 이때 DB 조회!
```

#### **JPA의 N+1 문제 해결**
```java
// 현재 방식: N+1 문제 발생 가능
List<LottoNumber> list = repository.findAll();  // 1번 쿼리
for (LottoNumber lotto : list) {
    // 각각의 lotto에 대해 추가 쿼리 발생 가능
}

// JPA: 페치 조인으로 해결
@Query("SELECT l FROM LottoNumber l JOIN FETCH l.relatedData")
List<LottoNumber> findAllWithRelated();
```

---

### **8. 트랜잭션 관리 ??**

#### **현재 방식**
```java
// 트랜잭션을 수동으로 관리
@Transactional
public void saveMultiple(List<LottoNumber> numbers) {
    for (LottoNumber number : numbers) {
        jdbcTemplate.update(sql, ...);  // 각각 수동으로 저장
    }
}
```

#### **JPA 방식**
```java
// 트랜잭션이 자동으로 관리됨
@Transactional
public void saveMultiple(List<LottoNumber> numbers) {
    lottoRepository.saveAll(numbers);  // 한 번에 저장, 자동 트랜잭션
}
```

---

## ? 실제 코드 비교 예시

### **예시 1: 최신 로또 번호 조회**

#### **현재 방식 (JDBC)**
```java
// 1. RowMapper 정의 (20줄)
private static final RowMapper<LottoNumber> LOTTO_NUMBER_ROW_MAPPER = 
    new RowMapper<LottoNumber>() {
        @Override
        public LottoNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
            LottoNumber n = new LottoNumber();
            n.setPostgame(rs.getInt("postgame"));
            n.setNum1(rs.getInt("num1"));
            // ... 10개 필드 매핑
            return n;
        }
    };

// 2. SQL 작성 및 실행 (3줄)
public LottoNumber getLottoNumber() {
    String sql = "SELECT * FROM tb_lotto_number ORDER BY postgame DESC LIMIT 1";
    List<LottoNumber> results = jdbcTemplate.query(sql, LOTTO_NUMBER_ROW_MAPPER);
    return results.isEmpty() ? null : results.get(0);
}
```
**총 코드: 약 25줄**

#### **JPA 방식**
```java
// 1. Entity 클래스에 @Entity만 추가 (1줄)
@Entity
public class LottoNumber { ... }

// 2. Repository 인터페이스 (2줄)
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    LottoNumber findFirstByOrderByPostgameDesc();
}

// 3. 사용 (1줄)
public LottoNumber getLottoNumber() {
    return lottoRepository.findFirstByOrderByPostgameDesc();
}
```
**총 코드: 약 4줄 (85% 감소!)**

---

### **예시 2: 데이터 저장**

#### **현재 방식 (JDBC)**
```java
public void save(LottoNumber lotto) {
    String sql = "INSERT INTO tb_lotto_number " +
                 "(postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql,
        lotto.getPostgame(),
        lotto.getNum1(),
        lotto.getNum2(),
        lotto.getNum3(),
        lotto.getNum4(),
        lotto.getNum5(),
        lotto.getNum6(),
        lotto.getBonusnum(),
        lotto.getFirstprize(),
        lotto.getFirstprizecount()
    );
}
```
**총 코드: 약 15줄**

#### **JPA 방식**
```java
public void save(LottoNumber lotto) {
    lottoRepository.save(lotto);
}
```
**총 코드: 1줄 (93% 감소!)**

---

### **예시 3: 복잡한 조건 조회**

#### **현재 방식 (JDBC)**
```java
// 현재 LottoDAO.java의 실제 코드 (약 50줄)
public List<LottoNumber> getLottoHistory(
    Integer startPostgame, Integer endPostgame, Integer includeNumber, 
    int offset, int limit, String sortCol, String sortDir) {
    
    StringBuilder sb = new StringBuilder(
        "SELECT postgame, num1, num2, num3, num4, num5, num6, bonusnum, firstprize, firstprizecount FROM tb_lotto_number WHERE 1=1");
    List<Object> params = new ArrayList<>();
    
    if (startPostgame != null) {
        sb.append(" AND postgame >= ?");
        params.add(startPostgame);
    }
    if (endPostgame != null) {
        sb.append(" AND postgame <= ?");
        params.add(endPostgame);
    }
    if (includeNumber != null) {
        sb.append(" AND (num1=? OR num2=? OR num3=? OR num4=? OR num5=? OR num6=? OR bonusnum=?)");
        for (int i = 0; i < 7; i++) params.add(includeNumber);
    }
    
    String orderBy = resolveOrderBy(sortCol, sortDir);
    sb.append(" ").append(orderBy).append(" LIMIT ? OFFSET ?");
    params.add(limit);
    params.add(offset);
    
    return jdbcTemplate.query(sb.toString(), params.toArray(), LOTTO_NUMBER_ROW_MAPPER);
}
```
**총 코드: 약 50줄 + 헬퍼 메서드**

#### **JPA 방식**
```java
// 방법 1: 쿼리 메서드 (간단한 경우)
public interface LottoRepository extends JpaRepository<LottoNumber, Integer> {
    Page<LottoNumber> findByPostgameBetween(
        int start, int end, Pageable pageable);
}

// 방법 2: @Query (복잡한 경우)
@Query("SELECT l FROM LottoNumber l WHERE " +
       "(:startPostgame IS NULL OR l.postgame >= :startPostgame) AND " +
       "(:endPostgame IS NULL OR l.postgame <= :endPostgame) AND " +
       "(:includeNumber IS NULL OR " +
       "l.num1 = :includeNumber OR l.num2 = :includeNumber OR " +
       "l.num3 = :includeNumber OR l.num4 = :includeNumber OR " +
       "l.num5 = :includeNumber OR l.num6 = :includeNumber OR " +
       "l.bonusnum = :includeNumber) " +
       "ORDER BY CASE WHEN :sortCol = 'postgame' THEN l.postgame END " +
       "DESC, CASE WHEN :sortCol = 'firstprize' THEN l.firstprize END DESC")
Page<LottoNumber> findWithFilters(
    @Param("startPostgame") Integer startPostgame,
    @Param("endPostgame") Integer endPostgame,
    @Param("includeNumber") Integer includeNumber,
    @Param("sortCol") String sortCol,
    Pageable pageable);
```
**총 코드: 약 10줄 (80% 감소!)**

---

## ? JPA의 핵심 개념 정리

### **1. Entity Manager (엔티티 매니저)**
JPA의 핵심 인터페이스로, 엔티티를 저장/조회/수정/삭제하는 역할

```java
// Spring Data JPA에서는 자동으로 관리됨
// 직접 사용할 필요는 거의 없음
```

### **2. 영속성 컨텍스트 (Persistence Context)**
엔티티를 영구 저장하는 환경으로, **1차 캐시** 역할

```java
// 같은 ID로 조회하면 캐시에서 가져옴
LottoNumber lotto1 = repository.findById(1000);  // DB 조회
LottoNumber lotto2 = repository.findById(1000);  // 캐시에서 가져옴 (DB 조회 안 함)
```

### **3. 엔티티 생명주기**
- **비영속 (Transient)**: 영속성 컨텍스트와 관계 없음
- **영속 (Persistent)**: 영속성 컨텍스트에 저장됨
- **준영속 (Detached)**: 영속성 컨텍스트에서 분리됨
- **삭제 (Removed)**: 삭제 예정

### **4. 지연 로딩 (Lazy Loading) vs 즉시 로딩 (Eager Loading)**
```java
// 지연 로딩: 필요할 때만 데이터를 가져옴
@OneToMany(fetch = FetchType.LAZY)
private List<RelatedData> relatedData;

// 즉시 로딩: 연관된 데이터를 바로 가져옴
@OneToMany(fetch = FetchType.EAGER)
private List<RelatedData> relatedData;
```

---

## ?? JPA 사용 시 주의사항

### **1. N+1 문제**
```java
// 문제 코드
List<LottoNumber> list = repository.findAll();
for (LottoNumber lotto : list) {
    lotto.getRelatedData();  // 각각 추가 쿼리 발생
}

// 해결 방법: 페치 조인
@Query("SELECT l FROM LottoNumber l JOIN FETCH l.relatedData")
List<LottoNumber> findAllWithRelated();
```

### **2. 성능 최적화**
```java
// 불필요한 데이터 조회 방지
@Query("SELECT l.postgame FROM LottoNumber l")  // 필요한 필드만 조회
List<Integer> findPostgames();
```

### **3. 트랜잭션 관리**
```java
// 트랜잭션 범위를 명확히 지정
@Transactional
public void saveMultiple(List<LottoNumber> numbers) {
    lottoRepository.saveAll(numbers);
}
```

---

## ? JPA vs MyBatis vs 현재 방식 (JDBC)

| 항목 | 현재 방식 (JDBC) | MyBatis | JPA |
|------|-----------------|---------|-----|
| **SQL 작성** | 필수 | 필수 (XML/어노테이션) | 선택 (자동 생성) |
| **객체 매핑** | 수동 (RowMapper) | 수동/자동 | 자동 |
| **코드 라인 수** | 많음 | 중간 | 적음 |
| **학습 곡선** | 낮음 | 중간 | 높음 |
| **유연성** | 매우 높음 | 높음 | 중간 |
| **표준 기술** | ? | ? | ? |
| **타입 안정성** | 낮음 | 중간 | 높음 |
| **데이터베이스 독립성** | 낮음 | 중간 | 높음 |

---

## ? 현재 프로젝트에 JPA 적용 시 예상 효과

### **코드 감소량**
- **DAO 클래스**: 현재 200줄 → 약 50줄 (75% 감소)
- **RowMapper**: 완전 제거
- **SQL 문자열**: 대부분 제거

### **개발 속도**
- **새로운 기능 추가**: 현재 2시간 → 약 30분 (75% 단축)
- **필드 추가**: 현재 4곳 수정 → 필드만 추가 (90% 단축)

### **유지보수성**
- **SQL 오타**: 런타임 에러 → 컴파일 타임 에러
- **타입 안정성**: 향상
- **IDE 지원**: 자동완성, 리팩토링 지원

---

## ? 결론: JPA를 사용해야 하는 이유

### **1. 생산성 ???**
- 코드 라인 수가 70-80% 감소
- 개발 시간 단축

### **2. 유지보수성 ???**
- 필드 추가/수정이 쉬움
- SQL 오타 방지
- 타입 안정성

### **3. 표준 기술 ??**
- 많은 회사에서 사용
- 이직 시 유리
- 커뮤니티 지원 풍부

### **4. 객체 지향적 코드 ??**
- 객체 중심 사고
- 비즈니스 로직에 집중

### **5. 데이터베이스 독립성 ?**
- MySQL → PostgreSQL 전환 시 코드 수정 최소화

---

## ? 다음 단계

현재 프로젝트에 JPA를 적용하려면:

1. **의존성 추가** (5분)
2. **Entity 클래스 생성** (30분)
3. **Repository 인터페이스 생성** (30분)
4. **DAO 코드 수정** (2-3시간)

**총 예상 시간: 하루~이틀**

준비되면 알려주세요! 실제 코드로 적용해드리겠습니다. ?

