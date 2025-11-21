# ? 로또 번호 추천 서비스

Spring Boot 기반의 로또 번호 추천 웹 애플리케이션입니다. 과거 당첨 번호 데이터를 분석하여 가중치 기반 알고리즘으로 번호를 추천하며, REST API와 웹 인터페이스를 모두 제공하는 풀스택 애플리케이션입니다.

## ? 프로젝트 개요

- **개발 기간**: 2025년
- **개발 목적**: Spring Boot, JPA, Spring Security 등 백엔드 기술 스택 학습 및 실전 적용
- **주요 특징**: 
  - RESTful API 설계 및 구현
  - JUnit을 활용한 체계적인 테스트 코드 작성 (56개 테스트 케이스)
  - GitHub Actions를 활용한 CI/CD 파이프라인 구축
  - 자동 테스트 실행, 커버리지 리포트 생성, 코드 품질 검사
  - Docker를 활용한 컨테이너화 및 배포 자동화
  - Spring Security 기반 인증/인가 시스템
  - 이메일 인증을 통한 회원가입 검증

## ? 기술 스택

### Backend
- **Language**: Java 11
- **Framework**: Spring Boot 2.7.18
- **ORM**: JPA (Hibernate)
- **Database**: MariaDB
- **Security**: Spring Security (BCrypt 암호화)
- **Mail**: Spring Mail (Gmail SMTP)
- **Build Tool**: Maven

### Frontend
- **Template Engine**: Thymeleaf
- **CSS/JavaScript**: Vanilla JS

### DevOps & Testing
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Test Coverage**: JaCoCo
- **Code Quality**: Checkstyle
- **Test Database**: H2 (인메모리 DB)

## ? 주요 기능

### 1. 로또 번호 추천
- **가중치 기반 알고리즘**: 과거 당첨 번호 출현 빈도를 분석하여 가중치 계산
- **행운번호 기능**: 오늘 날짜 기반 시드로 생성되는 행운번호 포함 옵션
- **REST API 제공**: 모바일 앱 및 프론트엔드 연동 가능

### 2. 히스토리 조회 및 분석
- **페이징 처리**: 대용량 데이터 효율적 처리
- **필터링**: 회차 범위, 특정 번호 포함 여부로 필터링
- **통계 분석**: 각 번호의 출현 빈도, 가장 많이/적게 나온 번호 분석
- **정렬 기능**: 다양한 정렬 옵션 제공

### 3. 사용자 인증 시스템
- **회원가입/로그인**: Spring Security 기반 인증
- **이메일 인증**: 회원가입 시 이메일 인증 토큰 발송 및 검증
- **비밀번호 암호화**: BCrypt 해시 알고리즘 사용
- **세션 관리**: HTTP 세션 기반 인증 상태 관리

### 4. 저장된 번호 관리
- **번호 저장**: 사용자별 로또 번호 저장 기능
- **중복 검사**: 동일한 번호 조합 중복 저장 방지
- **번호 조회/삭제**: 저장된 번호 목록 조회 및 삭제

## ? 아키텍처

### 계층 구조
```
Controller Layer (REST API + Web)
    ↓
Service Layer (비즈니스 로직)
    ↓
Repository Layer (JPA Repository)
    ↓
Database (MariaDB)
```

### 주요 설계 패턴
- **MVC 패턴**: Controller, Service, Repository 계층 분리
- **DTO 패턴**: Entity와 API 응답 분리
- **Repository 패턴**: 데이터 접근 로직 캡슐화
- **Dependency Injection**: Spring IoC 컨테이너 활용

## ? 테스트

### 테스트 전략
- **단위 테스트**: Service 계층 비즈니스 로직 검증 (Mockito 사용)
- **통합 테스트**: Controller 계층 API 엔드포인트 검증 (@WebMvcTest 사용)
- **Repository 테스트**: JPA Repository 동작 검증 (H2 인메모리 DB 사용)

### 테스트 실행
```bash
# 모든 테스트 실행
mvn test

# 특정 테스트 클래스만 실행
mvn test -Dtest=LottoApiControllerTest

# 테스트 커버리지 리포트 생성
mvn test jacoco:report

# 커버리지 리포트 확인 (생성 후)
# 브라우저에서 target/site/jacoco/index.html 열기
```

### 테스트 커버리지 확인
- **로컬**: `mvn test jacoco:report` 실행 후 `target/site/jacoco/index.html` 열기
- **CI/CD**: GitHub Actions Artifact에서 `test-coverage-report` 다운로드
- **커버리지 목표**: 현재 50% 이상 (JaCoCo 설정)

### 테스트 통계
- **총 테스트 케이스**: 56개
- **테스트 프레임워크**: JUnit 5, Mockito, Spring Boot Test
- **테스트 패턴**: Given-When-Then 패턴 적용
- **모킹**: Mockito를 활용한 의존성 격리

### 주요 테스트 시나리오
- ? 정상 케이스: 성공적인 API 호출 및 비즈니스 로직 실행
- ? 예외 케이스: 잘못된 입력값, 인증 실패, 데이터 없음 등
- ? 경계값 테스트: 최소/최대값, null 처리 등
- ? 보안 테스트: 인증되지 않은 요청, 권한 검사 등

## ? API 문서화 (Swagger/OpenAPI)

### Swagger UI 접속
애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 문서 (JSON)**: http://localhost:8080/v3/api-docs

### 주요 기능
- **인터랙티브 API 문서**: Swagger UI에서 직접 API를 테스트할 수 있습니다
- **자동 문서 생성**: 코드의 어노테이션을 기반으로 자동으로 문서가 생성됩니다
- **요청/응답 예시**: 각 API의 요청 파라미터와 응답 형식을 확인할 수 있습니다
- **인증 테스트**: 로그인 후 세션 쿠키를 사용하여 인증이 필요한 API를 테스트할 수 있습니다

### 사용 방법
1. 애플리케이션을 실행합니다 (`mvn spring-boot:run`)
2. 브라우저에서 `http://localhost:8080/swagger-ui.html` 접속
3. 원하는 API를 선택하고 "Try it out" 버튼 클릭
4. 파라미터를 입력하고 "Execute" 버튼 클릭하여 API 테스트

## ? REST API 문서

### 로또 번호 추첨
```http
GET /api/lotto/draw?useLucky=true
```
- **설명**: 가중치 기반 알고리즘으로 로또 번호 추첨
- **파라미터**: `useLucky` (선택) - 행운번호 포함 여부
- **응답**: 추첨된 번호 6개 + 보너스 번호

### 최신 로또 번호 조회
```http
GET /api/lotto/latest
```
- **설명**: 가장 최근 회차의 당첨 번호 조회
- **응답**: 회차 정보, 당첨 번호, 보너스 번호, 당첨자 수 등

### 로또 히스토리 조회
```http
GET /api/lotto/history?page=1&size=20&start=1000&end=2000&number=7
```
- **설명**: 과거 당첨 번호 히스토리 조회 (페이징, 필터링, 통계 포함)
- **파라미터**: 
  - `page`: 페이지 번호 (기본값: 1)
  - `size`: 페이지당 데이터 수 (기본값: 20)
  - `start`: 시작 회차 (선택)
  - `end`: 종료 회차 (선택)
  - `number`: 포함할 번호 (선택)
- **응답**: 히스토리 리스트, 페이징 정보, 필터 정보, 통계 정보

### 로또 번호 저장 (인증 필요)
```http
POST /api/lotto/save
Content-Type: application/json
Authorization: (세션 쿠키)

{
  "numbers": [1, 2, 3, 4, 5, 6],
  "bonusNumber": 7
}
```
- **설명**: 사용자의 로또 번호 저장
- **인증**: 로그인 필수
- **응답**: 저장된 번호 정보

## ? 설치 및 실행

### 요구사항
- Java 11 이상
- Maven 3.6 이상
- MariaDB (또는 MySQL) 10.3 이상
- Docker (선택사항)

### 로컬 환경 실행

#### 1. 데이터베이스 설정
```sql
CREATE DATABASE lotto CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 설정 파일 구성
`src/main/resources/application.properties` 파일을 생성하고 다음 내용을 설정:

```properties
# 데이터베이스 연결
spring.datasource.url=jdbc:mariadb://localhost:3306/lotto?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA 설정
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# 이메일 설정 (선택사항)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### 3. 빌드 및 실행
```bash
# Maven 빌드
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

애플리케이션이 실행되면 `http://localhost:8080`에서 접속할 수 있습니다.

### Docker를 사용한 실행

#### Docker Compose 사용 (권장)
```bash
# Docker Compose로 애플리케이션 및 데이터베이스 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 중지
docker-compose down
```

#### Dockerfile 직접 사용
```bash
# 이미지 빌드
docker build -t lotto-web:1.0.0 .

# 컨테이너 실행
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/lotto \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  lotto-web:1.0.0
```

## ? 프로젝트 구조

```
lotto_web/
├── src/
│   ├── main/
│   │   ├── java/com/lottoweb/
│   │   │   ├── config/          # 설정 클래스 (Security, etc.)
│   │   │   ├── controller/      # 컨트롤러 (REST API + Web)
│   │   │   ├── dao/             # 데이터 접근 객체
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   ├── model/           # 엔티티 모델
│   │   │   ├── repository/      # JPA Repository
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   └── util/            # 유틸리티 클래스
│   │   └── resources/
│   │       ├── application.properties      # 설정 파일
│   │       ├── application-docker.properties
│   │       ├── application-prod.properties
│   │       └── templates/       # Thymeleaf 템플릿
│   └── test/
│       ├── java/com/lottoweb/
│       │   ├── controller/      # Controller 테스트
│       │   ├── repository/      # Repository 테스트
│       │   └── service/         # Service 테스트
│       └── resources/
│           └── application-test.properties  # 테스트 설정
├── Dockerfile                   # Docker 이미지 빌드 파일
├── Dockerfile.ci                # CI/CD용 Dockerfile
├── docker-compose.yml           # Docker Compose 설정
├── pom.xml                      # Maven 의존성 관리
└── README.md                    # 프로젝트 문서
```

## ? 보안 기능

- **Spring Security**: 사용자 인증/인가 시스템
- **BCrypt 암호화**: 비밀번호 단방향 해시 암호화
- **이메일 인증**: 회원가입 시 이메일 인증 토큰 검증
- **세션 관리**: HTTP 세션 기반 인증 상태 관리
- **CSRF 보호**: REST API에서는 비활성화, 웹 폼에서는 활성화 가능

## ? 주요 엔티티

- **User**: 사용자 정보 (이름, 이메일, 비밀번호, 역할 등)
- **LottoNumber**: 로또 당첨 번호 (회차, 번호 6개, 보너스 번호, 당첨금 등)
- **SavedLottoNumber**: 사용자가 저장한 로또 번호
- **EmailVerificationToken**: 이메일 인증 토큰

## ? 기술적 도전 과제 및 해결

### 1. 테스트 코드 작성
- **도전**: Spring Security 설정으로 인한 ApplicationContext 로딩 실패
- **해결**: `@WebMvcTest`의 `excludeFilters`를 활용하여 SecurityConfig 제외
- **결과**: 56개 테스트 케이스 모두 통과

### 2. REST API 설계
- **도전**: 웹 페이지와 API를 동시에 제공하면서 코드 중복 최소화
- **해결**: Controller와 Service 계층 분리, DTO 패턴 적용
- **결과**: 재사용 가능한 비즈니스 로직, 일관된 API 응답 구조

### 3. 페이징 및 필터링
- **도전**: 대용량 데이터 효율적 처리
- **해결**: JPA Repository의 Pageable 활용, 동적 쿼리 구현
- **결과**: 빠른 응답 시간, 확장 가능한 구조

### 4. Docker 컨테이너화
- **도전**: 멀티 스테이지 빌드로 이미지 크기 최적화
- **해결**: Builder 패턴과 Alpine Linux 활용
- **결과**: 이미지 크기 29% 감소 (461MB → 326MB)

## ? 성능 최적화

- **JPA 쿼리 최적화**: N+1 문제 방지, 필요한 데이터만 조회
- **페이징 처리**: 대용량 데이터를 페이지 단위로 분할 처리
- **인덱스 활용**: 데이터베이스 인덱스 설계
- **Docker 이미지 최적화**: 멀티 스테이지 빌드로 이미지 크기 감소

## ? CI/CD 파이프라인

### GitHub Actions 자동화
- **자동 테스트 실행**: 코드 푸시 시 자동으로 모든 테스트 실행
- **테스트 커버리지 측정**: JaCoCo를 활용한 커버리지 리포트 자동 생성
- **코드 품질 검사**: Checkstyle을 활용한 코드 스타일 검사
- **Docker 이미지 빌드**: 테스트 통과 후 자동으로 Docker 이미지 빌드 및 푸시

### CI/CD 워크플로우
1. 코드 푸시 → GitHub Actions 트리거
2. 테스트 실행 → JUnit 테스트 자동 실행
3. 커버리지 리포트 생성 → JaCoCo 리포트 생성 및 Artifact 업로드
4. 코드 품질 검사 → Checkstyle 검사 실행
5. JAR 빌드 → Maven으로 JAR 파일 생성
6. Docker 빌드 → Docker 이미지 빌드 및 Docker Hub 푸시

### 리포트 확인 방법
- **테스트 커버리지**: GitHub Actions Artifact에서 `test-coverage-report` 다운로드
- **코드 품질**: GitHub Actions Artifact에서 `checkstyle-report` 다운로드
- **테스트 결과**: GitHub Actions 로그에서 확인

## ? 향후 개선 계획

- [ ] 테스트 커버리지 80% 이상 달성
- [ ] JWT 토큰 기반 인증 추가
- [ ] Redis를 활용한 세션 관리
- [x] API 문서화 (Swagger/OpenAPI) ?
- [x] CI/CD 파이프라인 구축 (GitHub Actions) ?
- [ ] 모니터링 및 로깅 시스템 구축

## ? 라이선스

이 프로젝트는 개인 학습 목적으로 제작되었습니다.

## ? 개발자

개발 관련 문의사항이 있으시면 이슈를 등록해주세요.

---

**? 이 프로젝트가 도움이 되었다면 Star를 눌러주세요!**
