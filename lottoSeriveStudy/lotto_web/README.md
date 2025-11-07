# 로또 번호 추천 서비스

Spring Boot 기반의 로또 번호 추천 웹 애플리케이션입니다.

## ? 기술 스택

- **Java**: 11
- **Framework**: Spring Boot 2.7.18
- **ORM**: JPA (Hibernate)
- **Database**: MariaDB
- **Template Engine**: Thymeleaf
- **Security**: Spring Security
- **Mail**: Spring Mail (Gmail SMTP)
- **Build Tool**: Maven

## ? 주요 기능

- ? **로또 번호 추천**: 가중치 기반 알고리즘으로 로또 번호 추천
- ? **히스토리 조회**: 과거 로또 당첨 번호 조회 및 분석
- ? **회원가입/로그인**: Spring Security 기반 사용자 인증
- ? **이메일 인증**: 회원가입 시 이메일 인증 기능
- ? **필터링 및 정렬**: 당첨 번호 히스토리 필터링 및 정렬 기능

## ? 요구사항

- Java 11 이상
- Maven 3.6 이상
- MariaDB (또는 MySQL)

## ? 설치 및 실행

### 1. 데이터베이스 설정

MariaDB 데이터베이스를 생성하고 `application.properties`에 연결 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/lotto?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
```

### 2. 이메일 설정 (선택사항)

Gmail SMTP 설정을 `application.properties`에 추가합니다.

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 3. 빌드 및 실행

```bash
# Maven 빌드
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

애플리케이션이 실행되면 `http://localhost:8080`에서 접속할 수 있습니다.

## ? 프로젝트 구조

```
lotto_web/
├── src/main/java/com/lottoweb/
│   ├── config/          # 설정 클래스 (Security, etc.)
│   ├── controller/      # 컨트롤러
│   ├── model/           # 엔티티 모델
│   ├── repository/      # JPA Repository
│   ├── service/         # 비즈니스 로직
│   └── util/            # 유틸리티 클래스
├── src/main/resources/
│   ├── application.properties  # 설정 파일
│   └── templates/       # Thymeleaf 템플릿
└── pom.xml
```

## ? 보안 기능

- Spring Security를 사용한 사용자 인증/인가
- BCrypt 암호화를 통한 비밀번호 보안
- 이메일 인증을 통한 회원가입 검증
- CSRF 보호 (개발 환경에서 비활성화 가능)

## ? 주요 엔티티

- **User**: 사용자 정보
- **LottoNumber**: 로또 당첨 번호
- **EmailVerificationToken**: 이메일 인증 토큰

## ? API 엔드포인트

- `GET /`: 메인 페이지
- `GET /login`: 로그인 페이지
- `POST /login`: 로그인 처리
- `GET /register`: 회원가입 페이지
- `POST /register`: 회원가입 처리
- `GET /verify-email`: 이메일 인증
- `GET /lotto`: 로또 번호 추천
- `GET /history`: 로또 히스토리 조회

## ? 라이선스

이 프로젝트는 개인 학습 목적으로 제작되었습니다.

## ??? 개발자

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

