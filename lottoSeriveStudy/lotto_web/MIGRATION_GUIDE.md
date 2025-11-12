# 데이터 마이그레이션 가이드

기존 로컬 MariaDB의 데이터를 Docker MariaDB로 마이그레이션하는 방법입니다.

## 1단계: 기존 로컬 DB에서 데이터 덤프

```bash
# 로컬 MariaDB에서 데이터베이스 덤프
mysqldump -u root -p1234 lotto > lotto_backup.sql

# 또는 전체 데이터베이스 덤프 (권장)
mysqldump -u root -p1234 --databases lotto > lotto_backup.sql
```

**참고:**
- `-u root`: 사용자명
- `-p1234`: 비밀번호 (공백 없이)
- `lotto`: 데이터베이스 이름
- `lotto_backup.sql`: 덤프 파일 이름

## 2단계: Docker Compose로 MariaDB 실행

```bash
# 프로젝트 디렉토리로 이동
cd lottoSeriveStudy/lotto_web

# MariaDB만 먼저 실행 (app은 나중에)
docker-compose up -d db

# DB가 준비될 때까지 대기 (약 10-20초)
docker-compose logs -f db
# "ready for connections" 메시지가 나오면 Ctrl+C로 종료
```

## 3단계: Docker DB에 데이터 복원

```bash
# 덤프 파일을 Docker 컨테이너에 복원
docker exec -i lotto-db mysql -u root -p1234 lotto < lotto_backup.sql

# 또는 전체 데이터베이스 복원
docker exec -i lotto-db mysql -u root -p1234 < lotto_backup.sql
```

**참고:**
- `-i`: 표준 입력 사용
- `lotto-db`: 컨테이너 이름
- `mysql -u root -p1234`: MySQL/MariaDB 접속 명령
- `< lotto_backup.sql`: 덤프 파일 입력

## 4단계: 데이터 확인

```bash
# Docker DB에 접속
docker exec -it lotto-db mysql -u root -p1234 lotto

# MySQL 프롬프트에서:
SHOW TABLES;
SELECT COUNT(*) FROM users;  # 예시: users 테이블 확인
EXIT;
```

## 5단계: 전체 애플리케이션 실행

```bash
# 모든 서비스 실행 (DB + App)
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 상태 확인
docker-compose ps
```

## 문제 해결

### 덤프 파일이 없는 경우
```bash
# 로컬 MariaDB가 실행 중인지 확인
# Windows: 서비스 관리자에서 MariaDB 확인
# 또는 MySQL Workbench로 직접 접속하여 데이터 확인
```

### 복원 실패 시
```bash
# DB 컨테이너 재시작
docker-compose restart db

# 덤프 파일 형식 확인 (UTF-8 인코딩 확인)
# 파일이 너무 크면 압축 해제 필요
```

### 포트 충돌
- 로컬 MariaDB가 3306 포트 사용 중
- Docker MariaDB는 3307 포트 사용 (docker-compose.yml 설정)
- 충돌 없음

## 완료!

이제 완전히 독립적인 Docker 환경이 준비되었습니다:
- ? 로컬 DB와 완전히 분리
- ? 어디서든 `docker-compose up`으로 실행 가능
- ? 데이터는 Docker 볼륨에 영구 저장

