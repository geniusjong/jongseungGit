# 배포 가이드

로또 번호 추천 서비스를 프로덕션 환경에 배포하는 방법을 안내합니다.

## 배포 옵션

### 옵션 1: VPS 서버 배포 (권장 - 가장 간단)

VPS 서버(AWS EC2, DigitalOcean, Linode, 네이버 클라우드 등)에 Docker Compose를 사용하여 배포합니다.

#### 1단계: 서버 준비

```bash
# 서버에 접속 후 Docker 및 Docker Compose 설치
# Ubuntu/Debian
sudo apt update
sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker

# Docker Compose v2 설치 (최신 버전)
sudo apt install -y docker-compose-plugin
```

#### 2단계: 프로젝트 파일 준비

```bash
# 서버에 프로젝트 디렉토리 생성
mkdir -p ~/lotto-web
cd ~/lotto-web

# docker-compose.yml 파일 생성
nano docker-compose.yml
```

#### 3단계: docker-compose.yml 설정

```yaml
version: '3.8'

services:
  # MariaDB 데이터베이스
  db:
    image: mariadb:10.11
    container_name: lotto-db
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: lotto
      MYSQL_USER: ${DB_USERNAME:-root}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - lotto-network

  # Spring Boot 애플리케이션
  app:
    image: jongseungpark/lotto-web:latest  # Docker Hub에서 이미지 pull
    container_name: lotto-app
    restart: always
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:mariadb://db:3306/lotto?useUnicode=true&characterEncoding=UTF-8
      - DB_USERNAME=${DB_USERNAME:-root}
      - DB_PASSWORD=${DB_PASSWORD}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
    volumes:
      - ./logs:/app/logs
    networks:
      - lotto-network

volumes:
  db_data:

networks:
  lotto-network:
    driver: bridge
```

#### 4단계: 환경 변수 설정

```bash
# .env 파일 생성
nano .env
```

`.env` 파일 내용:
```bash
# 데이터베이스 설정
DB_PASSWORD=your_secure_password_here
DB_USERNAME=root

# 이메일 설정 (Gmail 사용 시)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

**보안 주의사항:**
- `.env` 파일은 절대 Git에 커밋하지 마세요
- 강력한 비밀번호를 사용하세요
- Gmail 앱 비밀번호를 사용하세요 (일반 비밀번호가 아님)

#### 5단계: 배포 실행

```bash
# Docker Hub에서 최신 이미지 pull
docker pull jongseungpark/lotto-web:latest

# 애플리케이션 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 상태 확인
docker-compose ps
```

#### 6단계: 방화벽 설정

```bash
# Ubuntu/Debian
sudo ufw allow 8080/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

---

### 옵션 2: Nginx 리버스 프록시 설정 (HTTPS 포함)

도메인을 사용하고 HTTPS를 적용하려면 Nginx를 사용합니다.

#### 1단계: Nginx 설치

```bash
sudo apt install -y nginx
```

#### 2단계: Nginx 설정 파일 생성

```bash
sudo nano /etc/nginx/sites-available/lotto-web
```

설정 내용:
```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # HTTP를 HTTPS로 리다이렉트
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL 인증서 설정 (Let's Encrypt 사용)
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # SSL 보안 설정
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # 로그 설정
    access_log /var/log/nginx/lotto-access.log;
    error_log /var/log/nginx/lotto-error.log;

    # 프록시 설정
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 지원 (필요한 경우)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 파일 크기 제한
    client_max_body_size 10M;
}
```

#### 3단계: Let's Encrypt SSL 인증서 설치

```bash
# Certbot 설치
sudo apt install -y certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 자동 갱신 설정
sudo certbot renew --dry-run
```

#### 4단계: Nginx 활성화 및 재시작

```bash
# 심볼릭 링크 생성
sudo ln -s /etc/nginx/sites-available/lotto-web /etc/nginx/sites-enabled/

# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

### 옵션 3: 클라우드 서비스 배포

#### AWS 배포

**AWS ECS (Elastic Container Service) 사용:**

1. ECS 클러스터 생성
2. Docker Hub에서 이미지 pull
3. Task Definition 생성
4. Service 생성 및 실행

**AWS EC2 + Docker Compose 사용:**

옵션 1과 동일한 방법으로 EC2 인스턴스에 배포

#### Azure 배포

**Azure Container Instances (ACI) 사용:**

```bash
# Azure CLI 설치 후
az container create \
  --resource-group myResourceGroup \
  --name lotto-web \
  --image jongseungpark/lotto-web:latest \
  --dns-name-label lotto-web \
  --ports 8080 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    DB_URL=jdbc:mariadb://your-db:3306/lotto
```

#### Google Cloud Platform (GCP) 배포

**Cloud Run 사용:**

```bash
# gcloud CLI 설치 후
gcloud run deploy lotto-web \
  --image jongseungpark/lotto-web:latest \
  --platform managed \
  --region asia-northeast3 \
  --allow-unauthenticated \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod
```

---

## 배포 후 확인 사항

### 1. 애플리케이션 상태 확인

```bash
# 컨테이너 상태 확인
docker-compose ps

# 애플리케이션 로그 확인
docker-compose logs -f app

# 데이터베이스 연결 확인
docker-compose exec app ping db
```

### 2. 헬스 체크

```bash
# 애플리케이션 응답 확인
curl http://localhost:8080

# Swagger UI 확인
curl http://localhost:8080/swagger-ui.html

# API 테스트
curl http://localhost:8080/api/lotto/latest
```

### 3. 데이터베이스 확인

```bash
# 데이터베이스 컨테이너 접속
docker-compose exec db mysql -u root -p

# 데이터베이스 확인
SHOW DATABASES;
USE lotto;
SHOW TABLES;
```

---

## 업데이트 배포

코드 변경 후 새로운 버전을 배포하는 방법:

### 방법 1: Docker Hub 이미지 업데이트

```bash
# 로컬에서 빌드 및 푸시 (GitHub Actions가 자동으로 처리)
# 또는 수동으로:
docker build -t jongseungpark/lotto-web:latest .
docker push jongseungpark/lotto-web:latest

# 서버에서 최신 이미지 pull 및 재시작
cd ~/lotto-web
docker-compose pull app
docker-compose up -d app
```

### 방법 2: 로컬 빌드 후 배포

```bash
# 서버에서 직접 빌드
cd ~/lotto-web
git pull origin main
docker-compose build app
docker-compose up -d app
```

---

## 모니터링 및 로그 관리

### 로그 확인

```bash
# 실시간 로그 확인
docker-compose logs -f app

# 최근 100줄 로그 확인
docker-compose logs --tail=100 app

# 특정 시간 이후 로그
docker-compose logs --since 30m app
```

### 로그 파일 위치

- 애플리케이션 로그: `./logs/application.log` (볼륨 마운트된 경우)
- Nginx 로그: `/var/log/nginx/lotto-access.log`, `/var/log/nginx/lotto-error.log`

### 리소스 모니터링

```bash
# 컨테이너 리소스 사용량 확인
docker stats

# 디스크 사용량 확인
docker system df
```

---

## 문제 해결

### 애플리케이션이 시작되지 않는 경우

```bash
# 로그 확인
docker-compose logs app

# 컨테이너 재시작
docker-compose restart app

# 컨테이너 재생성
docker-compose up -d --force-recreate app
```

### 데이터베이스 연결 오류

```bash
# 데이터베이스 상태 확인
docker-compose ps db

# 데이터베이스 로그 확인
docker-compose logs db

# 데이터베이스 재시작
docker-compose restart db
```

### 포트 충돌

```bash
# 포트 사용 확인
sudo netstat -tulpn | grep 8080

# 다른 포트 사용 (docker-compose.yml 수정)
ports:
  - "8081:8080"  # 호스트 8081 포트 사용
```

---

## 보안 체크리스트

- [ ] 강력한 데이터베이스 비밀번호 설정
- [ ] 환경 변수 파일(.env) 권한 설정 (chmod 600)
- [ ] 방화벽 설정 완료
- [ ] HTTPS 설정 완료 (프로덕션)
- [ ] 정기적인 보안 업데이트
- [ ] 로그 모니터링 설정
- [ ] 백업 정책 수립

---

## 백업 방법

### 데이터베이스 백업

```bash
# 백업 생성
docker-compose exec db mysqldump -u root -p${DB_PASSWORD} lotto > backup_$(date +%Y%m%d_%H%M%S).sql

# 백업 복원
docker-compose exec -T db mysql -u root -p${DB_PASSWORD} lotto < backup_20250101_120000.sql
```

### 자동 백업 스크립트

```bash
#!/bin/bash
# backup.sh
BACKUP_DIR="/backup/lotto"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# 데이터베이스 백업
docker-compose exec -T db mysqldump -u root -p${DB_PASSWORD} lotto > $BACKUP_DIR/db_$DATE.sql

# 7일 이상 된 백업 삭제
find $BACKUP_DIR -name "db_*.sql" -mtime +7 -delete
```

Cron으로 자동 실행:
```bash
# 매일 새벽 2시에 백업
0 2 * * * /path/to/backup.sh
```

---

## 성능 최적화

### JVM 옵션 추가

`docker-compose.yml`에 추가:
```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
```

### 데이터베이스 인덱스 최적화

```sql
-- 자주 조회되는 컬럼에 인덱스 추가
CREATE INDEX idx_postgame ON lotto_number(postgame);
CREATE INDEX idx_user_id ON tb_saved_lotto_number(user_id);
```

---

이 가이드를 따라 배포하면 프로덕션 환경에서 안정적으로 서비스를 운영할 수 있습니다!

