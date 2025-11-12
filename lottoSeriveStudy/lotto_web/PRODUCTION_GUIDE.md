# 프로덕션 환경 설정 가이드

프로덕션 환경에서 애플리케이션을 실행하기 위한 설정 가이드입니다.

## 프로덕션 설정 파일

`application-prod.properties` 파일이 프로덕션 환경 설정을 포함합니다.

## 주요 변경사항

### 1. JPA 설정 최적화

**개발 환경:**
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**프로덕션 환경:**
```properties
spring.jpa.hibernate.ddl-auto=validate  # 스키마 변경 금지
spring.jpa.show-sql=false              # SQL 출력 비활성화
```

**이유:**
- `validate`: 프로덕션에서는 스키마 변경을 방지하여 데이터 손실 방지
- `show-sql=false`: 성능 향상 및 보안 강화

### 2. 로깅 레벨 조정

**개발 환경:**
```properties
logging.level.root=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**프로덕션 환경:**
```properties
logging.level.root=INFO
logging.level.org.hibernate.SQL=WARN
```

**이유:**
- INFO 레벨 이상만 로깅하여 성능 향상
- 디버그 로그는 프로덕션에서 불필요

### 3. Connection Pool 최적화

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

**이유:**
- 데이터베이스 연결 풀 크기 최적화
- 동시 접속자 수에 맞게 조정

### 4. 성능 최적화

```properties
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

**이유:**
- Open Session In View 비활성화로 성능 향상
- 배치 처리로 데이터베이스 부하 감소

## 프로덕션 환경 실행 방법

### Docker Compose 사용

```bash
# 프로덕션 프로파일 활성화
SPRING_PROFILES_ACTIVE=prod docker compose up -d
```

또는 `docker-compose.yml`에 추가:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
```

### 환경 변수 설정

프로덕션 환경에서는 다음 환경 변수를 설정해야 합니다:

```bash
# 데이터베이스
DB_URL=jdbc:mariadb://your-db-host:3306/lotto
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_password

# 애플리케이션 URL
APP_URL=https://yourdomain.com

# 이메일
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# 서버 포트 (선택사항)
SERVER_PORT=8080
```

## 프로덕션 배포 체크리스트

- [ ] `application-prod.properties` 설정 확인
- [ ] 환경 변수 설정 완료
- [ ] 데이터베이스 백업 완료
- [ ] 로그 디렉토리 생성 (`/app/logs`)
- [ ] HTTPS 설정 (도메인 사용 시)
- [ ] 방화벽 규칙 설정
- [ ] 모니터링 설정
- [ ] 에러 알림 설정

## 로그 관리

프로덕션 환경에서는 로그 파일이 `/app/logs/application.log`에 저장됩니다.

```bash
# 로그 확인
docker compose exec app tail -f /app/logs/application.log

# 로그 디렉토리 볼륨 마운트 (선택사항)
# docker-compose.yml에 추가:
volumes:
  - ./logs:/app/logs
```

## 보안 권장사항

1. **환경 변수 사용**: 모든 민감한 정보는 환경 변수로 관리
2. **HTTPS 사용**: 프로덕션에서는 반드시 HTTPS 사용
3. **방화벽 설정**: 필요한 포트만 열기
4. **정기 업데이트**: 보안 패치 정기 적용
5. **로그 모니터링**: 비정상적인 접근 패턴 모니터링

## 성능 모니터링

프로덕션 환경에서는 다음을 모니터링해야 합니다:

- 애플리케이션 응답 시간
- 데이터베이스 쿼리 성능
- 메모리 사용량
- CPU 사용량
- 에러 발생률

## 문제 해결

### 스키마 변경 오류

프로덕션에서 `ddl-auto=validate`로 설정되어 있어 스키마 변경이 불가능합니다.
스키마 변경이 필요하면:
1. 마이그레이션 스크립트 작성
2. 데이터베이스에 직접 적용
3. 애플리케이션 재시작

### 로그 파일이 생성되지 않는 경우

```bash
# 로그 디렉토리 생성
docker compose exec app mkdir -p /app/logs
docker compose exec app chmod 755 /app/logs
```

또는 볼륨 마운트 사용 (권장)

